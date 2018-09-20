package de.nuttercode.androidprojectss2018.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent

/**
 * A fragment representing a list of items, representing [ScoredEvent]s.
 * Activities containing this fragment MUST implement the
 * [EventListFragment.OnListFragmentInteractionListener] interface.
 */
class EventListFragment : Fragment() {
    private var columnCount = 1

    private val contentList: ArrayList<ScoredEvent> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            recyclerView = view

            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter = MyEventRecyclerViewAdapter(contentList, listener)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Removes all items from this list.
     * [refreshList] needs to be called in order to make the change visible in the view.
     */
    fun clearList() {
        contentList.clear()
    }

    /**
     * Adds an item to this list.
     * [refreshList] needs to be called in order to make the change visible in the view.
     */
    fun addElement(scoredEvent: ScoredEvent) {
        contentList.add(scoredEvent)
    }

    /**
     * Adds all items to this list.
     * [refreshList] needs to be called in order to make the change visible in the view.
     */
    fun addAllElements(collection: Collection<ScoredEvent>) {
        contentList.addAll(collection)
    }

    /**
     * Updates the view with any changes made to the data set.
     */
    fun refreshList() {
        // notifyDataSetChanged() needs to be run the UI thread
        activity!!.runOnUiThread { recyclerView.adapter!!.notifyDataSetChanged() }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: ScoredEvent?)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }
}
