package minformatika.polinema.ta.Kader

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.maps.*
import kotlinx.android.synthetic.main.mod_tambah_rumah.*
import minformatika.polinema.ta.R
import mumayank.com.airlocationlibrary.AirLocation

class maps : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    override fun onClick(v: View?) {
        airLoc = AirLocation(this,true,true,
            object : AirLocation.Callbacks{
                override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    Toast.makeText(this@maps, "Gagal mendapatkan posisi saat ini",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(location: Location) {
                    val ll = LatLng(location.latitude,location.longitude)
                    gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,16.0f))
                    Toast.makeText(this@maps,"Kordinat Berhasil di ambil",Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0
        if(gMap!=null){
            airLoc = AirLocation(this,true,true,
                object : AirLocation.Callbacks{
                    override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                        Toast.makeText(this@maps, "Gagal mendapatkan posisi saat ini",
                            Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(location: Location) {
                        val ll = LatLng(location.latitude,location.longitude)
                        gMap!!.addMarker(MarkerOptions().position(ll).title("Posisi Saya"))
                        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,16.0f))
                    }
                })
        }
    }
    var airLoc : AirLocation? = null
    var gMap : GoogleMap? = null
    lateinit var mapFragment : SupportMapFragment


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btx.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLoc?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLoc?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
