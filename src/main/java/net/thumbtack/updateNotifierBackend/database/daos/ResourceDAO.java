package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public interface ResourceDAO {
	
	boolean add(Resource resource);

	Resource get(long userId, long resourceId);

	List<Resource> getByUserIdAndTags(Long userId, List<Long> tagIds);

	List<Resource> getAllForUser(long userId);

	Set<Resource> getByscheduleCode(byte scheduleCode);

	boolean edit(Resource resource);

	boolean updateAfterCheck(Long id, Integer hash);

	boolean exists(long resourceId);

	boolean delete(Resource resource);

	boolean deleteByTags(long userId, List<Long> tagIds);

	boolean deleteAll(long userId);
}
