package net.thumbtack.updateNotifierBackend.database;

public class StringMaker {

	public static <T> String makeString(T[] array) {
		StringBuilder stringBuilder = new StringBuilder("(");
		for (T item : array) {
			stringBuilder.append(item);
			stringBuilder.append(",");
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
