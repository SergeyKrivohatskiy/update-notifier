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

	String INS_RESOURCE = "INSERT INTO resources VALUE (null, #{userId}, #{url}, #{sheduleCode}, #{dom_path}, #{filter}, #{hash}, NOW())";
	String DEL_RESOURCE = "DELETE FROM resources WHERE id=#{resourceId}";
	String GET_ALL_FOR_USER = "SELECT * FROM resources WHERE user_id=#{id}";
	String GET_BY_ID = "SELECT * FROM resources WHERE id=#{id}";
	String GET_BY_USER_ID_AND_TAGS = "SELECT DISTINCT * FROM resources WHERE NOT EXISTS (SELECT id FROM tags WHERE tags.id IN (${tagIds}) AND NOT EXISTS (SELECT * FROM resource_tag WHERE resource_id=resources.id AND tag_id = tags.id))";
	String GET_BY_SHEDULE_CODE = "SELECT * FROM resources WHERE shedule_code=#{sheduleCode}";
	String DEL_ALL_USER_RESOURCES = "DELETE FROM resources WHERE id=#{id}";
	String DEL_BY_TAGS = "DELETE FROM resources WHERE NOT EXISTS (SELECT id FROM tags WHERE tags.id IN (${tagIds}) AND NOT EXISTS (SELECT * FROM resource_tag WHERE resource_id=resources.id AND tag_id = tags.id))";
	String UPD_RESOURCE = "UPDATE resources SET user_id=#{userId}, url=#{url}, dom_path=#{dom_path}, filter=#{filter}, shedule_code=#{sheduleCode} WHERE id=#{id}";
	String UPD_AFTER_UPD = "UPDATE resources SET hash=#{hash}, last_update=NOW() WHERE id=#{id}";
	String LAST_ID = "SELECT LAST_INSERT_ID()";
	String DEL_ALL_RESOURCES = "DELETE FROM resources";

	@Insert(INS_RESOURCE)
	@Options(useGeneratedKeys = true)
	// TODO check that after addition resource have not-null id
	void add(Resource resource);

	@Delete(DEL_RESOURCE)
	int delete(long resourceId);

	@Select(GET_ALL_FOR_USER)
	List<Resource> getAllForUser(Long id);

	@Select(GET_BY_USER_ID_AND_TAGS)
	List<Resource> getByUserIdAndTags(@Param(value = "tagIds") String tagIds);

	@Select(GET_BY_SHEDULE_CODE)
	Set<Resource> getBySheduleCode(byte sheduleCode);

	@Delete(DEL_ALL_USER_RESOURCES)
	int deleteAll(long id);
	

	@Delete(DEL_BY_TAGS)
	int deleteByTags(@Param(value = "tagIds") String tagIds);

	@Update(UPD_RESOURCE)
	int update(Resource resource);

	@Update(UPD_AFTER_UPD)
	int updateAfterUpdate(@Param(value = "id") Long id,
			@Param(value = "hash") Integer hash);

	@Select(LAST_ID)
	Long getLastId();

	@Select(GET_BY_ID)
	Resource get(Long id);

//	@Delete(DEL_ALL_RESOURCES)
//	int deleteAll();
}
