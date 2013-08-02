package net.thumbtack.updateNotifierBackend.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	String GET_ID_BY_EMAIL = "SELECT id FROM users WHERE email=#{email}";
	String ADD_USER = "INSERT INTO users VALUE (null, #{email})";
	
	@Select(GET_ID_BY_EMAIL)
	Long getIdByEmail(String email);
	
	@Insert(ADD_USER)
	Long addUser(String email);
}
