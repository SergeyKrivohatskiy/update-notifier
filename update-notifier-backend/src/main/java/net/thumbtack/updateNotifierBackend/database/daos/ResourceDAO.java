package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;

public class ResourceDAO {

	public static Long add(ResourceMapper mapper,
			Resource resource) {
		mapper.add(resource);
		Long id = mapper.getLastId();
		return id == null ? 0 : id;
	}

	public static boolean delete(ResourceMapper mapper, long resourceId) {
		return mapper.delete(resourceId) > 0;
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
			list = mapper.getByUserIdAndTags(makeString(tagsId));
		}
		return list;
	}

	public static Set<Resource> getBySheduleCode(ResourceMapper mapper,
			byte sheduleCode) {
		// TODO Does it return collection of resources?
		return mapper.getBySheduleCode(sheduleCode);
	}

	public static boolean deleteByTags(ResourceMapper mapper, long userId,
			Long[] tagsId) {
		boolean result = false;
		if (tagsId == null) {
			result = mapper.deleteAll(userId) > 0;
		} else {
			result = mapper.deleteByTags(makeString(tagsId)) > 0;
		}
		return result;
	}

	public static boolean edit(ResourceMapper mapper,
			Resource resource) {
		return mapper.update(resource) > 0;
	}

	public static boolean updateHash(ResourceMapper mapper, Long id,
			Integer hash) {
		return mapper.updateHash(id, hash) > 0;
	}

	public static Resource get(ResourceMapper mapper, long resourceId) {
		return mapper.get(resourceId);
	}
	
	private static <T> String makeString(T[] array) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (T item : array) {
			stringBuilder.append(item);
			stringBuilder.append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length()-1);
		return stringBuilder.toString();
	}
}
