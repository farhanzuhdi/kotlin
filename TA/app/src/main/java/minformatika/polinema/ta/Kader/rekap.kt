package minformatika.polinema.ta.Kader

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.konf_hapus.view.*
import kotlinx.android.synthetic.main.konf_tambah.view.*
import kotlinx.android.synthetic.main.konf_ubah.view.*
import kotlinx.android.synthetic.main.mod_opsi_rekap.view.*
import kotlinx.android.synthetic.main.mod_tambah_rekap.view.*
import kotlinx.android.synthetic.main.mod_tambah_rekap.view.spinner3
import kotlinx.android.synthetic.main.mod_tambah_rekap.view.tagal
import kotlinx.android.synthetic.main.mod_tambah_rekap.view.tbrek
import kotlinx.android.synthetic.main.mod_ubah_rekap.view.*
import kotlinx.android.synthetic.main.rekap.*
import kotlinx.android.synthetic.main.row_rek.view.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class rekap : AppCompatActivity() , View.OnClickListener{

    lateinit var RekapAdapter : AdapterRekap
    var daftarRekap = mutableListOf<HashMap<String, String>>()
    val urlRekap = "http://192.168.43.37/myapi/rekap.php"
    val urltambahRekap = "http://192.168.43.37/myapi/tambahrekap.php"
    val urlubahRekap = "http://192.168.43.37/myapi/ubahrekap.php"
    var urlAbj = "http://192.168.43.37/myapi/abj.php"
    val urlhapusrekap = "http://192.168.43.37/myapi/hapusrekap.php"
    var _id = ""
    var LA = ""
    var LO = ""
    var pet_id = ""
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rekap)

        txRumah.setText(intent.getStringExtra("nama"))
        _id = intent.getStringExtra("id").toString()
        initRecyclerView()
            val stat = arrayOf("TIDAK ADA","ADA")

        pet_id = intent.getStringExtra("petid").toString()
        var rum = intent.getStringExtra("id").toString()

        var status = ""
        bttambahrek.setOnClickListener {
            val DialV = LayoutInflater.from(this).inflate(R.layout.mod_tambah_rekap,null)
            val Build = AlertDialog.Builder(this)
                .setView(DialV)
            val mAlertD = Build.show()
            val mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocation.lastLocation.addOnSuccessListener(this, object :
                OnSuccessListener<Location> {
                override fun onSuccess(location: Location?) {
                    Log.d("My Current location", "Lat : ${location?.latitude} Long : ${location?.longitude}")

                    LA = ("${location?.latitude}")
                    LO = ("${location?.longitude}")

                    DialV.lang.setText(LA).toString()
                    DialV.lon.setText(LO).toString()
                }
            })


            DialV.spinner3.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stat)

            DialV.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){
                    DialV.spinner3.setSelection(0)
                    status = stat.get(0)
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                    status = stat.get(position)
                }
            }

            DialV.tagal.setText(""+ SimpleDateFormat("yyy", Locale.getDefault())
                .format(Date())+"-"+ SimpleDateFormat("MM", Locale.getDefault())
                .format(Date())+"-"+ SimpleDateFormat("dd", Locale.getDefault())
                .format(Date()))
            DialV.tbrek.setOnClickListener{
                val tkon = LayoutInflater.from(this).inflate(R.layout.konf_tambah,null)
                val built = android.app.AlertDialog.Builder(this).setView(tkon)
                val dialt = built.show()

                tkon.ttdata.setText("Rekap ?")

                tkon.tya.setOnClickListener {
                    tambahrekap("insert",rum,DialV.tagal.text.toString(),status,pet_id,DialV.lang.text.toString(),DialV.lon.text.toString())
                    mAlertD.dismiss()
                    val han = Handler()
                    han.postDelayed({
                        ShowDataRekap(_id,pet_id)
                        ShowAbj(_id)
                    },1000)
                    dialt.dismiss()
                }

                tkon.ttidak.setOnClickListener {
                    dialt.dismiss()
                }
            }

            DialV.xcx.setOnClickListener {
                mAlertD.dismiss()
            }
        }
    }

    private fun initRecyclerView() {
        RekapAdapter = AdapterRekap(daftarRekap)

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        listrekap.layoutManager = layoutManager
        listrekap.adapter = RekapAdapter

        listrekap.addItemDecoration(
            DividerItemDecoration(
                listrekap.context,
                layoutManager.orientation
            )
        )
        RekapAdapter.setOnItemClickListener(object: AdapterRekap.OnItemClickListener {
            override fun setOnLongClickListener(v: View) {
                val mod = LayoutInflater.from(this@rekap).inflate(R.layout.mod_opsi_rekap,null)
                val buil = AlertDialog.Builder(this@rekap).setView(mod)
                val dial = buil.show()

                mod.Idrek.setText(v.txId.text.toString())
                mod.tglrek.setText(v.txTgl.text.toString())
                mod.statrek.setText(v.txStatus.text.toString())

                mod.txX.setOnClickListener {
                    dial.dismiss()
                }

                mod.hapus.setOnClickListener {
                    val kon = LayoutInflater.from(this@rekap).inflate(R.layout.konf_hapus,null)
                    val buil = android.app.AlertDialog.Builder(this@rekap).setView(kon)
                    val dia = buil.show()

                    kon.tvdata.setText("Rekap ?")

                    kon.ya.setOnClickListener {
                        hapusrek("delete",mod.Idrek.text.toString())
                        dia.dismiss()
                        dial.dismiss()
                        val han = Handler()
                        han.postDelayed({
                            ShowDataRekap(_id,pet_id)
                        },1000)
                    }
                }

                mod.ubah.setOnClickListener {
                    val u = LayoutInflater.from(this@rekap).inflate(R.layout.mod_ubah_rekap,null)
                    val b = AlertDialog.Builder(this@rekap).setView(u)
                    val d = b.show()

                    u.xxxx.setOnClickListener {
                        d.dismiss()
                    }

                    var ustat = ""

                    val stat = arrayOf("TIDAK ADA","ADA")
                    u.spinn.adapter = ArrayAdapter<String>(this@rekap,android.R.layout.simple_list_item_1,stat)
                    u.spinn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?){
                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                            ustat = stat.get(position)
                        }
                    }
                    if (mod.statrek.text.toString().equals("TIDAK ADA")){
                        u.spinn.setSelection(0)
                        ustat ="TIDAK ADA"
                    }else{
                        u.spinn.setSelection(1)
                        ustat ="ADA"
                    }
                    u.tgl.setText(mod.tglrek.text.toString())
                    u.ubid.setText(mod.Idrek.text.toString())
                    u.tbrekubah.setOnClickListener{
                        val ukon = LayoutInflater.from(this@rekap).inflate(R.layout.konf_ubah,null)
                        val buil = android.app.AlertDialog.Builder(this@rekap).setView(ukon)
                        val diall = buil.show()

                        ukon.tdata.setText("Rekap ?")

                        ukon.uya.setOnClickListener {
                            updaterekap("update",u.ubid.text.toString(),u.tgl.text.toString(),ustat)
                            d.dismiss()
                            dial.dismiss()
                            val han = Handler()
                            han.postDelayed({
                                ShowDataRekap(_id,pet_id)
                                ShowAbj(_id)
                            },500)
                            diall.dismiss()
                        }
                        ukon.utidak.setOnClickListener {
                            diall.dismiss()
                        }
                    }
                }
            }
        })
            }

    override fun onClick(p0: View?) {
    }

    override fun onStart() {
        super.onStart()
        ShowDataRekap(_id,pet_id)
        ShowAbj(_id)
    }

    fun ShowDataRekap(rum_id : String, petid:String) {
        val request = object : StringRequest(
            Request.Method.POST, urlRekap,
            Response.Listener { response ->
                daftarRekap.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rkp = HashMap<String, String>()
                    rkp.put("REK_ID", jsonObject.getString("REK_ID"))
                    rkp.put("TGL_KUNJ", jsonObject.getString("TGL_KUNJ"))
                    rkp.put("STATUS", jsonObject.getString("STATUS"))
                    daftarRekap.add(rkp)
                }
                RekapAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("rumid",rum_id)
                hm.put("petid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun ShowAbj(rum_id : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlAbj,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var abj = HashMap<String, String>()
                    abj.put("abj", jsonObject.getString("abj"))

                    tvabj.setText(abj.getValue("abj").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("rumid",rum_id)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun tambahrekap(mode:String, id: String,tgl: String,status: String, pet: String, Lat: String, Lon: String){
        val Request = object : StringRequest(Method.POST,urltambahRekap,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(this, "Tambah rekap berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, """Tambah rekap gagal
Rumah dan Hari ini sudah dilakukan rekap""",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "insert" -> {
                        hm.put("mode", mode)
                        hm.put("rumid", id)
                        hm.put("tgl", tgl)
                        hm.put("status", status)
                        hm.put("pet", pet)
                        hm.put("la", Lat)
                        hm.put("lo", Lon)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(Request)
    }

    fun updaterekap(mode:String, id: String,tgl: String,status: String){
        val Request = object : StringRequest(Method.POST,urlubahRekap,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(this, "Ubah rekap berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Ubah rekap gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", "update")
                        hm.put("rekid", id)
                        hm.put("tgl", tgl)
                        hm.put("status", status)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(Request)
    }

    fun hapusrek(mode:String, rekid:String){
        val Request = object : StringRequest(Method.POST,urlhapusrekap,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(this, "Hapus rekap berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Hapus rekap gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("rekid", rekid)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(Request)
    }
}