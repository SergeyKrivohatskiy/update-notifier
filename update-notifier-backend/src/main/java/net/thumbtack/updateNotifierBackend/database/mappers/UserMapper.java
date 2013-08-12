package net.thumbtack.updateNotifierBackend.database.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	String GET_ID = "SELECT id FROM users WHERE email=#{email}";
	String ADD_USER = "INSERT INTO users VALUE (null, #{email})";
	String GET_EMAIL = "SELECT email FROM users WHERE id=#{id}";
	String DEL_ALL = "DELETE FROM users";
	String CHECK_EXISTENCE = "SELECT id FROM users WHERE id=#{id}";
	
	@Select(GET_ID)
	Long getId(String email);
	
	@Insert(ADD_USER)
	Long addUser(String email);

	@Select(GET_EMAIL)
	String getEmail(Long id);

	@Delete(DEL_ALL)
	int deleteAll();

	@Select(CHECK_EXISTENCE)
	Long check(Long id);
}
