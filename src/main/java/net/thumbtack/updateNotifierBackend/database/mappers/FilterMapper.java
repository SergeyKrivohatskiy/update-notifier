package net.thumbtack.updateNotifierBackend.database.mappers;

import java.util.List;

import net.thumbtack.updateNotifierBackend.database.entities.Filter;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface FilterMapper {
	
	String ADD = "INSERT INTO filters (resource_id, path) VALUES (#{resourceId}, #{path})";
	String DEL = "DELETE * FROM filters WHERE id = #{id}";
	String GET = "SELECT * FROM filters WHERE resource_id = #{resourceId}";
	String GET_AFTER_UPD = "SELECT id FROM resources WHERE user_id=#{userId} AND last_update >= #{date}";

	@Insert(ADD)
	@Options(useGeneratedKeys = true)
	int add(Filter filter);

	@Delete(DEL)
	int delete(Filter filter);

	@Select(GET)
	List<Filter> get(long resourceId);

}
