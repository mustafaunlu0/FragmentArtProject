package com.mustafaunlu.fragmentprojectkotlin.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Art(
    @ColumnInfo(name = "artName")
    var artName:String,

    @ColumnInfo(name = "artistName")
    var artistName:String,

    @ColumnInfo( name="year")
    var year: String,

    @ColumnInfo (name="imageUri")
    var imageUri: ByteArray
) {
    @PrimaryKey(autoGenerate = true)
    var id=0
}