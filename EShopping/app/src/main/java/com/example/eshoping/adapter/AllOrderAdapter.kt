package com.example.eshoping.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eshoping.R
import com.example.eshoping.databinding.AllOrderItemLayoutBinding
import com.example.eshoping.model.AllOrderModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Currency

class AllOrderAdapter (val list: ArrayList<AllOrderModel>, val context: Context)
    : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {


    inner class AllOrderViewHolder(val binding : AllOrderItemLayoutBinding):
            RecyclerView.ViewHolder(binding.root) {
        val progressBar: LinearProgressIndicator = binding.progressBar
    }
    private fun updateProgressBar(progressBar: LinearProgressIndicator, status: String) {
        when (status) {
            "Ordered" -> progressBar.progress = 25
            "Dispatched" -> progressBar.progress = 50
            "Delivered" -> progressBar.progress = 100
            else -> progressBar.progress = 0
        }
    }

  /*  fun updateProgressBar(holder: RecyclerView.ViewHolder, status: String) {


        when (status) {
            "Ordered" -> progressBar.progress = 25
            "Dispatched" -> progressBar.progress = 50
            "Delivered" -> progressBar.progress = 100
            else -> progressBar.progress = 0
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        return AllOrderViewHolder(AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        val currencySymbol = Currency.getInstance("INR").symbol
        holder.binding.productTitle.text = list[position].name
        holder.binding.productPrice.text = "Price :"+currencySymbol+list[position].price
        /*holder.binding.productPrice.text = list[position].price*/
        Glide.with(context).load(list[position].img).into(holder.binding.image)


        when (list[position].status){
            "Ordered" ->{

            holder.binding.productStatus.text = "Ordered"
            }

            "Dispatched" ->{

                holder.binding.productStatus.text = "Dispatched"

            }

            "Delivered" ->{
                holder.binding.productStatus.text = "Delivered"
            }

            "Canceled" ->{
                holder.binding.productStatus.text = "Canceled"
            }
        }

        val currentItem = list[position]

        // Bind other views...

        currentItem.status?.let { updateProgressBar(holder.progressBar, it) }


    }



}