package net.thumbtack.updateNotifierBackend.database;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.daos.InitialDAO;
import net.thumbtack.updateNotifierBackend.database.daos.ResourceDAO;
import net.thumbtack.updateNotifierBackend.database.daos.ResourceTagDAO;
import net.thumbtack.updateNotifierBackend.database.daos.TagDAO;
import net.thumbtack.updateNotifierBackend.database.daos.UserDAO;
import net.thumbtack.updateNotifierBackend.database.daosimpl.InitialDAOImpl;
import net.thumbtack.updateNotifierBackend.database.daosimpl.ResourceDAOImpl;
import net.thumbtack.updateNotifierBackend.database.daosimpl.ResourceTagDAOImpl;
import net.thumbtack.updateNotifierBackend.database.daosimpl.TagDAOImpl;
import net.thumbtack.updateNotifierBackend.database.daosimpl.UserDAOImpl;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.entities.User;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseTinyException;
import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;
import net.thumbtack.updateNotifierBackend.resourceHandlers.UsersHandler;
import net.thumbtack.updateNotifierBackend.updateChecker.ResourceInvestigator;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseWrapper {

	private static final Logger log = LoggerFactory
			.getLogger(DatabaseWrapper.class);
	private static DatabaseWrapper db = null;

	private SqlSession session;
	private String RESOURCE = "mybatis-cfg.xml";

	private TagDAO tagDao;
	private UserDAO userDao;
	private ResourceDAO resourceDao;
	private ResourceTagDAO resourceTagDao;

	public static DatabaseWrapper getInstance() {
		if (db == null) {
			db = new DatabaseWrapper();
		}
		return db;
	}

	private DatabaseWrapper() {
		Reader reader = null;
		Properties properties = new Properties();
		try {
			reader = Resources.getResourceAsReader(RESOURCE);
			SqlSessionFactory sqlSessionFactory = null;
			String dbUrl = System.getenv("DATABASE_URL");
			if (dbUrl != null) {
				URI dbUri = new URI(dbUrl);

				properties.setProperty("p_username",
						dbUri.getUserInfo().split(":")[0]);
				properties.setProperty("p_password",
						dbUri.getUserInfo().split(":")[1]);
				properties.setProperty(
						"p_url",
						"jdbc:postgresql://" + dbUri.getHost() + ':'
								+ dbUri.getPort() + dbUri.getPath());
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(
						reader, "production", properties);

				session = sqlSessionFactory.openSession();
				InitialDAO lordOfTheDB = new InitialDAOImpl(session);
				lordOfTheDB.godMode();
				session.commit();
			} else {
				sqlSessionFactory = new SqlSessionFactoryBuilder()
						.build(reader);
				session = sqlSessionFactory.openSession();
			}
			tagDao = new TagDAOImpl(session);
			userDao = new UserDAOImpl(session);
			resourceDao = new ResourceDAOImpl(session);
			resourceTagDao = new ResourceTagDAOImpl(session);

		} catch (IOException e) {
			log.error("Great crash: exception on initialize database");
			// TODO Great crash should be here!
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Great crash should be here!
			e.printStackTrace();
		}
	}

	public User getUserIdByEmailOrAdd(User user)
			throws DatabaseSeriousException {
		if (log.isTraceEnabled()) {
			log.trace("getUserIdByEmailOrAdd(user); user: {}", user);
		}
		User savedUser = userDao.get(user.getEmail());
		if (savedUser == null) {
			log.debug("User doesn't exist, create new");
			if (!userDao.add(user)) {
				log.error("Serious exception: DB can't create user");
				throw new DatabaseSeriousException("User can't to login");
			} else {
				session.commit();
			}
		} else {
			log.debug("User exists");
			boolean updateNeed = false;
			if (!savedUser.getName().equals(user.getName())
					&& !UsersHandler.USER_DEFAULT_NAME.equals(user.getName())) {
				updateNeed = true;
				savedUser.setName(user.getName());
			}
			if (!savedUser.getSurname().equals(user.getSurname())
					&& !UsersHandler.USER_DEFAULT_SURNAME.equals(user
							.getSurname())) {
				updateNeed = true;
				savedUser.setSurname(user.getSurname());
			}
			if (updateNeed && !userDao.edit(savedUser)) {
				log.error("Serious exception: DB can't update user initials");
				throw new DatabaseSeriousException(
						"Automatic initials update failed");
			}
			user = savedUser;
		}
		return user;
	}

	public User getUserEmailById(long id) throws DatabaseSeriousException {
		log.trace("getUserEmailById; id: {}", id);
		User user = userDao.get2(id);
		if (user == null) {
			log.error("Serious exception: user doesn't exist");
			// TODO user deletes his account when his resource was updationg?
			// Serious, because i know, where this method is called
			throw new DatabaseSeriousException("Can't get user by id");
		}
		return user;
	}

	public void addResource(Resource resource) throws DatabaseTinyException,
			DatabaseSeriousException {
		if (log.isTraceEnabled()) {
			log.trace("Add resource; resource: {}", resource.toString());
		}
		if (!userDao.exists(new User(resource.getUserId()))) {
			log.debug("Database exception: can't add resource to nonexist user");
			throw new DatabaseTinyException(
					"Can't add resource to nonexist user");
		}
		log.debug("User exists");
		if (!ResourceInvestigator.setResourceMask(resource)) {
			log.debug("Database exception: can't get resource info for updating check");
			throw new DatabaseTinyException(
					"Can't get resource info for updating check");
		}
		if (!resourceDao.add(resource)) {
			throw new DatabaseSeriousException("Resource addition failed");
		}
		session.commit();
		if (resource.getTags() != null) {
			for (Long tagId : resource.getTags()) {
				if (!resourceTagDao.add(resource.getId(), tagId)) {
					log.error("Serious exception: relation addition failed");
					throw new DatabaseSeriousException(
							"Relation addition failed");
				}
			}
		}
		log.debug("Tag addition successful");
		session.commit();
	}

	public List<Resource> getResourcesByIdAndTags(long userId, List<Long> tagIds)
			throws DatabaseTinyException {
		if (log.isTraceEnabled()) {
			log.trace("Get resources by id and tags; user id: {}, tag ids: {}",
					userId, tagIds == null ? "null" : tagIds.toString());
		}
		if (!userDao.exists(new User(userId))) {
			log.debug("Database exception: can't get resources for nonexistent user");
			throw new DatabaseTinyException(
					"Database exception: can't get resources for nonexistent user");
		}
		log.debug("User exists");
		List<Resource> resources = null;
		if (tagIds == null) {
			resources = resourceDao.getAllForUser(userId);
		} else {
			if (!tagDao.exists(userId, tagIds)) {
				log.debug("Database exception: can't get resources for nonexistent tags");
				throw new DatabaseTinyException(
						"Database exception: can't get resources for nonexistent tags");
			}
			resources = resourceDao.getByUserIdAndTags(userId, tagIds);
		}
		for (Resource resource : resources) {
			resource.setTags(resourceTagDao.get(resource.getId()));
		}
		return resources;
	}

	public Resource getResource(long userId, long resourceId)
			throws DatabaseTinyException {
		log.trace("Get resource; user id = {}, resource id = {}", userId,
				resourceId);
		if (!userDao.exists(new User(userId))) {
			log.debug("Database exception: can't get resource to nonexist user");
			throw new DatabaseTinyException(
					"Can't get resource for nonexistent user");
		}
		log.debug("User exists");
		Resource resource = null;
		resource = resourceDao.get(userId, resourceId);
		if (resource != null) {
			log.debug("Resource exists");
			resource.setTags(resourceTagDao.get(resource.getId()));
		} else {
			log.debug("Resource doesn't exist");
		}
		return resource;
	}

	/**
	 * Get from database resources with specified <code>scheduleCode</code>
	 * 
	 * @param scheduleCode
	 * @return
	 */
	public Set<Resource> getResourcesByScheduleCode(byte scheduleCode) {
		log.trace("Get resources by shedule code; code: {}", scheduleCode);
		// TODO check schedule code?
		Set<Resource> resources = resourceDao.getByscheduleCode(scheduleCode);
		if (resources == null) {
			log.debug("No resources with such shedule code");
			return Collections.emptySet();
		}
		return resources;
	}

	public void deleteResource(Resource resource)
			throws DatabaseSeriousException, DatabaseTinyException {
		if (log.isTraceEnabled()) {
			log.trace("Delete resource; user id: {}, resource id: {}",
					resource.getUserId(), resource.getId());
		}
		if (!userDao.exists(new User(resource.getUserId()))) {
			log.debug("Database exception: can't delete resource for nonexistant user");
			throw new DatabaseTinyException(
					"Database exception: can't delete resource for nonexistant user");
		}
		log.debug("User exists");
		if (!resourceDao.delete(resource)) {
			log.error("Serious exception: resource deletion failed");
			throw new DatabaseSeriousException("Resource deletion failed");
		}
		log.debug("Resource deleted successfully");
		session.commit();
	}

	public void deleteResourcesByIdAndTags(long userId, List<Long> tagIds)
			throws DatabaseTinyException, DatabaseSeriousException {
		if (log.isTraceEnabled()) {
			log.trace("Delete resource; user id: {}, tag ids: {}", userId,
					tagIds == null ? "null" : tagIds.toString());
		}
		if (!userDao.exists(new User(userId))) {
			log.debug("Database exception: can't delete resource for nonexistant user");
			throw new DatabaseTinyException(
					"Database exception: can't delete resource for nonexistant user");
		}
		log.debug("User exists");
		boolean result = false;
		if (tagIds == null) {
			log.debug("Delete all resources");
			result = resourceDao.deleteAll(userId);
		} else {
			log.debug("Delete resources by tags");
			if (!tagDao.exists(userId, tagIds)) {
				log.debug("Database exception: can't get resources for nonexistent tags");
				throw new DatabaseTinyException(
						"Database exception: can't get resources for nonexistent tags");
			}
			result = resourceDao.deleteByTags(userId, tagIds);
		}
		if (!result) {
			log.error("Serious exception: resource delition failed");
			throw new DatabaseSeriousException("Resource delition failed");
		}
		session.commit();
	}

	public void editResource(Resource resource) throws DatabaseTinyException,
			DatabaseSeriousException {
		if (log.isTraceEnabled()) {
			log.trace("Edit resource; resource = {}", resource.toString());
		}
		if (!userDao.exists(new User(resource.getUserId()))) {
			throw new DatabaseTinyException(
					"Can't edit resource for nonexistent user");
		}
		log.debug("User exists");
		Resource savedResource = resourceDao.get(resource.getUserId(),
				resource.getId());
		if (savedResource == null) {
			log.debug("Database exception: not found resource for edit");
			throw (new DatabaseTinyException(
					"Database exception: not found resource for edit"));
		}
		if (!savedResource.getUrl().equals(resource.getUrl())) {
			log.debug("Mask update need");
			if (!ResourceInvestigator.setResourceMask(resource)) {
				log.debug("Database exception: can't get resource info for updating check");
				throw new DatabaseTinyException(
						"Can't get resource info for updating check");
			}
			resourceDao.updateAfterCheck(resource);
		}
		if (!tagDao.exists(resource.getUserId(), resource.getTags())) {
			log.debug("Database exception: can't assign nonexistant tags to resource");
			throw new DatabaseTinyException(
					"Database exception: can't assign nonexistant tags to resource");
		}
		if (resource.getName() == null) {
			resource.setName("noname");
		}
		if (!resourceDao.edit(resource)) {
			log.error("Serious exception: resource delition failed");
			throw new DatabaseSeriousException("Resource edition failed");
		}
		if (resourceTagDao.exists(resource.getId())
				&& !resourceTagDao.delete(resource.getId())) {
			log.error("Serious exception: relation delition failed");
			throw new DatabaseSeriousException("Relation delition failed");
		}
		log.debug("Relations were deleted");
		if (resource.getTags() != null) {
			for (Long tagId : resource.getTags()) {
				if (!resourceTagDao.add(resource.getId(), tagId)) {
					log.error("Serious exception: relation addition failed");
					throw new DatabaseSeriousException(
							"Relation addition failed");
				}
			}
		}
		log.debug("Relations were added");
		session.commit();
	}

	/**
	 * Update (in database) mask for resource with <code>resourceId</code>. Mask
	 * is the resource information for updating chec
	 * 
	 * @param resourceId
	 *            resource, which hash will be overridden
	 * @param newHash
	 *            new hash value
	 * @throws DatabaseSeriousException
	 */
	public void updateResourceHash(Resource resource)
			throws DatabaseSeriousException {
		log.trace("Update resource mask; resource: {}", resource);
		if (!resourceDao.exists(new Resource(resource.getId()))) {
			// TODO What can i dooo?
			log.error("Serious exception: mask updating failed");
			throw new DatabaseSeriousException(
					"Can't update mask for nonexistent resource");
		}
		if (!resourceDao.updateAfterCheck(resource)) {
			log.error("Serious exception: mask updating failed");
			throw new DatabaseSeriousException("Mask updating failed");
		}
		session.commit();
	}

	/**
	 * Return all tags for specified user.
	 * 
	 * @param userId
	 * @return all tags for user with <code>userId</code>
	 * @throws DatabaseTinyException
	 */
	public Set<Tag> getTags(long userId) throws DatabaseTinyException {
		log.trace("Get tags for user with user id: {}", userId);
		if (!userDao.exists(new User(userId))) {
			log.debug("Can't get tags for nonexistent user");
			throw new DatabaseTinyException(
					"Can't get tags for nonexistent user");
		}
		return tagDao.get(userId);
	}

	public Tag getTag(Tag tag) throws DatabaseTinyException {
		log.trace("Get tags for user with user id: {}", tag.getUserId());
		if (!userDao.exists(new User(tag.getUserId()))) {
			log.debug("Can't get tag for nonexistent user");
			throw new DatabaseTinyException(
					"Can't get tag for nonexistent user");
		}
		return tagDao.get(tag);
	}

	/**
	 * Add tag with specified name and user id to database
	 * 
	 * @param userId
	 * @param tagName
	 * @return true, if success, false otherwise
	 * @throws DatabaseTinyException
	 * @throws DatabaseSeriousException
	 */
	public void addTag(Tag tag) throws DatabaseTinyException,
			DatabaseSeriousException {
		if (!userDao.exists(new User(tag.getUserId()))) {
			log.debug("Can't add tag to nonexistent user");
			throw new DatabaseTinyException("Can't add tag to nonexistent user");
		}
		if (!tagDao.add(tag)) {
			log.error("Serious exception: tag addition failed");
			throw new DatabaseSeriousException("Tag addition failed");
		}
		session.commit();
	}

	public void editTag(Tag tag) throws DatabaseTinyException,
			DatabaseSeriousException {
		if (!userDao.exists(new User(tag.getUserId()))) {
			log.debug("Can't edit tag of nonexistent user");
			throw new DatabaseTinyException(
					"Can't edit tag of nonexistent user");
		}
		if (!tagDao.edit(tag)) {
			log.error("Serious exception: tag edition failed");
			throw new DatabaseSeriousException("Tag edition failed");
		}
		session.commit();
	}

	public void deleteTag(Tag tag) throws DatabaseTinyException,
			DatabaseSeriousException {
		if (!userDao.exists(new User(tag.getUserId()))) {
			log.debug("Can't delete tag of nonexistent user");
			throw new DatabaseTinyException(
					"Can't delete tag of nonexistent user");
		}
		if (!tagDao.delete(tag)) {
			log.error("Serious exception: tag edition failed");
			throw new DatabaseSeriousException("Tag delition failed");
		}
		session.commit();
	}

	public List<Long> getUpdated(long userId, Date date)
			throws DatabaseTinyException {
		if (log.isTraceEnabled()) {
			log.trace("Get updated resources; userId: {}, date: {}", userId,
					date);
		}
		User user = new User(userId);
		if (!userDao.exists(user)) {
			log.debug("Can't get updated resources for nonexistent user");
			throw new DatabaseTinyException(
					"Can't get updated resources for nonexistent user");
		}
		log.debug("User exists");
		List<Long> ids = resourceDao.getUpdated(userId, date);
		if (ids == null) {
			log.debug("Resource list is null");
			ids = Collections.emptyList();
		}
		if (ids.isEmpty()) {
			log.debug("Resource list is empty");
		} else {
			log.debug("Resource list is not empty");
		}
		return ids;
	}

	public void deleteAllData() {
		session.getMapper(UserMapper.class).deleteAll();
		session.commit();
	}

}
