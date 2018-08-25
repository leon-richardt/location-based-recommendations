package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * saves all event-genres the user wants to see
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagPreferenceConfiguration implements Serializable, Iterable<Tag> {

	private static final long serialVersionUID = 4061390806054215254L;

	private final Set<Tag> userChoiceTagSet;

	public TagPreferenceConfiguration() {
		userChoiceTagSet = new HashSet<>();
	}

	public TagPreferenceConfiguration(TagPreferenceConfiguration genrePreferenceConfiguration) {
		this();
		for (Tag tag : genrePreferenceConfiguration.userChoiceTagSet)
			addTag(tag);
	}

	public void addTag(Tag tag) {
		userChoiceTagSet.add(tag);
	}

	/**
	 * @param tags
	 * @return true if any configured genre is contained in the given Collection
	 */
	public boolean containsAny(Collection<Tag> tags) {
		for (Tag tag : this)
			if (tags.contains(tag))
				return true;
		return false;
	}

	@Override
	public String toString() {
		return "GenrePreferenceConfiguration [userChoiceTagSet=" + Arrays.toString(userChoiceTagSet.toArray())
				+ "]";
	}

	@Override
	public Iterator<Tag> iterator() {
		return userChoiceTagSet.iterator();
	}

}
