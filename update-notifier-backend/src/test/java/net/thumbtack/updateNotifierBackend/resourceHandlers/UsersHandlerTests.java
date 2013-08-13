package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.text.MessageFormat;
import java.util.ArrayList;

import javax.ws.rs.BadRequestException;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UsersHandlerTests {
	private static final String EXAMPLE_USER_EMAIL = "email@post.com";
	private static final Gson GSON = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd hh:mm:ss.S")
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	@Before
	public void beforeTest() {
		UpdateNotifierBackend.getDatabaseService().deleteAllData();
	}

	@Test
	public void signInTest() {
		final int EMAILS_COUNT = 100;

		Long[] usersId = new Long[EMAILS_COUNT];
		UsersHandler handler = new UsersHandler();
		final String EMAIL_PATTERN = "email{0,number}@post.com";

		for (Integer i = 0; i < EMAILS_COUNT; i += 1) {
			usersId[i] = handler.signIn(MessageFormat.format(EMAIL_PATTERN, i));
			assertTrue(usersId[i] != null);
		}

		for (Integer i = 0; i < EMAILS_COUNT; i += 1) {
			assertEquals(usersId[i].longValue(),
					handler.signIn(MessageFormat.format(EMAIL_PATTERN, i)));
		}
	}

	@Test
	public void addGetDeleteResourcesTest() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);

		int count = addResources(handler, userId);

		Resource[] resources = new Gson().fromJson(
				handler.getUserResources(userId, ""), Resource[].class);
		assertEquals(resources.length, count);

		for (int i = 0; i < resources.length; i += 1) {
			String resJson = handler.getUserResource(userId,
					resources[i].getId());
			assertTrue(new Gson().fromJson(resJson, Resource.class).equals(
					resources[i]));
			handler.deleteUserResource(userId, resources[i].getId());
		}
		assertEquals(new Gson().fromJson(handler.getUserResources(userId, ""),
				Resource[].class).length, 0);
	}

	@Test
	public void addGetEditTagsTest() {
		UsersHandler handler = new UsersHandler();

		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);

		int count = addTags(handler, userId);

		Tag[] tags = new Gson().fromJson(handler.getUserTags(userId),
				Tag[].class);

		assertEquals(tags.length, count);

		for (int i = 0; i < count; i += 1) {
			handler.editTag(userId, tags[i].getId(),
					Long.toString(tags[i].getId()));
		}

		Tag[] editedTags = new Gson().fromJson(handler.getUserTags(userId),
				Tag[].class);
		for (int i = 0; i < count; i += 1) {
			editedTags[i].getId().equals(Long.getLong(editedTags[i].getName()));
		}
	}

	@Test
	public void addGetResourcesWithTags() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);

		addTags(handler, userId);
		Tag[] tags = new Gson().fromJson(handler.getUserTags(userId),
				Tag[].class);
		int resCount = addResourcesWithTags(handler, userId, tags);
		Resource[] resources = new Gson().fromJson(
				handler.getUserResources(userId, ""), Resource[].class);

		assertEquals(resources.length, resCount);

		ArrayList<Long> tagsList = new ArrayList<Long>();
		for (int i = 0; i < tags.length; i += 1) {
			tagsList.add(tags[i].getId());
			String resJson = handler.getUserResource(userId,
					resources[i].getId());
			Resource res = new Gson().fromJson(resJson, Resource.class);
			assertTrue(res.getTags().containsAll(tagsList));
			assertTrue(res.getUrl().equals(Integer.toString(tagsList.size())));
		}
	}

	@Test
	public void addResourceBadRequest() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		try {
			handler.addUserResource(userId,
					"{'incorrect':'resource', 'j':'son'}");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
		try {
			handler.addUserResource(userId, "{'url':'http:/ya.ru', "
					+ "'dom_path:'/', 'scheduleCode' : -1}");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
		try {
			handler.addUserResource(userId, "");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
		try {
			handler.addUserResource(userId, "{}");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
		try {
			handler.addUserResource(-1, "{'url':'http:/ya.ru', "
					+ "'dom_path':'/', 'scheduleCode' : 1}");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
	}

	@Test
	public void editResourceBadRequest() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		try {
			handler.editUserResource(userId,
					"{'incorrect':'resource', 'j':'son'}");
			fail();
		} catch (BadRequestException e) {
		} // Ignore
	}

	@Test
	public void getResourceBadRequest() {
		UsersHandler handler = new UsersHandler();
		try {
			handler.getUserResources(handler.signIn(EXAMPLE_USER_EMAIL),
					"incorrect tag string");
			fail();
		} catch (BadRequestException e) {
			// ignore
		}
	}

	/**
	 * 
	 * @param handler
	 * @param userId
	 * @return number of resources were added
	 */
	private int addResources(UsersHandler handler, Long userId) {
		Resource newResource = new Resource();
		newResource.setUrl("http://google.com");
		newResource.setDomPath("/");
		handler.addUserResource(userId, GSON.toJson(newResource));
		newResource.setUrl("http://yandex.ru");
		newResource.setDomPath("/0");
		handler.addUserResource(userId, GSON.toJson(newResource));
		newResource.setUrl("http://habrahabr.ru");
		newResource.setDomPath("/1/0");
		handler.addUserResource(userId, GSON.toJson(newResource));
		return 3;
	}

	/**
	 * 
	 * @param handler
	 * @param userId
	 * @return number of tags were added
	 */
	private int addTags(UsersHandler handler, Long userId) {
		handler.addTag(userId, "\"New tag name1\"");
		handler.addTag(userId, "\"New tag name2\"");
		handler.addTag(userId, "\"New tag name3\"");
		return 3;
	}

	private int addResourcesWithTags(UsersHandler handler, Long userId,
			Tag[] tags) {
		ArrayList<Long> tagsList = new ArrayList<Long>();

		for (int i = 0; i < tags.length; i += 1) {
			tagsList.add(tags[i].getId());
			Resource newResource = new Resource();
			newResource.setDomPath("/");
			newResource.setTags(new ArrayList<Long>(tagsList));
			newResource.setUrl(Integer.toString(tagsList.size()));
			handler.addUserResource(userId, GSON.toJson(newResource));
		}
		return tags.length;
	}
}
