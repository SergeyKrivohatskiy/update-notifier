package net.thumbtack.updateNotifierBackend.database.daos;

public interface BaseDAO<T> {

	boolean add(T obj);

	boolean edit(T obj);

	boolean delete(T obj);

}
