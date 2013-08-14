package net.thumbtack.updateNotifierBackend.database.daosimpl;

import org.apache.ibatis.session.SqlSession;

import net.thumbtack.updateNotifierBackend.database.daos.UserDAO;
import net.thumbtack.updateNotifierBackend.database.entities.User;
import net.thumbtack.updateNotifierBackend.database.mappers.UserMapper;

public class UserDAOImpl implements UserDAO {

	private final UserMapper mapper;
	
	public UserDAOImpl(SqlSession session) {
		mapper = session.getMapper(UserMapper.class);
	}
	
	public boolean add(User user) {
		return mapper.add(user) > 0;
	}
	
	public User get(String email) {
		return mapper.get(email);
	}

	public User get2(Long id) {
		return mapper.get2(id);
	}
	
	public boolean exists(Long id) {
		return (mapper.check(id) != null);
	}
	
	public boolean delete(User user) {
		return mapper.delete(user) > 0;
	}
}
