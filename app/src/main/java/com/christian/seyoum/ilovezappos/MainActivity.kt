package com.christian.seyoum.ilovezappos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
                println("Network failed")
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                cryptoData = body.toString()
                val jsonArray = JSONArray(cryptoData)
                createGraph(jsonArray)
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

    fun createGraph(jsonArray:JSONArray){
        var i = 0
        val yValue: ArrayList<Entry> = arrayListOf()
        val xAxis = graph.xAxis
        while (i < jsonArray.length()) {

            yValue.add(
                Entry(
                    jsonArray.getJSONObject(i).getString("date").toFloat()
                    , jsonArray.getJSONObject(i).getString("price").toFloat()
                )
            )
            i++
        }
        yValue.reverse()

        xAxis.valueFormatter = (MyValueFormatter())



        val set = LineDataSet(yValue, "Cryptocurrency Graph")
        set.fillAlpha = 0
        val dataset: ArrayList<ILineDataSet> = arrayListOf()
        dataset.add(set)

        val Data = LineData(set)
        graph.isDragEnabled = true
        graph.isScaleXEnabled = true
        graph.setPinchZoom(true)
        graph.setTouchEnabled(true)

        graph.data = Data
        graph.notifyDataSetChanged()
        graph.invalidate()
    }

}

class MyValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return ("").toString()


    }
}


