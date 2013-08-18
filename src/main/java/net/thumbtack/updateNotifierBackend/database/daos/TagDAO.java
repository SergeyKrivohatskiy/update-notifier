package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Tag;

public interface TagDAO extends BaseDAO<Tag> {

	Set<Tag> get(long userId);

	boolean exists(long userId, List<Long> tagIds);

	boolean exists(Tag tag);

	Tag get(Tag tag);
	
}
