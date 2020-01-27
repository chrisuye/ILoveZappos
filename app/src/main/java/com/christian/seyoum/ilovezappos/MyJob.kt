package com.christian.seyoum.ilovezappos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread


class MyJob : JobService(){
    private val i = 0

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(job: JobParameters?): Boolean {

        Toast.makeText(this, "Received job request", Toast.LENGTH_SHORT).show()

        val p = job?.extras?.getDouble("price")

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
                            //showNotification()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val body = response?.body()?.string()
                            var price:String
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
                                if(price.toDouble() > p.toString().toDouble()){

                                    showNotification()


                                }
                            }



                        }

                    })
                }
            } catch(e: Exception) {
                // for when we get shut down...
            }
        }
        return false
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PRICE NOTIFICATION"
            val descriptionText = "Price has dropped"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIF_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val intent = Intent(this,BitCoinPrice::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("ILoveZappos")
            .setContentText("The price of Bit Coin has dropped")
            .setSmallIcon(android.R.drawable.btn_default)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
        val notification = builder.build()

        NotificationManagerCompat.from(this)
            .notify(NOTIF_ID, notification)




    }

    override fun onCreate() {
        super.onCreate()

        //Toast.makeText(this, "JobService created!", Toast.LENGTH_SHORT).show()
        createNotificationChannel()
    }


    companion object {
        val NOTIF_CHANNEL_ID = "com.christian.seyoum.ilovezappos"
        val NOTIF_ID = 1
    }

}