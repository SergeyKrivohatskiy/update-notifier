package net.thumbtack.updateNotifierBackend.database;

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

public class DatabaseService {

	private static final SqlSessionFactory sqlSessionFactory;

	public static SqlSessionFactory getSqlsessionfactory() {
		return sqlSessionFactory;
	}

	static {
		String resource = "mybatis-cfg.xml";
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			// TODO Great crash should be here!
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	}

	public DatabaseService() {
	}

	public Long getUserIdByEmailOrAdd(String email) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Long userId = UserDAO.getUserIdOrAdd(
					session.getMapper(UserMapper.class), email);
			if (userId != null) {
				session.commit();
			}
			return userId;
		} finally {
			session.close();
		}
	}

	public String getUserEmailById(Long id) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			String email = UserDAO.getUserEmail(
					session.getMapper(UserMapper.class), id);
			if (email != null) {
				session.commit();
			} else {
				// REVU (06.08) sKrivohatskiy Why do not you return null here?
				email = "";
			}
			return email;
		} finally {
			session.close();
		}
	}

	public List<Resource> getResourcesByIdAndTags(Long userId, Long[] tagIds) {
		SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
		try {
			List<Resource> resources = null;
			// REVU (06.08) sKrivohatskiy Why is this method called getByUserIdAndTags?
			resources = ResourceDAO.getByUserIdAndTags(
					session.getMapper(ResourceMapper.class), userId, tagIds);
			if (resources == null) {
				resources = Collections.emptyList();
			} else {
				for (Resource resource : resources) {
					resource.setTagIds(ResourceTagDAO.getForResource(
							session.getMapper(ResourceTagMapper.class),
							resource.getId()));
				}
			}
			return resources;
		} finally {
			session.close();
		}
	}

	public boolean addResource(long userId, Resource resource) {
		SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
		boolean result = false;
		try {
			resource.setUserId(userId);
			Long id = ResourceDAO.add(session.getMapper(ResourceMapper.class),
					resource);
			if (id != 0) {
				if (resource.getTagIds() != null) {
					for (Long tagId : resource.getTagIds()) {
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

	public boolean deleteResource(long resourceId) {
		// Not checked
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			result = ResourceDAO.delete(
					session.getMapper(ResourceMapper.class), resourceId);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean deleteResourcesByIdAndTags(long userId, Long[] tagsId) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			result = ResourceDAO.deleteByTags(
					session.getMapper(ResourceMapper.class), userId, tagsId);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean editResource(long userId, Resource resource) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			resource.setUserId(userId);
			// if (ResourceDAO.get(session.getMapper(ResourceMapper.class),
			// resource.getId()) == null) {
			// throw (new BadRequestException("Resource not exist"));
			// }
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

	public Resource getResource(long userId, long resourceId) {
		SqlSession session = sqlSessionFactory.openSession();
		Resource result = null;
		try {
			result = ResourceDAO.get(session.getMapper(ResourceMapper.class),
					resourceId);
			if (result != null) {
				List<Long> tagIds = ResourceTagDAO.getForResource(
						session.getMapper(ResourceTagMapper.class),
						result.getId());
				result.setTagIds(tagIds);
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
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return TagDAO.getTags(session.getMapper(TagMapper.class), userId);
		} finally {
			session.close();
		}
	}

	/**
	 * Get from database resources with specified <code>sheduleCode</code>
	 * 
	 * @param sheduleCode
	 * @return
	 */
	public Set<Resource> getResourcesBySheduleCode(byte sheduleCode) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Set<Resource> tags = ResourceDAO.getBySheduleCode(
					session.getMapper(ResourceMapper.class), sheduleCode);
			if (tags == null) {
				return Collections.emptySet();
			}
			return tags;
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
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			// Don't forget - hash will be update only with 'true' result
			result = ResourceDAO.updateHash(
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
	public boolean addTag(long userId, String tagName) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			result = TagDAO.addTag(session.getMapper(TagMapper.class), userId,
					tagName);
			if (result) {
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
			result = TagDAO.editTag(session.getMapper(TagMapper.class), tagId,
					tagName);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

}
