package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Tag;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TagMapper {

	// TODO return user id?
	String ADD = "INSERT INTO tags (user_id, name) VALUES (#{userId}, #{name})";
	String GET_TAGS = "SELECT * FROM tags WHERE user_id=#{userId}";
	String GET_BY_IDS = "SELECT * FROM tags WHERE user_id=#{userId} AND id=#{id}";
	String GET_FOR_RESOURCE = "SELECT id FROM tags JOIN resource_tag ON tags.id = resource_tag.tag_id WHERE resource_id=#{id}";
	String UPD = "UPDATE tags SET name=#{name} WHERE id=#{id} AND user_id=#{userId}";
	String DEL = "DELETE FROM tags WHERE id=#{id} AND user_id = #{userId}";
	String CHK_GROUP = "UPDATE tags SET id=id WHERE user_id = #{userId} AND id IN (${tags})";
	String CHK_ONE = "UPDATE tags SET id=id WHERE user_id = #{userId} AND id=#{id}";

	@Insert(ADD)
	@Options(useGeneratedKeys = true)
	int add(Tag tag);

	@Select(GET_BY_IDS)
	Tag getTag(Tag tag);

	@Select(GET_TAGS)
	Set<Tag> getTags(long userId);

	@Update(UPD)
	int edit(Tag tag);

	@Update(CHK_GROUP)
	int check(@Param(value = "userId") long userId,
			@Param(value = "tags") String tags);

	@Update(CHK_ONE)
	int checkOne(Tag tag);

	@Delete(DEL)
	int delete(Tag tag);

}
