package net.thumbtack.updateNotifierBackend.database.daos;

import static net.thumbtack.updateNotifierBackend.database.StringMaker.*;
import net.thumbtack.updateNotifierBackend.database.mappers.TagResourceMapper;

public class ResourceTagDAO {

	public static boolean addRelations(TagResourceMapper mapper, Long id, Long[] tagsIdArray) {
		
		mapper.add(id, makeString(tagsIdArray));
		// TODO Auto-generated method stub
		return false;
	}

	public static Long[] getResIdsByTags(Long[] tagsIds) {
		return tagsIds;
		// TODO Auto-generated method stub
		
	}

	public static long[] getTagsIdsByRes(long[] resIds) {
		return resIds;
		// TODO Auto-generated method stub
		// TODO Do you need this?
	}

}
