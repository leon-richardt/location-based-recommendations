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
	 * @param s
	 * @throws IllegalArgumentException
	 *             if and only if the object is null or the {@link String} is empty
	 */
	public static void assureNotEmpty(String s) {
		if (s == null || s.isEmpty())
			throw new IllegalArgumentException(s + " empty or null");
	}

	/**
	 * @param i
	 * @throws IllegalArgumentException
	 *             if and only if the argument is not positive
	 */
	public static void assurePositive(long i) {
		if (i <= 0)
			throw new IllegalArgumentException(i + " <= 0");
	}

	/**
	 * @param i
	 * @throws IllegalArgumentException
	 *             if and only if the argument is negative
	 */
	public static void assureNotNegative(long i) {
		if (i < 0)
			throw new IllegalArgumentException(i + " < 0");
	}

	/**
	 * @param i
	 * @throws IllegalArgumentException
	 *             if and only if the argument is negative
	 */
	public static void assureNotNegative(double i) {
		if (i < 0)
			throw new IllegalArgumentException(i + " < 0");
	}

	/**
	 * @param i
	 * @throws IllegalArgumentException
	 *             if and only if the argument is not positive
	 */
	public static void assurePositive(double i) {
		if (i <= 0)
			throw new IllegalArgumentException(i + " <= 0");
	}

	/**
	 * 
	 * @param value
	 * @param leftBoundary
	 * @param rightBoundary
	 * @throws IllegalArugmentException
	 *             if score is not element of [leftBoundary, rightBoundary]
	 */
	public static void assureBoundaries(double value, double leftBoundary, double rightBoundary) {
		if (value < leftBoundary || value > rightBoundary)
			throw new IllegalArgumentException("score is not element of [" + leftBoundary + ", " + rightBoundary + "]");
	}

}
