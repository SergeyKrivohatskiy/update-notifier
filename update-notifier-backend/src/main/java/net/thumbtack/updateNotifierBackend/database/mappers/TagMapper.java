package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TagMapper {

	String GET_TAGS = "SELECT id, name FROM tags WHERE user_id=#{userId}";
	String ADD_TAG = "INSERT IGNORE INTO tags VALUE (null, #{userId}, #{name})";
	String GET_FOR_RESOURCE = "SELECT id FROM tags JOIN resource_tag ON tags.id = resource_tag.tag_id WHERE resource_id=#{id}";
	String UPD_TAG = "UPDATE tags SET name=#{name} WHERE id=#{id}";

	@Select(GET_TAGS)
	List<Map<String, Object>> getTags(long userId);

	@Insert(ADD_TAG)
	//TODO Do I get 0 only if pair user_id-name exists?
	int addTag(@Param(value = "userId") long userId,
			@Param(value = "name") String name);

	@Update(UPD_TAG)
	int editTag(@Param(value = "id") long id, @Param(value = "name") String name);


}
