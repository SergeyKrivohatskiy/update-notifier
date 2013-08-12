package net.thumbtack.updateNotifierBackend.database;

import static net.thumbtack.updateNotifierBackend.updateChecker.CheckForUpdate.getNewHashCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.daos.ResourceDAO;
import net.thumbtack.updateNotifierBackend.database.daos.TagDAO;
import net.thumbtack.updateNotifierBackend.database.daos.ResourceTagDAO;
import net.thumbtack.updateNotifierBackend.database.daos.UserDAO;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.TagMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//REVU: You should use interfaces.
// Create dao interfaces and make current Dao's classes as implementation of them.
// All daos without implementation of base dao interface.
// Better to do this as BaseDao class with sqlSessionFactory. 
// All Daos should implement BaseDao interface.
// And you don't have to send mapper to dao class, they will use sqlSessionFactory.
public class DatabaseService {

	private static final Logger log = LoggerFactory
			.getLogger(DatabaseService.class);
	private static final SqlSessionFactory sqlSessionFactory;

	public static SqlSessionFactory getSqlsessionfactory() {
		return sqlSessionFactory;
	}
	
	//REVU: Better to use static method instead  of 'magic initialization'
	static {
		//REVU: use static final constant for config file.
		String resource = "mybatis-cfg.xml";
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			log.error("Great crash: exception on initialize database");
			// TODO Great crash should be here!
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	}

	public DatabaseService() {
	}
	
