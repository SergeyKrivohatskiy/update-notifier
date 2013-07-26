package test.java.net.thumbtack.updateNotifierBackend;

import java.util.List;


import main.java.net.thumbtack.updateNotifierBackend.databaseService.AccountInfo;
import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

import org.junit.Assert;
import org.junit.Test;



public class DatabaseTests {
	
	@Test
	public void AddGetAccountInfoResourceInfo() {
		DatabaseService database = new DatabaseService();
		
		ResourceInfo firstResource = new ResourceInfo();
		firstResource.setUrl("first.resource");
		ResourceInfo secondResource = new ResourceInfo();
		secondResource.setUrl("second.resource");
		
		AccountInfo firstAccount = new AccountInfo();
		firstAccount.setEmail("first@account.info");
		firstAccount.getResources().add(firstResource);
		AccountInfo secondAccount = new AccountInfo();
		secondAccount.setEmail("second@account.info");
		secondAccount.getResources().add(firstResource);
		secondAccount.getResources().add(secondResource);
		
		Assert.assertTrue(database.addAccountInfo(firstAccount));
		Assert.assertTrue(database.addAccountInfo(secondAccount));
		
		List<ResourceInfo> resourcesInfo = database.getResourcesInfo();
		Assert.assertTrue(resourcesInfo.size() == 2);
		for(ResourceInfo resourceInfo: resourcesInfo) {
			Assert.assertTrue(resourceInfo.getAccounts().contains(secondAccount));
		}
	}
	
}
