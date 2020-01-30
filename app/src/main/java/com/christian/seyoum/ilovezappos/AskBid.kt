package com.christian.seyoum.ilovezappos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ask_bid.*
import okhttp3.*
import java.io.IOException

class AskBid : AppCompatActivity() {
    /*
        using OKHttp to fetch json from the given url
        for parsing we are not using a JSONArray bc tht failed. instead we are self parsing the information
        then we will use runonUIthread to populate the recyclerview.
         */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_bid)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val url = "https://www.bitstamp.net/api/v2/order_book/btcusd/"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("network","The Network is unstable")
            }

            override fun onResponse(call: Call, response: Response) {
                var bids:String
                var asks:String
                val crypro:MutableList<CryptoFormat> = mutableListOf()
                val body = response?.body()?.string()
                var cryptoData = body.toString()

                cryptoData = cryptoData.replace("[","")
                cryptoData = cryptoData.replace("]","")
                cryptoData = cryptoData.removeRange(0,cryptoData.indexOf("bids"))
                bids = cryptoData.substring(0,cryptoData.indexOf("asks"))
                asks = cryptoData.substring(cryptoData.indexOf("asks"), cryptoData.length -1 )
                bids = bids.removeRange(0, bids.indexOf(" "))
                asks = asks.removeRange(0, asks.indexOf(" "))
                bids = bids.replace("\"", "")
                asks =asks.replace("\"", "")

                val bid = bids.split(",")
                val ask = asks.split(",")

                if (bid.size > ask.size){
                    var n = 0
                    while (n < ask.size-1){
                        if (ask[n].isEmpty()){
                            crypro.add(
                                CryptoFormat(
                                    bid[n], bid[n + 1]
                                    , "", ""
                                )
                            )
                        }
                        else {
                            crypro.add(
                                CryptoFormat(
                                    bid[n], bid[n + 1]
                                    , ask[n], ask[n + 1]
                                )
                            )
                        }
                        n = n + 2
                    }
                }
                else{
                    var n = 0
                    while (n < bid.size-1){
                        if (bid[n].isEmpty()){
                            crypro.add(
                                CryptoFormat(
                                    "", ""
                                    , ask[n], ask[n + 1]
                                )
                            )
                        }
                        else {
                            crypro.add(
                                CryptoFormat(
                                    bid[n], bid[n + 1]
                                    , ask[n], ask[n + 1]
                                )
                            )
                        }
                        n = n + 2
                    }
                }

                runOnUiThread {
                    recyclerview.adapter = MainAdapter(crypro)
                }



            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.bid_ask_menu, menu)
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
            R.id.price_menu -> {
                val intent = Intent(this,BitCoinPrice::class.java)
                startActivity(intent)
                return true
            }
            R.id.refresh_bid_ask -> {
                finish()
                startActivity(getIntent())
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

}
