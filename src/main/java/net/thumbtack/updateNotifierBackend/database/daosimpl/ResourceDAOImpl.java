package net.thumbtack.updateNotifierBackend.database.daosimpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import net.thumbtack.updateNotifierBackend.database.daos.ResourceDAO;
import net.thumbtack.updateNotifierBackend.database.entities.Filter;
import net.thumbtack.updateNotifierBackend.database.entities.Page;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.mappers.FilterMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.PageMapper;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceMapper;

public class ResourceDAOImpl implements ResourceDAO {

	private final ResourceMapper mapper;
	private final FilterMapper filterMapper;
	// private final AttributeMapper attributeMapper;
	private final PageMapper pageMapper;

	public ResourceDAOImpl(SqlSession session) {
		mapper = session.getMapper(ResourceMapper.class);
		filterMapper = session.getMapper(FilterMapper.class);
		// attributeMapper = session.getMapper(AttributeMapper.class);
		pageMapper = session.getMapper(PageMapper.class);
	}

	private boolean addResourcePage(Resource resource) {
		// Don't know, how to get id of the added row other than with
		// automapping, but automapping want an object. Isn't it?
		Page page = new Page(resource.getPage());
		boolean result = pageMapper.saveFor(page) > 0;
		if (result) {
			resource.setPageId(page.getId());
		}
		return result;
	}

	private boolean updateResourcePage(Resource resource) {
		long pageId = resource.getPageId();
		Page page = new Page(pageId, resource.getPage());
		return pageMapper.update(page) > 0;
	}

	private boolean deleteResourcePage(Resource resource) {
		boolean result = pageMapper.remove(resource.getPageId()) > 0;
		if (result) {
			resource.setId((long) 0);
		}
		return result;
	}

	public boolean add(Resource resource) {
		boolean result1 = addResourcePage(resource);

		boolean result2 = result1 && mapper.add(resource) > 0;
		if (result1) {
			if (!result2) {
				deleteResourcePage(resource);
			} else {
				for (Filter f : resource.getFilters()) {
					// if(result) {
					// result = result && filterMapper.add(f) > 0;
					f.setResourceId(resource.getId());
					filterMapper.add(f);
					// for (String attrName : f.getAttrs()) {
					// // result = result && attributeMapper.add(attr) > 0;
					// attributeMapper.add(f.getId(), attrName);
					// }
					// }
				}
			}
		}
		return result1 && result2;
	}

	public Resource get(long userId, long resourceId) {
		// List<Filter> filters = filterMapper.get(resourceId);
		// for(Filter f : filters) {
		// f.setAttrs(attributeMapper.get(f.getId()));
		// }
		Resource resource = mapper.get(userId, resourceId);
		// resource.setFilters(filters);
		return resource;
		// return mapper.get(userId, resourceId);
	}

	public List<Resource> getByUserIdAndTags(Long userId, List<Long> tagIds) {
		return mapper.getByUserIdAndTags(makeString(tagIds));
	}

	public List<Resource> getAllForUser(long userId) {
		return mapper.getAllForUser(userId);
	}

	public Set<Resource> getByscheduleCode(byte scheduleCode) {
		Set<Resource> resources = mapper.getByscheduleCode(scheduleCode);
		for (Resource resource : resources) {
			List<Filter> filters = filterMapper.get(resource.getId());
			// for (Filter f : filters) {
			// f.setAttrs(attributeMapper.get(f.getId()));
			// }
			resource.setFilters(filters);
		}
		// TODO Does it return collection of resources?
		return resources;
	}

	public boolean edit(Resource resource) {
		boolean result1 = updateResourcePage(resource);

		boolean result2 = mapper.update(resource) > 0;
		if (result1) {
			if (!result2) {
				System.err.println("Oops");
				deleteResourcePage(resource);
			} else {
				for (Filter f : resource.getFilters()) {
					f.setResourceId(resource.getId());
					filterMapper.add(f);
				}
			}
		}
		return result1 && result2;

	}

	public boolean updateAfterCheck(Resource resource) {
		updateResourcePage(resource);
		return mapper.updateAfterUpdate(resource.getId(), resource.getHash(),
				new Date()) > 0;
	}

	public boolean exists(Resource resource) {
		return mapper.check(resource);
	}

	public boolean delete(Resource resource) {
		return mapper.delete(resource) > 0;
	}

	public boolean deleteByTags(long userId, List<Long> tagIds) {
		return mapper.deleteByTags(userId, makeString(tagIds)) > 0;
	}

	public boolean deleteAll(long userId) {
		return mapper.deleteAll(userId) > 0;
	}

	private <T> String makeString(List<T> array) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (T item : array) {
			stringBuilder.append(item);
			stringBuilder.append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	public List<Long> getUpdated(long userId, Date date) {
		// TimeZone zone = TimeZone.getDefault();
		// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		List<Long> result = mapper.getUpdated(userId, date);
		// TimeZone.setDefault(zone);
		return result;
	}

	public String loadPage(long pageId) {
		String page = pageMapper.loadFor(pageId);
		return page == null ? "" : page;
	}

}
