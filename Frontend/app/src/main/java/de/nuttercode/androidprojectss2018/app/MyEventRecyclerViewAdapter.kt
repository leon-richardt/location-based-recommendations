package de.nuttercode.androidprojectss2018.app

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import de.nuttercode.androidprojectss2018.app.EventListFragment.OnListFragmentInteractionListener
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent


import kotlinx.android.synthetic.main.fragment_event.view.*

/**
 * [RecyclerView.Adapter] that can display a [ScoredEvent] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyEventRecyclerViewAdapter(
        private val mValues: List<ScoredEvent>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ScoredEvent
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.event.name
        holder.mContentView.text = item.event.venue.name

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.event_name
        val mContentView: TextView = mView.event_venue_name

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
