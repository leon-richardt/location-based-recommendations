package de.nuttercode.androidprojectss2018.csi;

/**
 * provides parameter checks and throws IllegalArgumentException if appropriate
 * 
 * @author Johannes B. Latzel
 *
 */
public final class Assurance {

	/**
	 * @param object
	 * @throws IllegalArgumentException
	 *             if and only if the object is null
	 */
	public static void assureNotNull(Object object) {
		if (object == null)
			throw new IllegalArgumentException("object is null");
	}

	/**
	 * @param object
	 * @throws IllegalArgumentException
	 *             if and only if the object is null or the {@link String} is empty
	 */
	public static void assureNotEmpty(String s) {
		if (s == null || s.isEmpty())
			throw new IllegalArgumentException(s + " empty or null");
	}

	/**
	 * @param object
	 * @throws IllegalArgumentException
	 *             if and only if the argument is not positive
	 */
	public static void assurePositive(long i) {
		if (i <= 0)
			throw new IllegalArgumentException(i + " <= 0");
	}

	/**
	 * @param object
	 * @throws IllegalArgumentException
	 *             if and only if the argument is not positive
	 */
	public static void assurePositive(double i) {
		if (i <= 0)
			throw new IllegalArgumentException(i + " <= 0");
	}

}
