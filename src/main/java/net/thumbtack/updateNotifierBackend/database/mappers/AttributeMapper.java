package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AttributeMapper {

	String ADD = "INSERT INTO attributes (filter_id, attr_name) VALUES (#{filterId}, #{attrName})";
	String DEL = "DELETE FROM attributes WHERE id = #{id}";
	String GET = "SELECT attr_name FROM attributes WHERE filter_id = #{filterId}";
	String DEL_BY_NAME = "DELETE FROM attributes WHERE name=#{attr_name}";

	@Insert(ADD)
	int add(@Param(value = "filterId") Long filterId, @Param(value = "attrName") String attrName);

	@Select(GET)
	Set<String> get(long filterId);
	
	@Delete(DEL)
	int delete(long id);

	@Delete(DEL_BY_NAME)
	int deleteByName(String attrName);

}
