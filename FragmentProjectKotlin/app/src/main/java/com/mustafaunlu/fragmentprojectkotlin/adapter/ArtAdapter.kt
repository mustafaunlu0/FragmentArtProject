package com.mustafaunlu.fragmentprojectkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.mustafaunlu.fragmentprojectkotlin.databinding.RecyclerRowBinding
import com.mustafaunlu.fragmentprojectkotlin.model.Art
import com.mustafaunlu.fragmentprojectkotlin.view.RecyclerFragmentDirections

class ArtAdapter(val artList: ArrayList<Art>) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recyclerViewTextView.text=artList[position].artName
        holder.itemView.setOnClickListener{
            val action=RecyclerFragmentDirections.actionRecyclerFragmentToUploadFragment(artList[position].id,"old")
            Navigation.findNavController(it!!).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return artList.size
    }


}