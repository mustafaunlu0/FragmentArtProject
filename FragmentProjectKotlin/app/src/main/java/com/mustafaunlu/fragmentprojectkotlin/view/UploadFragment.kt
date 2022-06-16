package com.mustafaunlu.fragmentprojectkotlin.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.mustafaunlu.fragmentprojectkotlin.R
import com.mustafaunlu.fragmentprojectkotlin.model.Art
import com.mustafaunlu.fragmentprojectkotlin.roomdb.ArtDao
import com.mustafaunlu.fragmentprojectkotlin.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.jar.Manifest

class UploadFragment : Fragment() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var db : ArtDatabase
    private lateinit var artDao : ArtDao
    val compositeDisposable = CompositeDisposable()
    var selectedBitmap: Bitmap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db= Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"Arts").build()
        artDao=db.artDao()

    }

    fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode==RESULT_OK){
                val intentFromResult=result.data
                if(intentFromResult !=null){
                    selectedPicture=intentFromResult.data
                    try{
                        if(Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(requireActivity().contentResolver,selectedPicture!!)
                            selectedBitmap=ImageDecoder.decodeBitmap(source)
                            selectImageView.setImageBitmap(selectedBitmap)

                        }
                        else{
                            selectedBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,selectedPicture)
                            selectImageView.setImageBitmap(selectedBitmap)
                        }
                    }
                    catch (e :Exception){
                        e.printStackTrace()
                    }
                }
            }

        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
            else{
                Toast.makeText(context,"Permission needed!",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImageView.setOnClickListener {
            if(ContextCompat.checkSelfPermission(it.context,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //permission denied
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(it.rootView,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
                        //permission launcher
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()

                }else{
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    //permission Launcher

                }

            }
            else{
                //permission granted
                val intent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            //activityResultLauncher

            }
        }

        uploadButton.setOnClickListener {
            if(selectedBitmap !=null){
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                val byteArray = outputStream.toByteArray()

                val art= Art(artNameEditText.text.toString(),artistEditText.text.toString(),yearEditText.text.toString(),byteArray)
                compositeDisposable.add(
                    artDao.insert(art)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }


        }

        arguments?.let {
            val info=UploadFragmentArgs.fromBundle(it).info
            if(info.equals("new")){
                //NEW
                artNameEditText.setText("")
                artistEditText.setText("")
                yearEditText.setText("")
                uploadButton.visibility=View.VISIBLE
                selectImageView.setImageResource(R.drawable.select)


            }
            else{
                uploadButton.visibility=View.INVISIBLE
                val selectedId=UploadFragmentArgs.fromBundle(it).id
                compositeDisposable.add(
                    artDao.getArtById(selectedId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseWithOldArt)
                )
            }

            }




    }
    private fun handleResponseWithOldArt(art :Art){

         artNameEditText.setText(art.artName)
         artistEditText.setText(art.artistName)
         yearEditText.setText(art.year)
         art.imageUri.let {
             val bitmap=BitmapFactory.decodeByteArray(it,0,it.size)
             selectImageView.setImageBitmap(bitmap)
         }
    }
    private fun handleResponse(){
        val action=UploadFragmentDirections.actionUploadFragmentToRecyclerFragment()
        Navigation.findNavController(view!!).navigate(action)
    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()

    }



}