package net.thumbtack.updateNotifierBackend.database.mappers;

import net.thumbtack.updateNotifierBackend.database.entities.User;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	String ADD = "INSERT INTO users (name, surname, email) VALUES (#{name}, #{surname}, #{email})";
	String GET_ID = "SELECT id FROM users WHERE email=#{email}";
	String GET_BY_EMAIL = "SELECT * FROM users WHERE email=#{email}";
	String GET_BY_ID = "SELECT * FROM users WHERE id=#{id}";
	String CHK = "SELECT id FROM users WHERE id=#{id}";
	String DEL = "DELETE FROM users WHERE id = #{id}";
	String DEL_ALL = "DELETE FROM users";
//	String GET_EMAIL = "SELECT email FROM users WHERE id=#{id}";
	
	@Insert(ADD)
	@Options(useGeneratedKeys = true)
	int add(User user);

	@Select(GET_ID)
	Long getId(String email);

	@Select(GET_BY_EMAIL)
	User get(String email);
	
	@Select(GET_BY_ID)
	User get2(long id);
	
	@Select(CHK)
	Long check(long id);

	@Delete(DEL)
	int delete(User user);

	@Delete(DEL_ALL)
	int deleteAll();

}
