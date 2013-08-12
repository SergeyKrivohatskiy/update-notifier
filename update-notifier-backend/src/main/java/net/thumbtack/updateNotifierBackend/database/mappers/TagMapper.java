package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TagMapper {

	// TODO return user id?
	String GET_TAGS = "SELECT id, name FROM tags WHERE user_id=#{userId}";
	String ADD_TAG = "INSERT INTO tags VALUE (null, #{userId}, #{name})";
	String GET_FOR_RESOURCE = "SELECT id FROM tags JOIN resource_tag ON tags.id = resource_tag.tag_id WHERE resource_id=#{id}";
	String UPD_TAG = "UPDATE tags SET name=#{name} WHERE id=#{id} AND user_id=#{userId}";
	String DEL_TAG = "DELETE FROM tags WHERE id=#{tagId} AND user_id = #{userId}";
	String LAST_ID = "SELECT LAST_INSERT_ID()";
	String CHECK_EXISTANCE = "UPDATE tags SET id=id WHERE id IN (${tags})";

	@Select(GET_TAGS)
	List<Map<String, Object>> getTags(long userId);

	@Insert(ADD_TAG)
	// TODO Do I get 0 only if pair user_id-name exists?
	int addTag(@Param(value = "userId") long userId,
			@Param(value = "name") String name);

	@Select(LAST_ID)
	Long getLastId();

	@Update(UPD_TAG)
	int editTag(@Param(value = "userId") long userId,
			@Param(value = "id") long id, @Param(value = "name") String name);

	@Update(CHECK_EXISTANCE)
	int check(String tags);

	@Delete(DEL_TAG)
	int deleteTag(@Param(value = "userId") long userId,
			@Param(value = "tagId") long tagId);

}
