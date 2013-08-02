package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.HashSet;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Tag;
import net.thumbtack.updateNotifierBackend.database.mappers.TagMapper;

public class TagDAO {

	public static Set<Tag> getTags(TagMapper tagMapper, long userId) {
		return new HashSet<Tag>();
		// TODO Auto-generated method stub
	}

}
