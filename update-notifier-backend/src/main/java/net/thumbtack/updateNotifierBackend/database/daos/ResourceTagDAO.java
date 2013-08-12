package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;

import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;

public class ResourceTagDAO {

	public static boolean addRelation(ResourceTagMapper mapper, Long id, Long tagId) {
		return mapper.add(id, tagId) > 0;
	}
	
	public static List<Long> getForResource(ResourceTagMapper mapper, Long id) {
		return mapper.getForResource(id);
	}
}
