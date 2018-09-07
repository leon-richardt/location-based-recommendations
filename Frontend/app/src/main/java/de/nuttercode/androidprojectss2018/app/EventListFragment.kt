package de.nuttercode.androidprojectss2018.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.nuttercode.androidprojectss2018.csi.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [EventListFragment.OnListFragmentInteractionListener] interface.
 */
class EventListFragment : Fragment(), FetchEventsTaskCallback {
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

    fun clearList() {
        contentList.clear()
    }

    fun addElement(scoredEvent: ScoredEvent) {
        contentList.add(scoredEvent)
    }

    fun addAllElements(collection: Collection<ScoredEvent>) {
        contentList.addAll(collection)
    }

    fun refreshList() {
        // notifyDataSetChanged() needs to be run the UI thread
        Log.i(TAG, "activity = $activity, recyclerView.adapter = ${recyclerView.adapter}")
        activity!!.runOnUiThread { recyclerView.adapter!!.notifyDataSetChanged() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun processFetchEventsResult(result: ArrayList<ScoredEvent>) {
        contentList.addAll(result)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: ScoredEvent?)
    }

    companion object {
        const val TAG = "EventListFragment"

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                EventListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
