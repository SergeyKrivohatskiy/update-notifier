package net.thumbtack.updateNotifierBackend.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.daos.ResourceDAO;
import net.thumbtack.updateNotifierBackend.database.daos.TagDAO;
import net.thumbtack.updateNotifierBackend.database.daos.TagResourceDAO;
import net.thumbtack.updateNotifierBackend.database.daos.UserDAO;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.TagMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;

import org.apache.ibatis.io.Resources;
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
		// try {
		// ServiceRegistry serviceRegistry;
		//
		// Configuration configuration = new Configuration();
		// configuration.configure();
		// serviceRegistry = new
		// ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		// sqlSessionFactory =
		// configuration.buildSessionFactory(serviceRegistry);
		// sqlSessionFactory = new
		// Configuration().configure().buildSessionFactory(serviceRegistry);
		// } catch (Throwable ex) {
		// throw new ExceptionInInitializerError(ex);
		// }
		// getResources();
	}

	public Long getUserIdByEmail(String email) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Long userId = UserDAO.getUserId(
					session.getMapper(UserMapper.class), email);
			if (userId != null) {
				session.commit();
			}
			return userId;
		} finally {
			session.close();
		}
	}

	public List<Resource> getResourcesByIdAndTags(Long userId, long[] tags) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return ResourceDAO.getResources(
					session.getMapper(ResourceMapper.class), userId, tags);
		} finally {
			session.close();
		}
	}

	public boolean addResource(long userId, Resource resource) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			result = ResourceDAO.addResource(userId, resource)
					&& TagResourceDAO.addRelations(resource.getId(),
							resource.getTagsIdArray());
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public boolean deleteResource(long resourceId) {
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

	public boolean deleteResourcesByIdAndTags(long userId, long[] tagsId) {
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

	public boolean editResource(long userId, long resourceId, Resource resource) {
		SqlSession session = sqlSessionFactory.openSession();
		boolean result = false;
		try {
			result = ResourceDAO.edit(session.getMapper(ResourceMapper.class),
					userId, resource);
			if (result) {
				session.commit();
			}
		} finally {
			session.close();
		}
		return result;
	}

	public Resource getResource(long userId, long resourceId) {
		return null;
		// TODO Auto-generated method stub
		// TODO Do you need in this method?

	}

	public Set<Tag> getTags(long userId) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return TagDAO.getTags(session.getMapper(TagMapper.class), userId);
		} finally {
			session.close();
		}
	}

	public Set<Resource> getResourcesBySheduleCode(int sheduleCode) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return ResourceDAO.getResources(
					session.getMapper(ResourceMapper.class), sheduleCode);
		} finally {
			session.close();
		}
	}

}
