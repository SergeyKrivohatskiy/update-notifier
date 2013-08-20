package net.thumbtack.updateNotifierBackend.database.daosimpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import net.thumbtack.updateNotifierBackend.database.daos.ResourceTagDAO;
import net.thumbtack.updateNotifierBackend.database.mappers.ResourceTagMapper;

public class ResourceTagDAOImpl implements ResourceTagDAO {

	private final ResourceTagMapper mapper;
	
	public ResourceTagDAOImpl(SqlSession session) {
		mapper = session.getMapper(ResourceTagMapper.class);
	}
	
	public boolean add(long id, long tagId) {
		return mapper.add(id, tagId) > 0;
	}
	
	public List<Long> get(long resourceId) {
		return mapper.get(resourceId);
	}

	public boolean exists(long resourceId) {
		return mapper.exist(resourceId) > 0;
	}
	
	public boolean delete(long resourceId) {
		return mapper.delete(resourceId) > 0;
	}

}
