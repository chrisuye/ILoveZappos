package com.christian.seyoum.ilovezappos

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bit_coin_price.*

class BitCoinPrice : AppCompatActivity(), IUserControl {
    val jobId: Int = 1
    override lateinit var users:IUserRepo

    override fun add(user: User) {
        users.add(user)
    }

    override fun getUser(): MutableList<User> {
        return users.getUser()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bit_coin_price)
        users = UserRepo(this)

        val bundle = PersistableBundle()
        bundle.putDouble("price",getUser()[0].price.toDouble())

        start_job.setOnClickListener {
            add(User(-1,price_in.text.toString()))
            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(jobId, ComponentName(this, MyJob::class.java))
                .setMinimumLatency(5000)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setExtras(bundle)
                .build()
            val success = scheduler.schedule(jobInfo)
            if(success == JobScheduler.RESULT_SUCCESS) {
                Toast.makeText(this, "Job scheduling succeeded", Toast.LENGTH_SHORT).show()
            }
        }

    }
}