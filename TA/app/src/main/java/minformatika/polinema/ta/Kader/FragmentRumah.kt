package minformatika.polinema.ta.Kader

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.data_frag_rumah.view.*
import kotlinx.android.synthetic.main.data_frag_rumah.view.listrumah
import kotlinx.android.synthetic.main.mod_opsi_kader.view.*
import kotlinx.android.synthetic.main.mod_tambah_rumah.view.*
import kotlinx.android.synthetic.main.mod_tambah_rumah.view.txNama
import kotlinx.android.synthetic.main.mod_tambah_rumah.view.txNo
import kotlinx.android.synthetic.main.row_rum.view.*
import org.json.JSONArray
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.konf_hapus.view.*
import kotlinx.android.synthetic.main.konf_tambah.view.*
import kotlinx.android.synthetic.main.konf_ubah.view.*
import kotlinx.android.synthetic.main.mod_ubah_rumah.view.*
import minformatika.polinema.ta.*
import org.json.JSONObject

class FragmentRumah : Fragment(), View.OnClickListener {

    lateinit var thisParent : Dashboard

    lateinit var RumahAdapter : AdapterRumah
    var daftarRumah = mutableListOf<HashMap<String, String>>()
    val urlRumah = "http://192.168.43.37/myapi/rumahh.php"
    val urlupdateRumah = "http://192.168.43.37/myapi/updaterumah.php"
    val urldetRumah = "http://192.168.43.37/myapi/detrumah.php"
    val urlhapusRumah = "http://192.168.43.37/myapi/hapusrumah.php"
    val urltambahRumah = "http://192.168.43.37/myapi/tambahrumah.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var dialog: AlertDialog.Builder
    lateinit var v: View

    var pet_id = ""
    var id_wil = ""
    var LA = ""
    var LO = ""

    lateinit var lokasirekues: LocationRequest
    lateinit var lokasiklien: FusedLocationProviderClient

    companion object{
        var instance:FragmentRumah?=null

        fun getmaininstance():FragmentRumah{
            return instance!!
        }
    }

    fun updatetextview(lat:String,lon:String)
    {
        thisParent.runOnUiThread{
            LO = lon
            LA = lat
        }
    }

