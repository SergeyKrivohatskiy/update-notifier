package net.thumbtack.updateNotifierBackend.database.daos;

import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;

public class UserDAO {

	/**
	 * Get user id by email from base, create if user with such email is not exist.
	 * @param mapper instance of current session <code>UserMapper</code>
	 * @param email user email
	 * @return existing or new user id
	 */
	public static Long getUserId(UserMapper mapper, String email) {
		Long userId = mapper.getIdByEmail(email);
		if (userId == null) {
			mapper.addUser(email);
			//TODO what about exception on add?
			userId = mapper.getIdByEmail(email);
		}
		return userId;
	}
}
