package de.nuttercode.androidprojectss2018.app

import android.app.job.JobParameters
import android.app.job.JobService

class UpdateTagsJobService: JobService() {
    private lateinit var mUpdateTagsTask: UpdateTagsTask

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mUpdateTagsTask = object: UpdateTagsTask(this@UpdateTagsJobService) {
            override fun onPostExecute(result: Boolean) {
                // Tell the JobScheduler that the job is finished and that the wakelock can be released
                jobFinished(jobParameters, result)
            }
        }

        mUpdateTagsTask.execute()
        // Tell the JobScheduler not to release the wakelock as the Task is probably still running
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        mUpdateTagsTask.cancel(true)
        // Tell the JobScheduler to reschedule this job
        return true
    }
}