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
import android.widget.Toast

import de.nuttercode.androidprojectss2018.app.dummy.DummyContent
import de.nuttercode.androidprojectss2018.csi.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [EventListFragment.OnListFragmentInteractionListener] interface.
 */
class EventListFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

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
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                // TODO: Fetch Events here (in separate Thread)
                val eventStore = EventStore(ClientConfiguration())
                eventStore.addStoreListener(object: StoreListener<ScoredEvent> {
                    override fun onElementAdded(newElement: ScoredEvent?) {
                        Toast.makeText(context, "Element hinzugefügt: ${newElement?.event?.name}", Toast.LENGTH_SHORT).show()
                        Log.i("EventListFragment", "Element hinzugefügt: ${newElement?.event?.name}")
                    }

                    override fun onElementRemoved(newElement: ScoredEvent?) {
                        Toast.makeText(context, "Element removed: ${newElement?.event?.name}", Toast.LENGTH_SHORT).show()
                        Log.i("EventListFragment", "Element hinzugefügt: ${newElement?.event?.name}")
                    }
                })
                Log.i("EventListFragment", eventStore.refresh().message)

                adapter = MyEventRecyclerViewAdapter(DummyContent.ITEMS, listener)

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
        fun onListFragmentInteraction(item: Event?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                EventListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
