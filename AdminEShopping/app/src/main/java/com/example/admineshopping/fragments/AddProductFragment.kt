package com.example.admineshopping.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isEmpty
import com.example.admineshopping.R
import com.example.admineshopping.adapter.AddProductImageAdapter
import com.example.admineshopping.databinding.FragmentAddProductBinding
import com.example.admineshopping.model.AddProductModel
import com.example.admineshopping.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class  AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter
    private var coverImage: Uri ? = null
    private lateinit var dialog : Dialog
    private var coverImgUrl : String? =""
    private lateinit var categoryList: ArrayList<String>

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility = VISIBLE
        }

    }

    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
           val  imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()

        }

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddProductBinding.inflate(layoutInflater)

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            val intent = Intent ("android.intent.action.GET_CONTENT")
            intent.type= "image/*"
            launchGalleryActivity.launch(intent)

        }
        binding.productImgBtn.setOnClickListener {
            val intent = Intent ("android.intent.action.GET_CONTENT")
            intent.type= "image/*"
            launchProductActivity.launch(intent)

        }

        setProductCategory()

        adapter = AddProductImageAdapter(list)
        binding.priductImgRecyclerView.adapter = adapter

        binding.submitProductBtn.setOnClickListener {
            validateData()
        }


        return binding.root
    }

    private fun validateData() {
        if(binding.ProductNameEdt.text.toString().isEmpty()){
            binding.ProductNameEdt.requestFocus()
            binding.ProductNameEdt.error="Empty"
        }else if(binding.ProductSPEdt.text.toString().isEmpty()){
            binding.ProductSPEdt.requestFocus()
            binding.ProductSPEdt.error="Empty"
        }else if(categoryList[binding.productCategoryDropdown.selectedItemPosition]=="Select Category"){

            Toast.makeText(requireContext(), "Please Select Category", Toast.LENGTH_SHORT).show()

        }else if(coverImage == null){
            Toast.makeText(requireContext(), "Please Select Cover Image", Toast.LENGTH_SHORT).show()
        } else if(list.size  <1){
            Toast.makeText(requireContext(), "Please Select product Image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }

    }

    private fun uploadImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    coverImgUrl = image.toString()

                    uploadProdectImage()
                }
            }.addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "SomeThing Want Wrong With Storage", Toast.LENGTH_SHORT).show()
            }
    }

    private var i=0
    private fun uploadProdectImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    listImages.add(image!!.toString())
                    if(list.size == listImages.size)
                    {
                        storeData()
                    }else{
                        i +=1
                        uploadImage()
                    }

                }
            }.addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "SomeThing Want Wrong With Storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id

        val data = AddProductModel(
            binding.ProductNameEdt.text.toString(),
            binding.ProductDescriptionEdt.text.toString(),
            coverImgUrl.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.ProductMRPEdt.text.toString(),
            binding.ProductSPEdt.text.toString(),
            listImages
        )

        db.document(key).set(data).addOnSuccessListener{
            dialog.dismiss()
            Toast.makeText(requireContext(), "Product Added", Toast.LENGTH_SHORT).show()
            binding.ProductNameEdt.text = null
        }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()

            }
    }

    private fun setProductCategory(){
        categoryList = ArrayList()
        Firebase.firestore.collection("categories").get().addOnSuccessListener {
           categoryList.clear()
           for (doc in it.documents)
           {
               val data = doc.toObject(CategoryModel::class.java)
               categoryList.add(data!!.cate!!)

           }
            categoryList.add(0,"Select Category")

            val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item_layout,categoryList)
            binding.productCategoryDropdown.adapter = arrayAdapter

        }
    }


}