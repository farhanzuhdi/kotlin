package minformatika.polinema.ta

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.location.LocationResult
import minformatika.polinema.ta.Kader.FragmentRumah
import java.lang.StringBuilder

class lokasiservis: BroadcastReceiver(){
    companion object{
        val AKSI_UPDATE_LOKASI="minformatika.polinema.ta.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null){
            val aksi = intent!!.action
            if (aksi.equals(AKSI_UPDATE_LOKASI)){
                val result = LocationResult.extractResult(intent!!)
                if (result != null){
                    val lokasi = result.lastLocation
                    val lat = lokasi.latitude.toString()
                    val lon = lokasi.longitude.toString()
                    try {
                        FragmentRumah.getmaininstance().updatetextview(lat, lon)
                    }catch (e:Exception)
                    {
                    }
                }
            }
        }
    }
}