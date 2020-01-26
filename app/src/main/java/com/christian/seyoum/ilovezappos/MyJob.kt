package com.christian.seyoum.ilovezappos

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread


class MyJob : JobService(){
    val i = 0

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(job: JobParameters?): Boolean {

        Toast.makeText(this, "Received job request", Toast.LENGTH_SHORT).show()

        val id = THREAD_ID++
        val p = job?.extras?.getDouble("price")
        // services by default run on the UI thread
        thread {

            println("Job received")
            try {
                while (i == 0 ){
                    Thread.sleep(5000)
                    println("Job received running")
                    val url = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd/"

                    val request = Request.Builder().url(url).build()

                    val client = OkHttpClient()


                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("network fail")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val body = response?.body()?.string()
                            var price = ""
                            println(body)
                            price = body.toString()
                            price = price.substring(price.indexOf("last"), price.indexOf("timestamp"))
                            price = price.replace("\"","")
                            price = price.substring(price.indexOf(":") + 1, price.indexOf(","))
                            price = price.replace(" ","")

                            println(price)
                            if (p.toString().isEmpty()){
                                println("price is empty")
                            }
                            else {
                                println("Price by user $p")
                            }


                        }

                    })
                }
            } catch(e: Exception) {
                // for when we get shut down...
            } finally {
            }
        }
        return false
    }

    companion object {
        var THREAD_ID = 0
    }

}