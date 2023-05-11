package com.example.eshoping.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.eshoping.R
import com.example.eshoping.adapter.CategoryProductAdapter
import com.example.eshoping.adapter.ProductAdapter
import com.example.eshoping.model.AddProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        
        getProducts(intent.getStringExtra("cat"))
    }

    private fun getProducts(category : String?) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products").whereEqualTo("productCategory",category)
            .get().addOnSuccessListener {
                list.clear()
                for( doc in it.documents){
                    val data = doc.toObject(AddProductModel :: class.java)
                    list.add(data!!)
                }
                var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
               recyclerView.adapter = CategoryProductAdapter(this,list)
            }

    }
}