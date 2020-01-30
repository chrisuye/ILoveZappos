package com.christian.seyoum.ilovezappos

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bit_coin_price.*
import okhttp3.*
import java.io.IOException

class BitCoinPrice : AppCompatActivity(), IUserControl {
    private val jobId: Int = 1
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
        /*
        using OKHttp to fetch json from the given url
        for parsing we are not using a JSONArray bc tht failed. instead we are self parsing the information
        then we will use runonUIthread to show the last price.
         */

        val url = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd/"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("network","The Network is unstable")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val price:String

                price = body.toString()
                var last = (price.substring(price.indexOf("last"), price.indexOf("timestamp")))

                last = last.replace("\"","")
                last = last.substring(last.indexOf(":") + 1, last.indexOf(","))
                last = last.replace(" ","")
                runOnUiThread {
                    last_view.text = "The curent price of Bitcoin is $last. \n" +
                            "Set an Alert below to check the price of Bitcoin by the hour"
                }

            }

        })

        val bundle = PersistableBundle()

        start_job.setOnClickListener {
            scheduler.cancelAll()
            add(User(-1,price_in.text.toString()))
            bundle.putDouble("price",getUser()[getUser().size - 1].price.toDouble())
            val jobInfo = JobInfo.Builder(jobId, ComponentName(this, MyJob::class.java))
                .setMinimumLatency(0)
                .setPersisted(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setExtras(bundle)
                .build()
            val success = scheduler.schedule(jobInfo)
            if(success == JobScheduler.RESULT_SUCCESS) {
                Toast.makeText(this, "Alert succeeded", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.price_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
        menu buttons to move to other activities and refresh activity in order to get the most
        current data. refresh will cloth the current activity and reload it.
         */

        when(item.itemId){
            R.id.graph_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.ask_bid_menu -> {
                val intent = Intent(this, AskBid::class.java)
                startActivity(intent)
                return true
            }
            R.id.refresh_price -> {
                finish()
                startActivity(getIntent())
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }
}
