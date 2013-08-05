package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;

import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;

public class ResourceTagDAO {

	public static void addRelation(ResourceTagMapper mapper, Long id, Long tagId) {
		mapper.add(id, tagId);
	}
	
	public static List<Long> getForResource(ResourceTagMapper mapper, Long id) {
		return mapper.getForResource(id);
	}

	public static List<Long> getResourcesIdsByTags(ResourceTagMapper mapper,
			Long[] tagIds) {
		return mapper.getResourcesIdsByTags(tagIds);
	}
}
