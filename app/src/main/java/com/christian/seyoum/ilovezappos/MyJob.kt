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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread


class MyJob : JobService(){
    /*
    This is the jobService for my notification of price changed.
    we are using i to create an endless loop that runs every hour in the background
    when we notice that price has dropped a notification will be sent to the user
     */
    private val i = 0
    private val hour:Long = 60 * 60 * 1000

    override fun onStopJob(job: JobParameters?): Boolean {

        return false
    }

    override fun onStartJob(job: JobParameters?): Boolean {

        val p = job?.extras?.getDouble("price")

        thread {

            try {
                while (i == 0 ){
                    Thread.sleep(hour)
                    val url = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd/"

                    val request = Request.Builder().url(url).build()

                    val client = OkHttpClient()


                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d("network","The Network is unstable")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val body = response?.body()?.string()
                            var price:String

                            price = body.toString()
                            price = price.substring(price.indexOf("last"), price.indexOf("timestamp"))
                            price = price.replace("\"","")
                            price = price.substring(price.indexOf(":") + 1, price.indexOf(","))
                            price = price.replace(" ","")

                            if (p.toString().isNotEmpty()){
                                if(price.toDouble() < p.toString().toDouble()){

                                    showNotification(price.toDouble(), p.toString().toDouble())
                                }
                            }

                        }

                    })
                }
            } catch(e: Exception) {

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

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(price:Double, p:Double) {

        val dif = "%.2f".format(p - price).toDouble()
        val intent = Intent(this,BitCoinPrice::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("ILoveZappos")
            .setContentText("The price of BitCoin has dropped by $dif to current low price of $price")
            .setSmallIcon(android.R.drawable.btn_default)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
        val notification = builder.build()

        NotificationManagerCompat.from(this)
            .notify(NOTIF_ID, notification)




    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    companion object {
        val NOTIF_CHANNEL_ID = "com.christian.seyoum.ilovezappos"
        val NOTIF_ID = 1
    }

}