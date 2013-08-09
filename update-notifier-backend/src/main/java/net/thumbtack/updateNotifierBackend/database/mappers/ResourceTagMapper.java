package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ResourceTagMapper {

	String ADD = "INSERT INTO resource_tag VALUE (#{id},#{tagId})";
	String GET_TAG_IDS_FOR_RESOURCE = "SELECT tag_id FROM resource_tag WHERE resource_id = #{id}";
	String DEL_ALL = "DELETE FROM resource_tag";
	
	@Insert(ADD)
	int add(@Param(value = "id") Long id, @Param(value = "tagId") Long tagId);

	@Select(GET_TAG_IDS_FOR_RESOURCE)
	List<Long> getForResource(Long id);
	
}
