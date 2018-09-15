package de.nuttercode.androidprojectss2018.test;

import java.util.ArrayList;

import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.config.TagUserChoice;
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.pojo.Tag;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultInformation;
import de.nuttercode.androidprojectss2018.csi.store.EventStore;
import de.nuttercode.androidprojectss2018.csi.store.TagStore;
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
		eventStore.refresh();
		clientConfiguration.getTagPreferenceConfiguration().addTag(tags.get(0));
		eventStore.refresh();
		clientConfiguration.getTagPreferenceConfiguration().addTag(tags.get(1));
		eventStore.refresh();
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(1), TagUserChoice.LIKE);
		eventStore.refresh();
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(0), TagUserChoice.LIKE);
		clientConfiguration.getTagPreferenceConfiguration().setTag(tags.get(1), TagUserChoice.DISLIKE);
		eventStore.refresh();
		for (ScoredEvent scoredEvent : eventStore.getAll())
			System.out.println(scoredEvent);

	}

}
