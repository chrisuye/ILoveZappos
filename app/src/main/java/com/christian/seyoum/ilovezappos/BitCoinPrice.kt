package com.christian.seyoum.ilovezappos

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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

        val url = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd/"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("network fail")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val price:String

                price = body.toString()
                var high = (price.substring(price.indexOf("high"), price.indexOf("last")))
                var last = (price.substring(price.indexOf("last"), price.indexOf("timestamp")))
                var bid = (price.substring(price.indexOf("bid"), price.indexOf("vwap")))
                var vwap = (price.substring(price.indexOf("vwap"), price.indexOf("volume")))
                var volume = (price.substring(price.indexOf("volume"), price.indexOf("low")))
                var low = (price.substring(price.indexOf("low"), price.indexOf("ask")))
                var ask = (price.substring(price.indexOf("ask"), price.indexOf("open")))
                var open = (price.substring(price.indexOf("open"), price.indexOf("}")))

                high = high.replace("\"","")
                high = high.substring(high.indexOf(":") + 1, high.indexOf(","))
                high = high.replace(" ","")



                last = last.replace("\"","")
                last = last.substring(last.indexOf(":") + 1, last.indexOf(","))
                last = last.replace(" ","")



                bid = bid.replace("\"","")
                bid = bid.substring(bid.indexOf(":") + 1, bid.indexOf(","))
                bid= bid.replace(" ","")



                vwap = vwap.replace("\"","")
                vwap = vwap.substring(vwap.indexOf(":") + 1, vwap.indexOf(","))
                vwap= vwap.replace(" ","")



                volume = volume.replace("\"","")
                volume = volume.substring(volume.indexOf(":") + 1, volume.indexOf(","))
                volume = volume.replace(" ","")



                low = low.replace("\"","")
                low = low.substring(low.indexOf(":") + 1, low.indexOf(","))
                low = low.replace(" ","")



                ask = ask.replace("\"","")
                ask = ask.substring(ask.indexOf(":") + 1, ask.indexOf(","))
                ask = ask.replace(" ","")



                open = open.replace("\"","")
                open = open.substring(open.indexOf(":") + 1, open.length )
                open = open.replace(" ","")



                runOnUiThread {

                    high_view.text = high
                    open_view.text = open
                    a_view.text = ask
                    low_view.text = low
                    volume_view.text = volume
                    vwap_view.text = vwap
                    b_view.text = bid
                    last_view.text = last
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

            else -> return super.onOptionsItemSelected(item)
        }

    }
}
