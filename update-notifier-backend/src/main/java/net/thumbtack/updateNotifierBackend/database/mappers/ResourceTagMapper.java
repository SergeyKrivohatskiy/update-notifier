package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ResourceTagMapper {

	String ADD = "INSERT INTO resource_tag VALUE (#{id},#{tagId})";
	String GET_TAG_IDS_FOR_RESOURCE = "SELECT tag_id FROM resource_tag WHERE resource_id = #{id}";
	String GET_RESOURCES_IDS_BY_TAGS = "SELECT id FROM resources WHERE NOT EXISTS (SELECT id FROM tags WHERE tags.id IN (1,2,3) AND NOT EXISTS (SELECT * FROM resource_tag WHERE resource_id=resources.id AND tag_id = tags.id))";
	
	@Insert(ADD)
	public void add(@Param(value = "id") Long id, @Param(value = "tagId") Long tagId);

	@Select(GET_TAG_IDS_FOR_RESOURCE)
	List<Long> getForResource(Long id);

	@Select(GET_RESOURCES_IDS_BY_TAGS)
	public List<Long> getResourcesIdsByTags(@Param(value = "tagIds") Long[] tagIds);

}
