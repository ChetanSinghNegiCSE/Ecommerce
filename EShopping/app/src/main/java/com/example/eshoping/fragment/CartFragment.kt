package com.example.eshoping.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.eshoping.R
import com.example.eshoping.activity.AddressActivity
import com.example.eshoping.activity.CategoryActivity
import com.example.eshoping.adapter.CartAdapter
import com.example.eshoping.databinding.FragmentCartBinding
import com.example.eshoping.roomdb.AppDatabase
import com.example.eshoping.roomdb.ProductModel

class CartFragment : Fragment() {

    private lateinit var binding : FragmentCartBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        val preferences = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("isCart",false)
        editor.apply()


        val dao = AppDatabase.getInstance(requireContext()).productDao()

        dao.getAllProducts().observe(requireActivity()){
            binding.cardRecycler.adapter = CartAdapter(requireContext(), it)

            totalCost(it)
        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total = 0
        for(item in data!!){
            total += item.productSp!!.toInt()
        }

        binding.textView12.text = "Total item in cart is ${data.size}"
        binding.textView13.text = "Total Cost : $total"

        binding.checkout.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra("totalCost",total)
            startActivity(intent)
        }

    }

}