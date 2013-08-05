package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public interface ResourceMapper {

	String INS_RESOURCE = "INSERT INTO resources VALUE (null, #{userId}, #{url}, #{sheduleCode}, #{hash})";
	String DEL_RESOURCE = "DELETE FROM resources WHERE id=#{resourceId}";
	String GET_ALL_FOR_USER = "SELECT * FROM resources WHERE user_id=#{userId}";
	// TODO next doesn't work
	String GET_BY_USER_ID_AND_TAGS = "SELECT resources.* FROM resources INNER JOIN resource_tag ON resources.id = resource_tag.resource_id WHERE tag_id in #{tagsId}";
	String GET_BY_SHEDULE_CODE = "SELECT * FROM resources WHERE shedule_code=#{sheduleCode}";
	String DEL_ALL = "DELETE FROM resources WHERE id=#{userId}";
	String DEL_BY_TAGS = "";
	String UPD_RESOURCE = "";
	String UPD_HASH = "UPDATE resources SET hash=#{hash} WHERE id=#{id}";

	@Insert(INS_RESOURCE)
	@Options(useGeneratedKeys = true)
	// TODO check that after addition resource have not-null id
	void add(long userId, Resource resourceInfo);

	@Delete(DEL_RESOURCE)
	void delete(long resourceId);

	@Select(GET_ALL_FOR_USER)
	List<Resource> getAllForUser(Long userId);

	@Select(GET_BY_USER_ID_AND_TAGS)
	List<Resource> getByUserIdAndTags(String tagsId);

	@Select(GET_BY_SHEDULE_CODE)
	Set<Resource> getBySheduleCode(byte sheduleCode);

	@Delete(DEL_ALL)
	void deleteAll(long userId);

	@Delete(DEL_BY_TAGS)
	void deleteByTags(long userId);

	@Update(UPD_RESOURCE)
	void update(long userId, Resource resource);

	@Update(UPD_HASH)
	int updateHash(@Param(value = "id") Long id,
			@Param(value = "hash") Integer hash);

}
