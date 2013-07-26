package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.util.List;
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
	
	public boolean addAccountInfo(AccountInfo accountInfo) {
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
	
	public boolean addResourceInfo(ResourceInfo resourceInfo) {
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
