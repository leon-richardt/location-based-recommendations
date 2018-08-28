package de.nuttercode.androidprojectss2018.app.dummy

import android.util.Log
import de.nuttercode.androidprojectss2018.csi.Event
import de.nuttercode.androidprojectss2018.csi.Venue
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Event> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Event> = HashMap()

    private val COUNT = 24

    init {
        // Add some sample items.
        for (i in 0..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: Event) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id.toString(), item)
    }

    private fun createDummyItem(position: Int): Event {
        Log.i("DummyContent", "position = $position")
        return Event(Venue("Venue Description", position, "Venue #$position", 0.0, 0.0, 0.0), "Event #$position",
                "Event at Venue #$position", position)
    }

}
