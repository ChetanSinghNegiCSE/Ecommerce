package com.example.eshoping.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.checkerframework.checker.nullness.qual.NonNull


@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    val productId : String,
    @ColumnInfo(name ="productName")
    val productName : String? = "",
    @ColumnInfo(name ="productImage")
    val productImage : String? = "",
    @ColumnInfo(name ="productSp")
    val productSp : String? = "",



)
