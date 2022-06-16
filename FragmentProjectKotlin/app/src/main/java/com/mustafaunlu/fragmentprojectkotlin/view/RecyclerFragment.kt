package com.mustafaunlu.fragmentprojectkotlin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mustafaunlu.fragmentprojectkotlin.R
import com.mustafaunlu.fragmentprojectkotlin.adapter.ArtAdapter
import com.mustafaunlu.fragmentprojectkotlin.databinding.RecyclerRowBinding
import com.mustafaunlu.fragmentprojectkotlin.model.Art
import com.mustafaunlu.fragmentprojectkotlin.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recycler.*


class RecyclerFragment : Fragment() {

    private val compositeDisposable=CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db=Room.databaseBuilder(context!!,ArtDatabase::class.java,"Arts").build()
        val artDao=db.artDao()

        compositeDisposable.add(
            artDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }
    private fun handleResponse(artList : List<Art>){
        recylcerView.layoutManager=LinearLayoutManager(context)
        val adapter=ArtAdapter(artList as ArrayList<Art>)
        recylcerView.adapter=adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}