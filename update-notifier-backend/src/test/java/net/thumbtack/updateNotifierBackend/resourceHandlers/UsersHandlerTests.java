package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.text.MessageFormat;

import javax.ws.rs.BadRequestException;


import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class UsersHandlerTests {
	private static final String EXAMPLE_USER_EMAIL = "email@post.com";
	
	@Before
	public void beforeTest() {
		UpdateNotifierBackend.getDatabaseService().deleteAllData();
	}
	
	@Test
	public void signInTest() {
		final int EMAIL_COUNT = 100;
		Long[] usersId = new Long[EMAIL_COUNT];
		UsersHandler handler = new UsersHandler();
		final String EMAIL_PATTERN = "email{0,number}@post.com";

		for(Integer i = 0; i < EMAIL_COUNT; i += 1) {
			usersId[i] = handler.signIn(MessageFormat.format(EMAIL_PATTERN, i));
			Assert.assertTrue(usersId[i] != null);
		}
		
		for(Integer i = 0; i < EMAIL_COUNT; i += 1) {
			Assert.assertEquals(usersId[i].longValue(), 
					handler.signIn(MessageFormat.format(EMAIL_PATTERN, i)));
		}
	}
	
	@Test
	public void getAddResourcesTest() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		Resource newResource = new Resource();
		newResource.setUrl("http://google.com");
		newResource.setUserId(userId);
		handler.addUserResource(userId, new Gson().toJson(newResource));
		Resource[] resources = new Gson().fromJson(handler.getUserResources(userId, ""), Resource[].class);
	
		Assert.assertTrue(resources.length >= 1);
	}
	
	@Test
	public void addGetTagsTest() {
		UsersHandler handler = new UsersHandler();
		
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		
		handler.addTag(userId, "New tag name1");
		handler.addTag(userId, "New tag name2");
		handler.addTag(userId, "New tag name3");
		Tag[] tags = new Gson().fromJson(handler.getUserTags(userId), Tag[].class);

		Assert.assertTrue(tags.length >= 3);
	}
	
	@Test
	public void addResourceBadRequest() {
		UsersHandler handler = new UsersHandler();
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		try {
			handler.addUserResource(userId, "{'incorrect':'resource', 'j':'son'}");
			Assert.fail();
		} catch(BadRequestException e) {
			// ignore
		}
	}
}