    private fun updatelokasi() {
        bangunrekueslokasi()
        if (ActivityCompat.checkSelfPermission(thisParent,Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED)
            return
        lokasiklien = LocationServices.getFusedLocationProviderClient(thisParent)
        lokasiklien.requestLocationUpdates(lokasirekues,getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent =Intent(thisParent,lokasiservis::class.java)
        intent.setAction(lokasiservis.AKSI_UPDATE_LOKASI)
        return PendingIntent.getBroadcast(thisParent,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun bangunrekueslokasi() {
        lokasirekues = LocationRequest()
        lokasirekues.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        lokasirekues.interval=5000
        lokasirekues.fastestInterval=3000
        lokasirekues.smallestDisplacement=10f

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard
        instance = this

        Dexter.withActivity(thisParent)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    updatelokasi()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Toast.makeText(thisParent,"Beri Izin Akses Lokasi!",Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(thisParent,"Beri Izin Akses Lokasi!",Toast.LENGTH_SHORT).show()
                }
            }).check()

        v = inflater.inflate(R.layout.data_frag_rumah, container, false)
        dialog = AlertDialog.Builder(thisParent)

        v.button.setOnClickListener(this)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(thisParent, permissions,0)

        initRecyclerView()

        pet_id = SharedPref.getInstance(thisParent).user.id.toString()

        addRightCancelDrawable(v.findViewById(R.id.txCariRumah))

        v.txCariRumah.onRightDrawableClicked {
            it.text.clear()
            ShowDataRumah(id_wil,pet_id,"")
        }
        v.carirum.setOnClickListener {
            ShowDataRumah(id_wil,pet_id,v.txCariRumah.text.toString())
        }
        return v
    }

    private fun addRightCancelDrawable(editText: EditText) {
        val cancel = ContextCompat.getDrawable(thisParent, R.drawable.close)
        cancel?.setBounds(0,0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        editText.setCompoundDrawables(null, null, cancel, null)
    }
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    private fun initRecyclerView(){
        RumahAdapter = AdapterRumah(daftarRumah)

        layoutManager = LinearLayoutManager(thisParent, RecyclerView.VERTICAL,false)

        v.listrumah.layoutManager = layoutManager
        v.listrumah.adapter = RumahAdapter

        v.listrumah.addItemDecoration(DividerItemDecoration(v.listrumah.context,layoutManager.orientation))

        RumahAdapter.setOnItemClickListener(object: AdapterRumah.OnItemClickListener{
            override fun setOnLongClickListener(v: View) {
                val dView = LayoutInflater.from(thisParent).inflate(R.layout.mod_opsi_kader,null)
                val builder = AlertDialog.Builder(thisParent).setView(dView)
                val aDialog = builder.show()
                dView.tId.setText(v.txId.text.toString())
                dView.tNama.setText(v.txNAMA.text.toString())

                dView.rekap.setOnClickListener {
                    val intent = Intent(thisParent, minformatika.polinema.ta.Kader.rekap::class.java)
                    intent.putExtra("id",dView.tId.text.toString())
                    intent.putExtra("nama",dView.tNama.text.toString())
                    intent.putExtra("petid",pet_id)
                    startActivity(intent)
                }

                dView.ubah.setOnClickListener {
                    val uView = LayoutInflater.from(thisParent).inflate(R.layout.mod_ubah_rumah,null)
                    val uilder = AlertDialog.Builder(thisParent).setView(uView)
                    val Dialog = uilder.show()

                    val request = object : StringRequest(Request.Method.POST, urldetRumah,
                        Response.Listener { response ->
                            val jsonArray = JSONArray(response)
                            for (x in 0..(jsonArray.length()- 1)) {
                                val jsonObject = jsonArray.getJSONObject(x)
                                var rmh = HashMap<String, String>()
                                rmh.put("PEMILIK", jsonObject.getString("PEMILIK"))
                                rmh.put("ALAMAT", jsonObject.getString("ALAMAT"))
                                rmh.put("no_TELP", jsonObject.getString("no_TELP"))
                                rmh.put("LATITUDE", jsonObject.getString("LATITUDE"))
                                rmh.put("LONGITUDE", jsonObject.getString("LONGITUDE"))

                                uView.xNama.setText(rmh.getValue("PEMILIK").toString())
                                uView.xALAMAT.setText(rmh.getValue("ALAMAT").toString())
                                uView.xNo.setText(rmh.getValue("no_TELP").toString())
                                uView.vLatitude.setText(rmh.getValue("LATITUDE").toString())
                                uView.vLongitude.setText(rmh.getValue("LONGITUDE").toString())
                            }
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String> {
                            val hm = HashMap<String,String>()
                            hm.put("RUM_ID",dView.tId.text.toString())
                            return hm
                        }
                    }
                    val queue = Volley.newRequestQueue(thisParent)
                    queue.add(request)

                    uView.xPosisi.setOnClickListener {
                        startActivity(Intent(thisParent,maps::class.java))
                                val handler = Handler()
                                handler.postDelayed({
                                    uView.vLatitude.setText(LA).toString()
                                    uView.vLongitude.setText(LO).toString()
                                }, 2000)
                    }

                    uView.tnTambah.setOnClickListener {
                        val ukon = LayoutInflater.from(thisParent).inflate(R.layout.konf_ubah,null)
                        val buil = AlertDialog.Builder(thisParent).setView(ukon)
                        val diall = buil.show()

                        ukon.tdata.setText("Rumah ?")

                        ukon.uya.setOnClickListener {
                            updaterum("update",dView.tId.text.toString(),uView.xNama.text.toString(),
                                uView.xALAMAT.text.toString(),uView.xNo.text.toString(),uView.vLatitude.text.toString(),uView.vLongitude.text.toString())
                            Dialog.dismiss()
                            aDialog.dismiss()
                            val han = Handler()
                            han.postDelayed({
                                ShowDataRumah(id_wil,pet_id,"")
                            },1500)
                            diall.dismiss()
                        }
                        ukon.utidak.setOnClickListener {
                            diall.dismiss()
                        }
                    }

                    uView.tnX1.setOnClickListener {
                        Dialog.dismiss()
                    }

                    uView.utton4.setOnClickListener {
                        Dialog.dismiss()
                    }
                }


                dView.hapus.setOnClickListener {
                    val kon = LayoutInflater.from(thisParent).inflate(R.layout.konf_hapus,null)
                    val buil = AlertDialog.Builder(thisParent).setView(kon)
                    val dia = buil.show()

                    kon.tvdata.setText("Rumah ?")

                    kon.ya.setOnClickListener {
                        hapusrum("delete",dView.tId.text.toString())
                        val han = Handler()
                        han.postDelayed({
                            ShowDataRumah(id_wil,pet_id,"")
                        },1000)
                        aDialog.dismiss()
                        dia.dismiss()
                    }
                    kon.tidak.setOnClickListener {
                        dia.dismiss()
                    }

                }
                dView.txX.setOnClickListener {
                    aDialog.dismiss()
                }
            }

        })
    }

    fun ShowWil(petid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlWil,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("WIL_ID", jsonObject.getString("WIL_ID"))

                    v.tx_wil.setText(wil.getValue("WIL_ID").toString())

                    id_wil= v.tx_wil.text.toString()
                    ShowDataRumah(id_wil,pet_id,"")
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("petid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    override fun onStart() {
        super.onStart()
        ShowWil(pet_id)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.button->{
                val mDialogView = LayoutInflater.from(thisParent).inflate(R.layout.mod_tambah_rumah,null);

                val mBuilder = AlertDialog.Builder(thisParent)
                    .setView(mDialogView)

                val mAlertDialog = mBuilder.show()

                mDialogView.btnTambah.setOnClickListener {
                    val tkon = LayoutInflater.from(thisParent).inflate(R.layout.konf_tambah,null)
                    val built = AlertDialog.Builder(thisParent).setView(tkon)
                    val dialt = built.show()

                    tkon.ttdata.setText("Rumah ?")

                    tkon.tya.setOnClickListener {
                        tambahrumah("insert",mDialogView.txNama.text.toString(),mDialogView.txNokk.text.toString(),id_wil,pet_id,mDialogView.txALAMAT.text.toString(),mDialogView.txNo.text.toString(),
                            mDialogView.tvLatitude.text.toString(), mDialogView.tvLongitude.text.toString())
                        mAlertDialog.dismiss()
                        val han = Handler()
                        han.postDelayed({
                            ShowDataRumah(id_wil,pet_id,"")
                        },1000)
                        dialt.dismiss()
                    }

                    tkon.ttidak.setOnClickListener {
                        dialt.dismiss()
                    }

                }

                mDialogView.txPosisi.setOnClickListener {
                    startActivity(Intent(thisParent,maps::class.java))

                            val handler = Handler()
                            handler.postDelayed({
                                mDialogView.tvLatitude.setText(LA).toString()
                                mDialogView.tvLongitude.setText(LO).toString()
                            }, 1000)
                }
                mDialogView.btnX1.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                mDialogView.button4.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }

        }
    }
    fun ShowDataRumah(wil_id : String,petid:String,nama:String) {
        val request = object : StringRequest(Request.Method.POST, urlRumah,
            Response.Listener { response ->
                daftarRumah.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("RUM_ID", jsonObject.getString("RUM_ID"))
                    rmh.put("PEMILIK", jsonObject.getString("PEMILIK"))
                    rmh.put("ALAMAT", jsonObject.getString("ALAMAT"))
                    rmh.put("no_TELP", jsonObject.getString("no_TELP"))
                    daftarRumah.add(rmh)
                }
                RumahAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wil_id)
                hm.put("petid",petid)
                hm.put("nama",nama)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun tambahrumah(mode:String, Pemilik: String,nokk:String,wilid: String,petid: String, Alamat: String, No: String, Lat: String, Lon: String){
        val Request = object : StringRequest(Method.POST,urltambahRumah,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Tambah rumah berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "NO KK Sudah Terdaftar",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "insert" -> {
                        hm.put("mode", mode)
                        hm.put("PEMILIK", Pemilik)
                        hm.put("nokk",nokk)
                        hm.put("WIL_ID", wilid)
                        hm.put("petid", petid)
                        hm.put("ALAMAT", Alamat)
                        hm.put("no_TELP", No)
                        hm.put("la", Lat)
                        hm.put("lo", Lon)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
    fun hapusrum(mode:String, rumid:String){
        val Request = object : StringRequest(Method.POST,urlhapusRumah,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Hapus rumah berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Hapus rumah gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("rumid", rumid)
                    }
            }
            return hm
        }
    }
    val queue = Volley.newRequestQueue(thisParent)
    queue.add(Request)
    }
    fun updaterum(mode:String, rumid:String, pemilik:String, alamat:String,no:String,la:String,lo:String){
        val Request = object : StringRequest(Method.POST,urlupdateRumah,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah data rumah berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah data rumah gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", "update")
                        hm.put("rumid", rumid)
                        hm.put("pemilik", pemilik)
                        hm.put("alamat", alamat)
                        hm.put("no", no)
                        hm.put("la", la)
                        hm.put("lo", lo)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
}