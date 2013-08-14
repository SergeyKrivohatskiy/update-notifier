package net.thumbtack.updateNotifierBackend.database.daosimpl;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import net.thumbtack.updateNotifierBackend.database.daos.TagDAO;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.mappers.TagMapper;

public class TagDAOImpl implements TagDAO {
	
	private final TagMapper mapper;
	
	public TagDAOImpl(SqlSession session) {
		mapper = session.getMapper(TagMapper.class);
	}
	
	public Set<Tag> get(long userId) {
		return mapper.getTags(userId);
	}

	/**
	 * Add tag with <code>name</code> for user with <code>userId</code>
	 * 
	 * @param mapper
	 *            current session mapper
	 * @param userId
	 *            tag owner
	 * @param name
	 *            name of the new tag
	 * @return <code>true</code>, if success; <code>false</code> otherwise
	 */
	public boolean add(Tag tag) {
		return mapper.add(tag) > 0;
	}

	public boolean edit(Tag tag) {
		return mapper.edit(tag) > 0;
	}

	public boolean exists(long userId, List<Long> tagIds) {
		return tagIds.isEmpty() ? true : mapper.check(userId,
				makeString(tagIds)) == tagIds.size();
	}

	public boolean exists(Tag tag) {
		return mapper.checkOne(tag) == 1;
	}
	
	public boolean delete(Tag tag) {
		return mapper.delete(tag) > 0;
	}
	
//	private Set<Tag> tagsMaker(List<Map<String, Object>> list) {
//		Set<Tag> tags = new HashSet<Tag>();
//		for (Map<String, Object> map : list) {
//			// TODO What do you think about BigInteger -> long convertation?
//			tags.add(new Tag(((BigInteger) map.get("id")).longValue(),
//					(String) map.get("name")));
//		}
//		return tags;
//	}

	private <T> String makeString(List<T> array) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (T item : array) {
			stringBuilder.append(item);
			stringBuilder.append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

}
