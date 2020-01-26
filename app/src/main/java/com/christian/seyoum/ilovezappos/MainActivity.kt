package com.christian.seyoum.ilovezappos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_ask_bid.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var cryptoData: String? = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = "https://www.bitstamp.net/api/v2/transactions/btcusd/"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("not tre")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                println(body)
                val yValue:ArrayList<Entry> = arrayListOf()
                cryptoData = body.toString()
                val jsonArray = JSONArray(cryptoData)
                var i = 0

                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                var date = java.util.Date(1532358895 * 1000)
                sdf.format(date)


                runOnUiThread {
                    while (i < jsonArray.length()){
                        date = java.util.Date(jsonArray.getJSONObject(i).getString("date").toLong())
                        yValue.add(Entry(jsonArray.getJSONObject(i).getString("date").toFloat()
                            ,jsonArray.getJSONObject(i).getString("price").toFloat()))
                        i++
                    }
                    yValue.reverse()

                    println(cryptoData)

                    val set = LineDataSet(yValue, "Date Set 1")

                    set.fillAlpha = 0

                    val dataset:ArrayList<ILineDataSet> = arrayListOf()

                    dataset.add(set)

                    val Data = LineData(dataset)

                    graph.data = Data
                }
                graph.isDragEnabled = true
                graph.isScaleXEnabled = false



            }

        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.ask_bid_menu -> {
                val intent = Intent(this, AskBid::class.java)
                startActivity(intent)
                return true
            }
            R.id.price_menu -> {
                val intent = Intent(this,BitCoinPrice::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }
}
