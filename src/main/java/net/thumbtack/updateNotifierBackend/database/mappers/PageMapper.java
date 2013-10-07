package net.thumbtack.updateNotifierBackend.database.mappers;

import net.thumbtack.updateNotifierBackend.database.entities.Page;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PageMapper {

	String LOAD = "SELECT page FROM pages WHERE id = #{id}";
	String SAVE = "INSERT INTO pages (page) VALUES (#{page})";
	String UPDATE = "UPDATE pages SET page = #{page} WHERE id = #{id}";
	String DELETE = "DELETE FROM pages WHERE id = #{id}";

	@Insert(SAVE)
	@Options(useGeneratedKeys = true)
	long saveFor(Page page);

	@Select(LOAD)
	String loadFor(long id);
	
	@Update(UPDATE)
	long update(Page page);

	@Delete(DELETE)
	long remove(long id);
}
