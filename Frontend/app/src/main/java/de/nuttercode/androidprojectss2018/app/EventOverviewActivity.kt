package de.nuttercode.androidprojectss2018.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent

class EventOverviewActivity : AppCompatActivity() {

    private lateinit var scoredEvent: ScoredEvent    // ScoredEvent because we might want to incorporate the score at some point in the future
    private lateinit var eventNameView: TextView
    private lateinit var eventDescriptionView: TextView
    private lateinit var venueNameView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_overview)
        scoredEvent = obtainMostRecentEventStore()!!.getById(intent.getIntExtra(EXTRA_EVENT_CLICKED, -1))

        eventNameView = findViewById<TextView>(R.id.event_name).apply { text = scoredEvent.event.name }
        venueNameView = findViewById<TextView>(R.id.venue_name).apply { text = scoredEvent.event.venue.name }
        eventDescriptionView = findViewById<TextView>(R.id.event_description).apply { text = scoredEvent.event.description }
    }
}
