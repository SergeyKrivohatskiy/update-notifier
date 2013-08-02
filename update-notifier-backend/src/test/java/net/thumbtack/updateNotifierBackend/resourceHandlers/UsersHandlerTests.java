package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.text.MessageFormat;

import javax.ws.rs.BadRequestException;

import net.thumbtack.updateNotifierBackend.databaseService.Tag;
import net.thumbtack.updateNotifierBackend.databaseService.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

public class UsersHandlerTests {
	private static final String EXAMPLE_USER_EMAIL = "email@post.com";
	
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
	public void getAddEditResourcesTest() {
		
		UsersHandler handler = new UsersHandler();
		final String CORRECT_TAGS_STRING = "11,125,21,12";
		final String INCORRECT_TAGS_STRING = "11,125,asd,5";
		
		Resource[] resources;
		resources = new Gson().fromJson(handler.getUserResources(
				handler.signIn(EXAMPLE_USER_EMAIL), null), Resource[].class);
		int initialSize = resources.length;
		Resource resourceToAdd = new Resource();
		resourceToAdd.setUrl("google.com");
		handler.addUserResource(handler.signIn(EXAMPLE_USER_EMAIL), 
				new Gson().toJson(resourceToAdd));

		resources = new Gson().fromJson(handler.getUserResources(
				handler.signIn(EXAMPLE_USER_EMAIL), null), Resource[].class);
		Assert.assertEquals(resources.length, initialSize + 1);

		handler.getUserResources(handler.signIn(EXAMPLE_USER_EMAIL), CORRECT_TAGS_STRING);
		
		try {
			handler.getUserResources(handler.signIn(EXAMPLE_USER_EMAIL), INCORRECT_TAGS_STRING);
			Assert.assertTrue("Exception expected here", false);
		} catch(BadRequestException ex) {}
		
		Long existingResourceId = resources[0].getId();
		
		Resource res = new Gson().fromJson(handler.getUserResource(handler.signIn(EXAMPLE_USER_EMAIL),
				existingResourceId), Resource.class);
		
		Assert.assertTrue(res != null);
		
		Assert.assertEquals(res.getId(), existingResourceId);
		
		resourceToAdd.setUrl("yandex.ru");
		handler.editUserResource(handler.signIn(EXAMPLE_USER_EMAIL), resourceToAdd.getId(),
				new Gson().toJson(resourceToAdd));
		
		res = new Gson().fromJson(handler.getUserResource(handler.signIn(EXAMPLE_USER_EMAIL),
				resourceToAdd.getId()), Resource.class);
		
		Assert.assertEquals(res.getUrl(), resourceToAdd.getUrl());
		
	}
	
	@Test
	public void getTagsTest() {
		UsersHandler handler = new UsersHandler();
		
		Long userId = handler.signIn(EXAMPLE_USER_EMAIL);
		
		Tag[] tags = new Gson().fromJson(handler.getUserTags(userId), Tag[].class);
		Assert.assertTrue(tags != null);
	}
	
}
