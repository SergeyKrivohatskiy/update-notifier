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
	String ADD_TAG = "INSERT INTO tags (user_id, name) VALUES (#{userId}, #{name})";
	String GET_TAGS = "SELECT * FROM tags WHERE user_id=#{userId}";
	String GET_TAG = "SELECT * FROM tags WHERE user_id=#{userId} AND id=#{id}";
	String GET_FOR_RESOURCE = "SELECT id FROM tags JOIN resource_tag ON tags.id = resource_tag.tag_id WHERE resource_id=#{id}";
	String UPD_TAG = "UPDATE tags SET name=#{name} WHERE id=#{id} AND user_id=#{userId}";
	String DEL_TAG = "DELETE FROM tags WHERE id=#{id} AND user_id = #{userId}";
	String CHECK_EXISTANCE = "UPDATE tags SET id=id WHERE user_id = #{userId} AND id IN (${tags})";
	String CHECK_EXISTS_ONE = "UPDATE tags SET id=id WHERE user_id = #{userId} AND id=#{id}";

	@Insert(ADD_TAG)
	@Options(useGeneratedKeys = true)
	int add(Tag tag);

	@Select(GET_TAG)
	Tag getTag(Tag tag);

	@Select(GET_TAGS)
	Set<Tag> getTags(long userId);

	@Update(UPD_TAG)
	int edit(Tag tag);

	@Update(CHECK_EXISTANCE)
	int check(@Param(value = "userId") long userId,
			@Param(value = "tags") String tags);

	@Update(CHECK_EXISTS_ONE)
	int checkOne(Tag tag);

	@Delete(DEL_TAG)
	int delete(Tag tag);

}
