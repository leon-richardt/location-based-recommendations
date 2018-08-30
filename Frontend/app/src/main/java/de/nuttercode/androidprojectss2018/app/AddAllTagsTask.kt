package de.nuttercode.androidprojectss2018.app

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.TagStore
import de.nuttercode.androidprojectss2018.csi.query.QueryResultState
import java.lang.ref.WeakReference

class AddAllTagsTask(private val context: WeakReference<Context>, private val callback: AddAllTagsTaskCallback): AsyncTask<ClientConfiguration, Unit, ClientConfiguration>() {

    private var qrs: QueryResultState = QueryResultState.OK

    override fun doInBackground(vararg clientConfiguration: ClientConfiguration): ClientConfiguration {
        if (clientConfiguration.size != 1) throw IllegalArgumentException("Only one argument may be passed")

        val clientConfig = clientConfiguration[0]

        val tagPrefs = clientConfig.tagPreferenceConfiguration
        val tagStore = TagStore(clientConfig)
        val queryResultInformation = tagStore.refresh()

        if (queryResultInformation.queryResultState != QueryResultState.OK) {
            Log.e(TAG, "QueryResultState is ${queryResultInformation.queryResultState.name}. Message: ${queryResultInformation.message}")
            qrs = queryResultInformation.queryResultState
        }

        Log.i(TAG, "Refreshing TagStore was successful! Now adding all (${tagStore.all.size}) Tags to the TagPreferenceConfiguration.")
        for (tag in tagStore.all) tagPrefs.addTag(tag)
        Log.i(TAG, "Added all Tags to the TagPreferenceConfiguration.")

        return clientConfig
    }

    override fun onPostExecute(result: ClientConfiguration) {
        callback.processAddAllTagsResult(result)
        if (qrs != QueryResultState.OK) {
            Toast.makeText(context.get(),
                    "Bad QueryResultState: $qrs! Please check your Internet connection and try again!",
                    Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val TAG = "AddAllTagsTask"
    }
}

interface AddAllTagsTaskCallback {
    fun processAddAllTagsResult(result: ClientConfiguration)
}
