package net.thumbtack.updateNotifierBackend.database.daos;

import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;

public class UserDAO {

	/**
	 * Get user id by email from base, create if user with such email is not exist.
	 * @param mapper instance of current session <code>UserMapper</code>
	 * @param email user email
	 * @return existing or new user id
	 */
	public static Long getIdOrAdd(UserMapper mapper, String email) {
		Long userId = mapper.getId(email);
		if (userId == null) {
			mapper.addUser(email);
			//TODO what about exception on add?
			userId = mapper.getId(email);
		}
		return userId;
	}

	public static String getUserEmail(UserMapper mapper, Long id) {
		return mapper.getEmail(id);
	}

	public static boolean exists(UserMapper mapper, Long id) {
		return (mapper.check(id) != null);
	}
	
}
