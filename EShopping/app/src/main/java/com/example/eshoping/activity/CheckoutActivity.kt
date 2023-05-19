package com.example.eshoping.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.eshoping.MainActivity
import com.example.eshoping.R
import com.example.eshoping.roomdb.AppDatabase
import com.example.eshoping.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity() , PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_NJz2yMxxSWHjAb");

        val price = intent.getStringExtra("totalCost")

        try {
            val options = JSONObject()
            options.put("name","U.K. Cart")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#FF5454")
            options.put("currency","INR")
/*
            options.put("order_id", "order_DBJOWzybf0sJbb")
*/
            options.put("amount",(price!!.toInt()*100).toString())//pass amount in currency subunits

          /*  val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)
*/
            val prefill = JSONObject()
            prefill.put("email","chetanitcse@gmail.com")
            prefill.put("contact","8533978696")

            options.put("prefill",prefill)
            checkout.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this ,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()

        uploadData()
    }

    private fun uploadData() {
        val id =intent.getStringArrayListExtra("productIds")
        for(currentID in id!!){
            fetchData(currentID)
        }
    }

    private fun fetchData(productID: String?) {

        val dao = AppDatabase.getInstance(this).productDao()
            Firebase.firestore.collection("products")
                .document(productID!!).get().addOnSuccessListener {

                    lifecycleScope.launch(Dispatchers.IO) {
                        dao.deleteProduct(ProductModel(productID))
                    }

                    saveData(it.getString("productName"),
                             it.getString("productSp"),
                             productID)
                }
    }

    private fun saveData(name: String?, price: String?, productID: String) {

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val data = hashMapOf<String, Any>()
        data["name"] = name!!
        data["price"] = price!!
        data["productId"]= productID
        data["status"] = "Ordered"
        data["userId"] =preferences.getString("number","")!!

        val firestore = Firebase.firestore.collection("allOrders")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.add(data).addOnSuccessListener {
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }.addOnFailureListener{
            Toast.makeText(this, "Error"+it.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Error", Toast.LENGTH_SHORT).show()
    }
}
