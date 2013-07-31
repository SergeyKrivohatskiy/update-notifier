package net.thumbtack.updateNotifierBackend.databaseService;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class DatabaseService {
	
	private SessionFactory sessionFactory;
	
	public DatabaseService() {
		try {
			ServiceRegistry serviceRegistry;
	   	
			Configuration configuration = new Configuration();
			configuration.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			sessionFactory = new Configuration().configure().buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
		getAllResources();
	}
	
	// Cast from List to List<ResourceInfo>
	@SuppressWarnings("unchecked")
	public List<ResourceInfo> getAllResources() {
		Session currentSession = null;
		List<ResourceInfo> resourceInfoList;
		try {
			currentSession = sessionFactory.openSession();
			resourceInfoList = currentSession
					.createCriteria(ResourceInfo.class).list();
			return resourceInfoList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}
	
	public Set<ResourceInfo> getResourcesByIdAndTags(Long id, long[] tags) {
		Session currentSession = null;
		
		try {
			currentSession = sessionFactory.openSession();
			AccountInfo account = (AccountInfo) currentSession.get(AccountInfo.class, id);
			if(account == null) {
				return null;
			}
			return account.getResources();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}
	
	private boolean addAccountInfo(AccountInfo accountInfo) {
		Session currentSession = null;
		try {
			currentSession = sessionFactory.openSession();
			accountInfo.setId(new Long(1001));
			Transaction transaction = currentSession.beginTransaction();
			currentSession.save(accountInfo);
			transaction.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}
	
	public Long getAccountIdByEmail(String email) {
		Session currentSession = null;
		try {
			currentSession = sessionFactory.openSession();
			currentSession.beginTransaction();
			Query query = currentSession.createQuery("from AccountInfo where email = :email ").setString("email", email);
			AccountInfo userAccount =  ((AccountInfo) query.uniqueResult());
			currentSession.getTransaction().commit();
			if(userAccount == null) {
				AccountInfo newAccount = new AccountInfo();
				newAccount.setEmail(email);
				return addAccountInfo(newAccount) ? newAccount.getId() : null;
			}
			return userAccount.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}
	
	private boolean addResourceInfo(ResourceInfo resourceInfo) {
		Session currentSession = null;
		try {
			currentSession = sessionFactory.openSession();
			Transaction transaction = currentSession.beginTransaction();
			currentSession.save(resourceInfo);
			transaction.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}
	
	public Set<Category> getCategoriesByUser(long userId) {
		
		Session currentSession = null;

		try {
			currentSession = sessionFactory.openSession();
			AccountInfo account = (AccountInfo) currentSession.get(
					AccountInfo.class, userId);
			if (account == null) {
				return null;
			}
			return account.getCategories();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}

	public boolean appendResource(long userId, ResourceInfo resourceInfo) {
		AccountInfo account = new AccountInfo();
		account.setId(userId);
		resourceInfo.setAccount(account);
		return addResourceInfo(resourceInfo);
	}

	public boolean deleteResource(long resourceId) {
		Session currentSession = null;
		ResourceInfo resourceInfo = new ResourceInfo();
		resourceInfo.setId(resourceId);
		try {
			currentSession = sessionFactory.openSession();
			Transaction transaction = currentSession.beginTransaction();
			currentSession.delete(resourceInfo);
			transaction.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(currentSession != null && currentSession.isOpen()) {
				currentSession.close();
			}
		}
	}

	public Set<ResourceInfo> deleteResourcesByIdAndTags(long userId, long[] tags) {
		// TODO Auto-generated method stub
		return null;
	}

	public void editResource(long userId, long resourceId, ResourceInfo fromJson) {
		// TODO Auto-generated method stub
		
	}

	public void getResource(long userId, long resourceId) {
		// TODO Auto-generated method stub
		
	}

	public void getTags(long userId) {
		// TODO Auto-generated method stub
		
	}
	
}
