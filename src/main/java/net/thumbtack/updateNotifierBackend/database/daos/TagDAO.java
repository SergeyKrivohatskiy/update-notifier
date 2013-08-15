package net.thumbtack.updateNotifierBackend.database.daos;

import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Tag;

public interface TagDAO {

	Set<Tag> get(long userId);

	boolean add(Tag tag);

	boolean edit(Tag tag);

	boolean exists(long userId, List<Long> tagIds);

	boolean exists(Tag tag);
	
	boolean delete(Tag tag);
	
}
