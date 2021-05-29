package me.kaungmyatmin.jobseeker.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import me.kaungmyatmin.jobseeker.JobAdapter
import me.kaungmyatmin.jobseeker.JobDelegate
import me.kaungmyatmin.jobseeker.R
import me.kaungmyatmin.jobseeker.model.Job
import me.kaungmyatmin.jobseeker.rest.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity(), JobDelegate {
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = getSharedPreferences("default_pref", Context.MODE_PRIVATE)
        val language = pref.getString("language", "en")?:"en"
        Log.d("tag", "language $language")
        setNewLocale(language)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jobAdapter = JobAdapter(this)

        title = "Find Your Job Today"

        val jobsPerRow = resources.getInteger(R.integer.jobs_per_row)
        val gridLayoutManager = GridLayoutManager(this, jobsPerRow)

        rvJob.apply {
            adapter = jobAdapter
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
        }

        searchPosition("kotlin")
    }

    private fun searchPosition(query: String?) {
        RestClient.getApiService()
            .getJobsByPosition(query ?: "Kotlin")
            .enqueue(object : Callback<List<Job>> {
                override fun onResponse(call: Call<List<Job>>, response: Response<List<Job>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            jobAdapter.setNewData(it)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Job>>, t: Throwable) {
                    Log.e("network",t.message?:"Unknown error")
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView?
        searchView?.let {
            it.queryHint = getString(R.string.search_position)
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchPosition(query)
                    menu?.findItem(R.id.search)?.collapseActionView()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_en -> {
                setNewLocale("en")
                reOpenApp()
            }
            R.id.menu_my -> {
                setNewLocale("my")
                reOpenApp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onJobDetailClicked(job: Job) {
        val intent = JobDetailActivity.newIntent(this,job.id)
        startActivity(intent)
    }

    private fun setNewLocale(language: String){
        val locale = Locale(language)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)
        val pref = getSharedPreferences("default_pref", Context.MODE_PRIVATE)
        pref.edit()
            .putString("language", language)
            .apply()
    }

    private fun reOpenApp(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }
}