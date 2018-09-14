package de.nuttercode.androidprojectss2018.app

import android.app.job.JobParameters
import android.app.job.JobService

class UpdateEventsJobService: JobService() {
    private lateinit var mUpdateEventsTask: UpdateEventsTask

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mUpdateEventsTask = object: UpdateEventsTask(this@UpdateEventsJobService) {
            override fun onPostExecute(result: Boolean) {
                // Tell the JobScheduler that the job is finished and that the wakelock can be released
                jobFinished(jobParameters, result)
            }
        }

        mUpdateEventsTask.execute()
        // Tell the JobScheduler not to release the wakelock as the Task is probably still running
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        mUpdateEventsTask.cancel(true)
        // Tell the JobScheduler to reschedule this job
        return true
    }

}