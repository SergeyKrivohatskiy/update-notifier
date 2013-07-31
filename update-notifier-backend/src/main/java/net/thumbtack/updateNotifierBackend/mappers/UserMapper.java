package net.thumbtack.updateNotifierBackend.mappers;

public interface UserMapper {

	Long getIdByEmail(String email);
	void addUser(String email);
}
