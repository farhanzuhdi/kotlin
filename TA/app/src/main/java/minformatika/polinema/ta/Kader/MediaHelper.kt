package minformatika.polinema.ta.Kader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.util.*

class MediaHelper(context : Context){
    val context = context

    fun getRcGallery() : Int {
        return REQ_CODE_GALLERY
    }
    //mengubah gambar menjadi bentuk string
    fun bitmapToString(bmp : Bitmap) : String{
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG,60,outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray,Base64.DEFAULT) //menggunakan base65 tsb

    }

    //mengubah ukuran gambar
    fun getBitmapToString(uri : Uri?, imv : ImageView) : String{
        var bmp = MediaStore.Images.Media.getBitmap(
            this.context.contentResolver,uri)
        var dim = 720 //uk HD
        if(bmp.height > bmp.width){
            bmp = Bitmap.createScaledBitmap(bmp,
                (bmp.width*dim).div(bmp.height),dim,true)
        }else{
            bmp = Bitmap.createScaledBitmap(bmp,
                dim, (bmp.height*dim).div(bmp.width), true)
        }
        imv.setImageBitmap(bmp)
        return bitmapToString(bmp)
    }

    companion object {
        const val REQ_CODE_GALLERY = 100
    }
}