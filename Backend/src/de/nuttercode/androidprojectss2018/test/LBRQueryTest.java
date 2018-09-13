package de.nuttercode.androidprojectss2018.test;

import java.util.ArrayList;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.EventStore;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.TagStore;
import de.nuttercode.androidprojectss2018.csi.TagUserChoice;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultInformation;
import de.nuttercode.androidprojectss2018.example.StoreListenerExample;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRQueryTest {

	public static void main(String[] args) {

		// client config
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setRadius(10);

		// eventstore and config
		EventStore eventStore = new EventStore(clientConfiguration);
		eventStore.setUserLocation(52.3, 8.045);
		eventStore.addStoreListener(new StoreListenerExample<>());

		// tagstore for tagfilter testing
		TagStore tagStore = new TagStore(clientConfiguration);
		QueryResultInformation resultInformation = tagStore.refresh();
		if (!resultInformation.isOK())
			throw new IllegalStateException(resultInformation.toString());
		ArrayList<Tag> tags = new ArrayList<>(tagStore.getAll());
		if (tags.size() < 2)
			throw new IllegalStateException();

		// actual tests
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().addTag(tags.get(0));
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().addTag(tags.get(1));
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(1), TagUserChoice.Like);
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(0), TagUserChoice.Like);
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(1), TagUserChoice.Deny);
		System.out.println(eventStore.refresh());
		for (ScoredEvent scoredEvent : eventStore.getAll())
			System.out.println(scoredEvent);

	}

}
