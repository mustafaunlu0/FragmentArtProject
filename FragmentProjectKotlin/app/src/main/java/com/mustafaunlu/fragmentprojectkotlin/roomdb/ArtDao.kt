package com.mustafaunlu.fragmentprojectkotlin.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mustafaunlu.fragmentprojectkotlin.model.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {

    @Query("SELECT * FROM Art")
    fun getAll() : Flowable<List<Art>>

    @Query("SELECT * FROM Art where id = :id")
    fun getArtById(id : Int): Flowable<Art>

    @Insert
    fun insert(art : Art) : Completable

    @Delete
    fun delete(art : Art) : Completable
}