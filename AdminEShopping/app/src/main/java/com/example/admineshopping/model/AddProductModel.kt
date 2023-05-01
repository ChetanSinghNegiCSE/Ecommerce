package com.example.admineshopping.model

import android.accounts.AuthenticatorDescription

data class AddProductModel(
    val productName: String?="",
    val productDescription: String?= "",
    val productCoverImg: String?= "",
    val productCategory: String?="",
    val productID: String?="",
    val productMrp: String?="",
    val productSp: String = "",
    val productImages: ArrayList<String>

    )
