package me.kaungmyatmin.jobseeker.rest


import me.kaungmyatmin.jobseeker.model.Job
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("positions.json")
    fun getJobsByPosition(@Query("search") q: String): Call<List<Job>>

    @GET("positions/{jobId}.json")
    fun getJobById(@Path("jobId") jobId: String): Call<Job>
}