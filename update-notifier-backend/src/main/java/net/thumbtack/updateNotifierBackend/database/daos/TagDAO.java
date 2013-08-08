package net.thumbtack.updateNotifierBackend.database.daos;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.mappers.TagMapper;

public class TagDAO {

	public static Set<Tag> getTags(TagMapper mapper, long userId) {
		List<Map<String, Object>> list = mapper.getTags(userId);
		return tagsMaker(list);
		// TODO Auto-generated method stub
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
	public static Long addTag(TagMapper mapper, long userId, String name) {
		boolean result = false;
		try {
			result = mapper.addTag(userId, name) >= 0;
		} catch (Exception e) {
			// TODO exceptions?
			e.printStackTrace();
		}
		return result ? mapper.getLastId() : null;
	}

	/**
	 * Make beautiful
	 * 
	 * @param list
	 * @return
	 */
	private static Set<Tag> tagsMaker(List<Map<String, Object>> list) {
		Set<Tag> tags = new HashSet<Tag>();
		for (Map<String, Object> map : list) {
			// TODO What do you think about BigInteger -> long convertation?
			tags.add(new Tag(((BigInteger) map.get("id")).longValue(),
					(String) map.get("name")));
		}
		return tags;
	}

	public static boolean editTag(TagMapper mapper, long tagId, String tagName) {
		return mapper.editTag(tagId, tagName) > 0;
	}
}