	public Long getUserIdByEmailOrAdd(String email) throws DatabaseException {
		log.trace("Get user email by id; email: {}", email);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Long userId = UserDAO.getIdOrAdd(
					session.getMapper(UserMapper.class), email);
			if (userId != null) {
				session.commit();
			} else {
				log.error("Database returns null - I don't know why :(");
				throw new DatabaseException();
			}
			return userId;
		} finally {
			session.close();
		}
	}

	public String getUserEmailById(Long id) throws DatabaseException {
		log.trace("get user email by id; id: {}", id);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			String email = UserDAO.getUserEmail(
					session.getMapper(UserMapper.class), id);
			if (email != null) {
				session.commit();
			} else {
				log.error("Database exception: user email is null");
				throw new DatabaseException("User email is null");
			}
			return email;
		} finally {
			session.close();
		}
	}

	public boolean addResource(long userId, Resource resource)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Add resource; user id: {}, resource: {}", userId,
					resource.toString());
		}
		SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
		boolean result = false;
		try {
			if (!UserDAO.exists(session.getMapper(UserMapper.class), userId)) {
				log.error("Database exception: can't add resource to nonexist user");
				throw new DatabaseException(
						"Database exception: can't add resource to nonexist user");
			}
			resource.setUserId(userId);

			Integer hash = getNewHashCode(resource);
			if (hash == null) {
				hash = 0;
			}
			resource.setHash(hash);

			Long id = ResourceDAO.add(session.getMapper(ResourceMapper.class),
					resource);
			if (id != null) {
				if (resource.getTags() != null) {
					for (Long tagId : resource.getTags()) {
						ResourceTagDAO.addRelation(
								session.getMapper(ResourceTagMapper.class), id,
								tagId);
					}
				}
				session.commit();
				result = true;
			}
		} finally {
			session.close();
		}
		return result;
	}

	public List<Resource> getResourcesByIdAndTags(Long userId, Long[] tagIds)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Get resources by id and tags; id: {}, tag ids: {}",
					userId, tagIds == null ? null : tagIds.toString());
		}
		SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
		try {
			if (!UserDAO.exists(session.getMapper(UserMapper.class), userId)) {
				log.debug("Database exception: can't get resources for nonexistent user");
				throw new DatabaseException(
						"Database exception: can't get resources for nonexistent user");
			}
			if (tagIds != null
					&& !TagDAO.exists(session.getMapper(TagMapper.class),
							tagIds)) {
				log.debug("Database exception: can't get resources for nonexistent tags");
				throw new DatabaseException(
						"Database exception: can't get resources for nonexistent tags");
			}
			List<Resource> resources = ResourceDAO.getByUserIdAndTags(
					session.getMapper(ResourceMapper.class), userId, tagIds);
			for (Resource resource : resources) {
				resource.setTags(ResourceTagDAO.getForResource(
						session.getMapper(ResourceTagMapper.class),
						resource.getId()));
			}
			return resources;
		} finally {
			session.close();
		}
	}

	public Resource getResource(long userId, long resourceId) {
		log.trace("Get resource; user id = {}, resource id = {}", userId,
				resourceId);
		SqlSession session = sqlSessionFactory.openSession();
		Resource result = null;
		try {
			result = ResourceDAO.get(session.getMapper(ResourceMapper.class),
					userId, resourceId);
			if (result != null) {
				List<Long> tagIds = ResourceTagDAO.getForResource(
						session.getMapper(ResourceTagMapper.class),
						result.getId());
				result.setTags(tagIds);
			}
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * Get from database resources with specified <code>scheduleCode</code>
	 * 
	 * @param scheduleCode
	 * @return
	 */
	public Set<Resource> getResourcesByScheduleCode(byte scheduleCode) {
		log.trace("Get resources by shedule code; code: {}", scheduleCode);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			// TODO check schedule code
			Set<Resource> resources = ResourceDAO.getByscheduleCode(
					session.getMapper(ResourceMapper.class), scheduleCode);
			if (resources == null) {
				return Collections.emptySet();
			}
			return resources;
		} finally {
			session.close();
		}
	}

	public boolean deleteResource(long userId, long resourceId)
			throws DatabaseException {
		log.trace("Delete resource; user id: {}, resource id: {}", userId,
				resourceId);
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			if (!UserDAO.exists(session.getMapper(UserMapper.class), userId)) {
				log.debug("Database exception: can't delete resource for nonexistant user");
				throw new DatabaseException(
						"Database exception: can't delete resource for nonexistant user");
			}
			result = ResourceDAO
					.delete(session.getMapper(ResourceMapper.class), userId,
							resourceId);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean deleteResourcesByIdAndTags(long userId, Long[] tagIds)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Delete resource; user id: {}, resource id: {}", userId,
					tagIds.toString());
		}
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			if (!UserDAO.exists(session.getMapper(UserMapper.class), userId)) {
				log.debug("Database exception: can't delete resource for nonexistant user");
				throw new DatabaseException(
						"Database exception: can't delete resource for nonexistant user");
			}
			if (tagIds != null
					&& !TagDAO.exists(session.getMapper(TagMapper.class),
							tagIds)) {
				log.debug("Database exception: can't get resources for nonexistent tags");
				throw new DatabaseException(
						"Database exception: can't get resources for nonexistent tags");
			}
			result = ResourceDAO.deleteByTags(
					session.getMapper(ResourceMapper.class), userId, tagIds);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean editResource(long userId, Resource resource)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Edit resource; user id = {}, resource = {}", userId,
					resource.toString());
		}
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			Resource savedResource = ResourceDAO.get(
					session.getMapper(ResourceMapper.class), userId,
					resource.getId());
			if (savedResource == null) {
				log.debug("Database exception: not found resource for edit");
				throw (new DatabaseException(
						"Database exception: not found resource for edit"));
			}

			if (savedResource.getUrl() != resource.getUrl()) {
				Integer hash = getNewHashCode(resource);
				if (hash == null) {
					hash = 0;
				}
				ResourceDAO.updateAfterCheck(
						session.getMapper(ResourceMapper.class),
						resource.getId(), hash);
			}
			resource.setUserId(userId);
			result = ResourceDAO.edit(session.getMapper(ResourceMapper.class),
					resource);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * Return all tags for specified user.
	 * 
	 * @param userId
	 * @return all tags for user with <code>userId</code>
	 */
	public Set<Tag> getTags(long userId) {
		log.trace("Get tags for user with user id: {}", userId);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return TagDAO.getTags(session.getMapper(TagMapper.class), userId);
		} finally {
			session.close();
		}
	}

	/**
	 * Update (in database) hash for resource with <code>resourceId</code>.
	 * 
	 * @param resourceId
	 *            resource, which hash will be overridden
	 * @param newHash
	 *            new hash value
	 */
	public boolean updateResourceHash(Long resourceId, Integer newHash) {
		log.trace("Update resource hash; resource id: {}, new hash: {}",
				resourceId, newHash);
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			// Don't forget - hash will be update only with 'true' result
			result = ResourceDAO.updateAfterCheck(
					session.getMapper(ResourceMapper.class), resourceId,
					newHash);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * Add tag with specified name and user id to database
	 * 
	 * @param userId
	 * @param tagName
	 * @return true, if success, false otherwise
	 */
	public Long addTag(long userId, String tagName) {
		SqlSession session = sqlSessionFactory.openSession();
		Long result = null;
		try {
			result = TagDAO.addTag(session.getMapper(TagMapper.class), userId,
					tagName);
			if (result != null) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean editTag(long userId, long tagId, String tagName) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;

		try {
			result = TagDAO.editTag(session.getMapper(TagMapper.class), userId,
					tagId, tagName);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public void deleteAllData() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			session.getMapper(UserMapper.class).deleteAll();
			session.commit();
		} finally {
			session.close();
		}
	}

}
