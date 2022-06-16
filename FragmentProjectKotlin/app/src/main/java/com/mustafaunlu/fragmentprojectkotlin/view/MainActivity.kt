package com.mustafaunlu.fragmentprojectkotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.mustafaunlu.fragmentprojectkotlin.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.fragment_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.add_art){
            //go to upload Fragment
            val action=RecyclerFragmentDirections.actionRecyclerFragmentToUploadFragment(0,"new")
            Navigation.findNavController(this, R.id.fragment).navigate(action)




        }


        return super.onOptionsItemSelected(item)

    }


}