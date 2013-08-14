package net.thumbtack.updateNotifierBackend.database;

import static net.thumbtack.updateNotifierBackend.updateChecker.CheckForUpdate.getNewHashCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;

import net.thumbtack.updateNotifierBackend.database.daosimpl.ResourceDAOOld;
import net.thumbtack.updateNotifierBackend.database.daosimpl.ResourceTagDAOOld;
import net.thumbtack.updateNotifierBackend.database.daosimpl.TagDAOImpl;
import net.thumbtack.updateNotifierBackend.database.daosimpl.UserDAOImpl;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.entities.User;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseException;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseTinyException;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;
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
// BaseDao should be with sqlSessionFactory and All daos should implement BaseDao interface.
// And you don't have to send mapper to dao class, they will use sqlSessionFactory.
public class DatabaseService {

	private static final Logger log = LoggerFactory
			.getLogger(DatabaseService.class);
	private static DatabaseService db = null;

	private SqlSession session;
	private String RESOURCE = "mybatis-cfg.xml";
	
	private TagDAOImpl tagDao;
	private UserDAOImpl userDao;

	public static DatabaseService getInstance() {
		if (db == null) {
			db = new DatabaseService();
		}
		return db;
	}

	public DatabaseService() {
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(RESOURCE);
		} catch (IOException e) {
			log.error("Great crash: exception on initialize database");
			// TODO Great crash should be here!
			e.printStackTrace();
		}
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(inputStream);
		session = sqlSessionFactory.openSession(ExecutorType.BATCH);
		tagDao = new TagDAOImpl(session);
		userDao = new UserDAOImpl(session);
	}

	public Long getUserIdByEmailOrAdd(String email) throws DatabaseSeriousException {
		log.trace("Get user email by id; email: {}", email);
		User user = userDao.get(email);
		if(user == null) {
			if(!userDao.add(user)) {
				throw new DatabaseSeriousException("User can't to login");
			} else {
				session.commit();
			}
		}
		return user.getId();
	}

	public User getUserEmailById(Long id) throws DatabaseSeriousException {
		log.trace("get user email by id; id: {}", id);
		User user = userDao.get2(id);
		if(user == null) {
			// TODO user deletes his account when his resource was updationg?
			// Serious, because i know, where this method is called
			throw new DatabaseSeriousException("Can't get user by id");
		}
		return user;
	}

	public boolean addResource(long userId, Resource resource)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Add resource; user id: {}, resource: {}", userId,
					resource.toString());
		}
		boolean result = false;
		if(!userDao.exists(userId)) {
			log.debug("Database exception: can't add resource to nonexist user");
			throw new DatabaseTinyException(
					"Database exception: can't add resource to nonexist user");
		}
		resource.setUserId(userId);
		resource.setHash(getNewHashCode(resource));

		Long id = ResourceDAOOld.add(session.getMapper(ResourceMapper.class),
				resource);
		if (id != null) {
			if (resource.getTags() != null) {
				for (Long tagId : resource.getTags()) {
					ResourceTagDAOOld.addRelation(
							session.getMapper(ResourceTagMapper.class), id,
							tagId);
				}
			}
			session.commit();
			result = true;
		}
		return result;
	}

	public List<Resource> getResourcesByIdAndTags(Long userId, List<Long> tagIds)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Get resources by id and tags; user id: {}, tag ids: {}",
					userId, tagIds == null ? null : tagIds.toString());
		}
		if (!userDao.exists(userId)) {
			log.debug("Database exception: can't get resources for nonexistent user");
			throw new DatabaseException(
					"Database exception: can't get resources for nonexistent user");
		}
		if (tagIds != null
				&& !tagDao.exists(userId, tagIds)) {
			log.debug("Database exception: can't get resources for nonexistent tags");
			throw new DatabaseException(
					"Database exception: can't get resources for nonexistent tags");
		}
		List<Resource> resources = ResourceDAOOld.getByUserIdAndTags(
				session.getMapper(ResourceMapper.class), userId, tagIds.toArray(new Long[]{}));
		for (Resource resource : resources) {
			resource.setTags(ResourceTagDAOOld.getForResource(
					session.getMapper(ResourceTagMapper.class),
					resource.getId()));
		}
		return resources;
	}

	public Resource getResource(long userId, long resourceId) {
		log.trace("Get resource; user id = {}, resource id = {}", userId,
				resourceId);
		Resource result = null;
		result = ResourceDAOOld.get(session.getMapper(ResourceMapper.class),
				userId, resourceId);
		if (result != null) {
			List<Long> tagIds = ResourceTagDAOOld.getForResource(
					session.getMapper(ResourceTagMapper.class), result.getId());
			result.setTags(tagIds);
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
		// TODO check schedule code
		Set<Resource> resources = ResourceDAOOld.getByscheduleCode(
				session.getMapper(ResourceMapper.class), scheduleCode);
		if (resources == null) {
			return Collections.emptySet();
		}
		return resources;
	}

	public boolean deleteResource(long userId, long resourceId)
			throws DatabaseException {
		log.trace("Delete resource; user id: {}, resource id: {}", userId,
				resourceId);
		boolean result = false;
		if (!userDao.exists(userId)) {
			log.debug("Database exception: can't delete resource for nonexistant user");
			throw new DatabaseException(
					"Database exception: can't delete resource for nonexistant user");
		}
		result = ResourceDAOOld.delete(session.getMapper(ResourceMapper.class),
				userId, resourceId);
		if (result) {
			session.commit();
		}
		return result;
	}

	public boolean deleteResourcesByIdAndTags(long userId, List<Long> tagIds)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Delete resource; user id: {}, tag ids: {}", userId,
					tagIds == null ? "null" : tagIds.toString());
		}
		boolean result = false;
		if (!userDao.exists(userId)) {
			log.debug("Database exception: can't delete resource for nonexistant user");
			throw new DatabaseException(
					"Database exception: can't delete resource for nonexistant user");
		}
		if (tagIds != null
				&& !tagDao.exists(userId, tagIds)) {
			log.debug("Database exception: can't get resources for nonexistent tags");
			throw new DatabaseException(
					"Database exception: can't get resources for nonexistent tags");
		}
		result = ResourceDAOOld.deleteByTags(
				session.getMapper(ResourceMapper.class), userId, tagIds.toArray(new Long[]{}));
		if (result) {
			session.commit();
		}
		return result;
	}

	public boolean editResource(long userId, Resource resource)
			throws DatabaseException {
		if (log.isTraceEnabled()) {
			log.trace("Edit resource; user id = {}, resource = {}", userId,
					resource.toString());
		}
		boolean result = false;
		Resource savedResource = ResourceDAOOld.get(
				session.getMapper(ResourceMapper.class), userId,
				resource.getId());
		if (savedResource == null) {
			log.debug("Database exception: not found resource for edit");
			throw (new DatabaseException(
					"Database exception: not found resource for edit"));
		}

		if (!savedResource.getUrl().equals(resource.getUrl())) {
			Integer hash = getNewHashCode(resource);
			if (hash == null) {
				hash = 0;
			}
			ResourceDAOOld.updateAfterCheck(
					session.getMapper(ResourceMapper.class), resource.getId(),
					hash);
		}
		resource.setUserId(userId);
		if (!tagDao.exists(userId, resource
				.getTags())) {
			log.debug("Database exception: can't assign nonexistant tags to resource");
			throw new DatabaseException(
					"Database exception: can't assign nonexistant tags to resource");
		}
		result = ResourceDAOOld.edit(session.getMapper(ResourceMapper.class),
				resource);
		if (result) {
			ResourceTagDAOOld.deleteRelations(
					session.getMapper(ResourceTagMapper.class),
					resource.getId());
			if (resource.getTags() != null) {
				for (Long tagId : resource.getTags()) {
					ResourceTagDAOOld.addRelation(
							session.getMapper(ResourceTagMapper.class),
							resource.getId(), tagId);
				}
			}
		}
		if (result) {
			session.commit();
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
		return tagDao.get(userId);
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
		boolean result = false;
		// Don't forget - hash will be update only with 'true' result
		result = ResourceDAOOld.updateAfterCheck(
				session.getMapper(ResourceMapper.class), resourceId, newHash);
		if (result) {
			session.commit();
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
	public void addTag(Tag tag) {
		tagDao.add(tag);
		if (tag.getId() != null) {
			session.commit();
		}
	}

	public boolean editTag(long userId, long tagId, String tagName) {
		boolean result = false;

		result = tagDao.edit(new Tag(tagId, userId, tagName));
		if (result) {
			session.commit();
		}
		return result;
	}

	public void deleteTag(long userId, long tagId) throws DatabaseException {
		boolean result = false;
		if (!userDao.exists(userId)) {
			throw new DatabaseException(
					"Database exception. Can't delete tag of nonexistent user");
		}
		result = tagDao.delete(new Tag(tagId, userId, null));

		if (result) {
			session.commit();
		}
	}

	public void deleteAllData() {
		session.getMapper(UserMapper.class).deleteAll();
		session.commit();
	}

}
