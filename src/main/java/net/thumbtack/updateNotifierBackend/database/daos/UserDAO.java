package net.thumbtack.updateNotifierBackend.database.daos;

import net.thumbtack.updateNotifierBackend.database.entities.User;

public interface UserDAO extends BaseDAO<User> {

	User get(String email);

	User get2(long id);
	
	boolean exists(long id);
	
}