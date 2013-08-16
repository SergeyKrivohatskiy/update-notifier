package net.thumbtack.updateNotifierBackend.database.mappers;

import net.thumbtack.updateNotifierBackend.database.entities.User;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	String ADD_USER = "INSERT INTO users (email) VALUES (#{email})";
	String GET_ID = "SELECT id FROM users WHERE email=#{email}";
	String GET = "SELECT * FROM users WHERE email=#{email}";
	String GET2 = "SELECT * FROM users WHERE id=#{id}";
	String CHECK_EXISTENCE = "SELECT id FROM users WHERE id=#{id}";
	String DEL = "DELETE FROM users WHERE id = #{id}";
	String DEL_ALL = "DELETE FROM users";
//	String GET_EMAIL = "SELECT email FROM users WHERE id=#{id}";
	
	@Insert(ADD_USER)
	@Options(useGeneratedKeys = true)
	int add(User user);

	@Select(GET_ID)
	Long getId(String email);

	@Select(GET)
	User get(String email);
	
	@Select(GET2)
	User get2(long id);
	
	@Select(CHECK_EXISTENCE)
	Long check(long id);

	@Delete(DEL)
	int delete(User user);

	@Delete(DEL_ALL)
	int deleteAll();

}
