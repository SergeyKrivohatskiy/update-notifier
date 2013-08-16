package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;

public interface ResourceTagDAO {

	boolean add(long id, long tagId);
	
	List<Long> get(long resourceId);

	boolean delete(long resourceId);
}
