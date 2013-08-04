package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TagMapper {

	String GET_TAGS = "SELECT id, name FROM tags WHERE user_id=#{userId}";
	String ADD_TAG = "INSERT IGNORE INTO tags VALUE (null, #{userId}, #{name})";

	@Select(GET_TAGS)
	List<Map<String, Object>> getTags(long userId);

	@Insert(ADD_TAG)
	//TODO Do I get 0 only if pair user_id-name exists?
	int addTag(@Param(value = "userId") long userId,
			@Param(value = "name") String name);

}
