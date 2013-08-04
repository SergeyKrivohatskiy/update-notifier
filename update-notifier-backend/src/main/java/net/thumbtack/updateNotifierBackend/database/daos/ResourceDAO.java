package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.thumbtack.updateNotifierBackend.database.StringMaker.*;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;

public class ResourceDAO {

	public static boolean add(ResourceMapper mapper, long userId,
			Resource resource) {
		mapper.add(userId, resource);
		// TODO check that after addition resource have not-null id
		// TODO make return value
		return false;
	}

	public static boolean delete(ResourceMapper mapper, long resourceId) {
		mapper.delete(resourceId);
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Return list of resources for specified user and tags. If
	 * <code>tagsId</code> is null, result is all resources of user with
	 * <code>userId</code>; otherwise list of all resources with specified tags
	 * will be returned.
	 * 
	 * @param mapper
	 *            current session mapper
	 * @param userId
	 * @param tagsId
	 *            array of tag ids
	 * @return all user resources, if tag id array is null, all resources with
	 *         specified tags otherwise
	 */
	public static List<Resource> getByUserIdAndTags(ResourceMapper mapper,
			Long userId, Long[] tagsId) {
		List<Resource> list = null;
		if (tagsId == null) {
			list = mapper.getAllForUser(userId);

		} else {
			// TODO doesn't work yet
//			list = mapper.getByUserIdAndTags(makeString(tagsId));
		}
		if (list == null) {
			list = Collections.emptyList();
		}
		return list;
	}

	public static Set<Resource> getBySheduleCode(ResourceMapper mapper,
			byte sheduleCode) {
		// TODO Does it return collection of resources?
//		return new HashSet<Resource>();
		Set<Resource> list = mapper.getBySheduleCode(sheduleCode);
		if (list == null) {
			list = Collections.emptySet();
		}
		return list;
	}

	public static boolean deleteByTags(ResourceMapper mapper, long userId,
			Long[] tagsId) {
		if (tagsId == null) {
			mapper.deleteAll(userId);
		} else {
			mapper.deleteByTags(userId);
		}
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean edit(ResourceMapper mapper, long userId,
			Resource resource) {
		mapper.update(userId, resource);
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean updateHash(ResourceMapper mapper, Long id,
			Integer hash) {
		boolean result = false;
		result = mapper.updateHash(id, hash) > 0;
		return result;
	}
}
