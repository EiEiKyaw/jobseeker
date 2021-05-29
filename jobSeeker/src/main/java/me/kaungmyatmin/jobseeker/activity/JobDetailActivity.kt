package me.kaungmyatmin.jobseeker.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_job_detail.*
import me.kaungmyatmin.jobseeker.R
import me.kaungmyatmin.jobseeker.model.Job
import me.kaungmyatmin.jobseeker.rest.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDetailActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context, jobId: String): Intent {
            return Intent(context, JobDetailActivity::class.java)
                .putExtra("jobId", jobId)
        }
    }

    private fun getJobId() = intent.getStringExtra("jobId") ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        title = "Apply Your Job Now!"
        RestClient.getApiService()
            .getJobById(getJobId())
            .enqueue(object : Callback<Job> {
                override fun onResponse(call: Call<Job>, response: Response<Job>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            populateUi(it)
                        }
                    }
                }

                override fun onFailure(call: Call<Job>, t: Throwable) {

                }
            })
    }

    private fun populateUi(job: Job) {
        tvTitle.text = job.title
        tvApply.text = HtmlCompat.fromHtml(job.applicationMethod, HtmlCompat.FROM_HTML_MODE_COMPACT)
        tvCompany.text = job.company
        Glide.with(this)
            .load(job.companyLogo)
            .error(R.drawable.job_search)
            .into(ivCompany)

        tvDescription.text = HtmlCompat.fromHtml(job.description, HtmlCompat.FROM_HTML_MODE_COMPACT)

        tvLink.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(job.company_url)
            startActivity(i)
        }

        tvType.text = job.type
        tvLocation.text = job.location




    }
}