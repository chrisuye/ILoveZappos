package com.christian.seyoum.ilovezappos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.askbid_view.view.*

class MainAdapter(val cryptoFormat: MutableList<CryptoFormat>): RecyclerView.Adapter<CustomViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context).inflate(R.layout.askbid_view, parent, false)
        val viewHolder = CustomViewHolder(layoutInflater)

        return viewHolder
    }

    override fun getItemCount(): Int {
        return cryptoFormat.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cry = cryptoFormat[position]
        holder?.view?.bid_view?.text = cry.bid
        holder?.view?.amount_bid?.text = cry.bidAmount
        holder?.view?.ask_view?.text = cry.ask
        holder?.view?.amount_ask?.text = cry.askAmount
    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}