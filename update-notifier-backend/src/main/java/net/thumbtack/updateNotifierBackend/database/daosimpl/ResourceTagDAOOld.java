package net.thumbtack.updateNotifierBackend.database.daosimpl;

import java.util.List;

import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;

public class ResourceTagDAOOld {

	public static boolean addRelation(ResourceTagMapper mapper, Long id, Long tagId) {
		return mapper.add(id, tagId) > 0;
	}
	
	public static List<Long> getForResource(ResourceTagMapper mapper, Long id) {
		return mapper.getForResource(id);
	}

	public static boolean deleteRelations(ResourceTagMapper mapper, Long resourceId) {
		return mapper.delete(resourceId) > 0;
	}
}
