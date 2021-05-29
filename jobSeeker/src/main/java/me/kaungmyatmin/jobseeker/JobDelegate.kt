package me.kaungmyatmin.jobseeker

import me.kaungmyatmin.jobseeker.model.Job

interface JobDelegate {
    fun onJobDetailClicked(job: Job)
}