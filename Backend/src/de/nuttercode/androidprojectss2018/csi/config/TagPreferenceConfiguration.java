package de.nuttercode.androidprojectss2018.csi.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.pojo.Tag;

/**
 * saves all {@link Tag}s and the users configurations regarding the specific
 * {@link Tag}s
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagPreferenceConfiguration implements Serializable {

	private static final long serialVersionUID = 4061390806054215254L;

	private final Map<Integer, TagUserChoice> userChoiceTagMap;

	public TagPreferenceConfiguration() {
		userChoiceTagMap = new HashMap<>();
	}

	/**
	 * adds a tag (if the tag is not already configured herein) with
	 * {@link TagUserChoice#Accept} configuration
	 * 
	 * @param tag
	 * @throws IllegalArgumentException
	 *             if tag is null
	 */
	public void addTag(Tag tag) {
		Assurance.assureNotNull(tag);
		Integer id = tag.getId();
		if (!userChoiceTagMap.containsKey(id))
			userChoiceTagMap.put(id, TagUserChoice.Accept);
	}

	/**
	 * adds a tag (if the tag is not already configured herein) with the the
	 * specified tagUserChoice
	 * 
	 * @param tag
	 * @param tagUserChoice
	 * @throws IllegalArgumentException
	 *             if tag or tagUserChoice is null
	 */
	public void setTag(Tag tag, TagUserChoice tagUserChoice) {
		Assurance.assureNotNull(tag);
		Assurance.assureNotNull(tagUserChoice);
		userChoiceTagMap.put(tag.getId(), tagUserChoice);
	}

	/**
	 * 
	 * @param tag
	 * @return the {@link TagUserChoice} or {@link TagUserChoice#Accept} if the
	 *         {@link Tag} is not configured
	 * @throws IllegalArgumentException
	 *             if tag is null
	 */
	public TagUserChoice getUserChoice(Tag tag) {
		Assurance.assureNotNull(tag);
		Integer id = tag.getId();
		if (userChoiceTagMap.containsKey(id))
			return userChoiceTagMap.get(id);
		return TagUserChoice.Accept;
	}

	/**
	 * @param tags
	 * @return true if any configured tag is contained in the given Collection and
	 *         is not denied by the client
	 */
	public boolean containsAny(Collection<Tag> tags) {
		Integer id;
		for (Tag tag : tags) {
			id = tag.getId();
			if (userChoiceTagMap.containsKey(id) && userChoiceTagMap.get(id) != TagUserChoice.Deny)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "TagPreferenceConfiguration [userChoiceTagSet=" + userChoiceTagMap + "]";
	}

}
