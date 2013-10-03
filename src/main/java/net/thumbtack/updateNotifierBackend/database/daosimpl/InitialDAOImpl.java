package net.thumbtack.updateNotifierBackend.database.daosimpl;

import org.apache.ibatis.session.SqlSession;

import net.thumbtack.updateNotifierBackend.database.daos.InitialDAO;
import net.thumbtack.updateNotifierBackend.database.mappers.InitialMapper;

public class InitialDAOImpl implements InitialDAO {

	private InitialMapper mapper;

	public InitialDAOImpl(SqlSession session) {
		mapper = session.getMapper(InitialMapper.class);
	}
	
	public void godMode() {
		mapper.createUserCounter();
		mapper.createUserTable();
		
		mapper.createResourceCounter();
		mapper.createResourceTable();
		
		mapper.createTagCounter();
		mapper.createTagTable();
		
		mapper.createResourceTagTable();
		
		mapper.createFiltersInc();
		mapper.createFiltersTbl();

		mapper.createAttributesInc();
		mapper.createAttributesTbl();
	}

}
