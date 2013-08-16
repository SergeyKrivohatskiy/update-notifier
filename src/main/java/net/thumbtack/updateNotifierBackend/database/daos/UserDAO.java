package net.thumbtack.updateNotifierBackend.database.daos;

import net.thumbtack.updateNotifierBackend.database.entities.User;

public interface UserDAO {

	boolean add(User user);
	
	User get(String email);

	User get2(long id);
	
	boolean exists(long id);
	
	boolean delete(User user);
}