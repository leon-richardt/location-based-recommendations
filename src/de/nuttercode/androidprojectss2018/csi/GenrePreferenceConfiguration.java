package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * saves all event-genres the user wants to see
 * 
 * @author Johannes B. Latzel
 *
 */
public class GenrePreferenceConfiguration implements Serializable {

	private static final long serialVersionUID = 4061390806054215254L;

	private final Set<Genre> userChoiceGenreSet;

	public GenrePreferenceConfiguration() {
		userChoiceGenreSet = new HashSet<>();
	}

	public GenrePreferenceConfiguration(GenrePreferenceConfiguration genrePreferenceConfiguration) {
		this();
		for (Genre genre : genrePreferenceConfiguration.userChoiceGenreSet)
			addGenre(genre);
	}

	public void addGenre(Genre genre) {
		userChoiceGenreSet.add(genre);
	}

	@Override
	public String toString() {
		return "GenrePreferenceConfiguration [userChoiceGenreSet=" + Arrays.toString(userChoiceGenreSet.toArray())
				+ "]";
	}

}
