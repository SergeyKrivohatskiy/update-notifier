package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;

public class ResourceDAO {

	public static boolean addResource(long userId, Resource resourceInfo) {
		// TODO Auto-generated method stub
		// TODO useGeneratedKey should be here
		return false;
	}

	public static boolean delete(ResourceMapper mapper, long resourceId) {
		// TODO Auto-generated method stub
		return false;
	}

	public static List<Resource> getResources(ResourceMapper mapper,
			Long userId, long[] tags) {
		// TODO tags array may be null
		return new LinkedList<Resource>();
	}

	public static Set<Resource> getResources(ResourceMapper mapper,
			int sheduleCode) {
		// TODO Auto-generated method stub
		return new HashSet<Resource>();
	}

	public static boolean deleteByTags(ResourceMapper mapper, long userId,
			long[] tagsId) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean edit(ResourceMapper mapper, long userId,
			Resource resource) {
		// TODO Auto-generated method stub
		return false;
	}
}
