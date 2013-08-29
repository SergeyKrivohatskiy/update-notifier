package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public interface ResourceDAO extends BaseDAO<Resource> {
	
	Resource get(long userId, long resourceId);

	List<Resource> getByUserIdAndTags(Long userId, List<Long> tagIds);

	List<Resource> getAllForUser(long userId);

	Set<Resource> getByscheduleCode(byte scheduleCode);

	boolean updateAfterCheck(Long id, Integer hash);

	boolean deleteByTags(long userId, List<Long> tagIds);

	boolean deleteAll(long userId);

	List<Long> getUpdated(long userId, Date date);
}
