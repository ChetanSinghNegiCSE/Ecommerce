package com.example.eshoping.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.eshoping.activity.ProductDetailsActivity
import com.example.eshoping.databinding.LayoutProductItemBinding
import com.example.eshoping.model.AddProductModel
import java.util.ArrayList
import java.util.Currency

class ProductAdapter (val context : Context , val list: ArrayList<AddProductModel>)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
    val binding =  LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent , false)
    return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
    return  list.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
    val data = list[position]
        val currencySymbol = Currency.getInstance("INR").symbol

        Glide.with(context).load(data.productCoverImg).into(holder.binding.imageView2)
        holder.binding.textView2.text = data.productName
        holder.binding.textView3.text = data.productCategory
        holder.binding.textView4.text = currencySymbol+data.productMrp
        holder.binding.textView4.paintFlags = holder.binding.textView4.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.binding.button.text= currencySymbol+data.productSp

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("id",list[position].productID)
            context.startActivity(intent)
        }
    }
    }
