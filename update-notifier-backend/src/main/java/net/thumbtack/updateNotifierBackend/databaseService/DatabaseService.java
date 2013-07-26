package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.util.List;

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
		getResourcesInfo();
	}
	
	// Cast from List to List<ResourceInfo>
	@SuppressWarnings("unchecked")
	public List<ResourceInfo> getResourcesInfo() {
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
	
	// Cast from List to List<ResourceInfo>
	@SuppressWarnings("unchecked")
	public List<ResourceInfo> getResourcesInfoByAccountId(Long id) {
		Session currentSession = null;
		
		try {
			currentSession = sessionFactory.openSession();
			currentSession.beginTransaction();
			Query query = currentSession.createQuery(" select r "
		               + " from ResourceInfo r INNER JOIN r.accounts account"
		               + " where account.id = :accountId ").setLong("accountId", id);
			List<ResourceInfo> resourceInfoList =  (List<ResourceInfo>) query.list();
			currentSession.getTransaction().commit();
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
	
	private boolean addAccountInfo(AccountInfo accountInfo) {
		Session currentSession = null;
		try {
			currentSession = sessionFactory.openSession();
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
	
}
