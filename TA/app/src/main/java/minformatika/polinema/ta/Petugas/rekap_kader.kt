package minformatika.polinema.ta.Petugas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.rekap_kad.*
import minformatika.polinema.ta.R
import org.json.JSONArray

class rekap_kader: AppCompatActivity() {
    var rumid = ""
    var petid = ""
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var RekapAdapter : AdapterRek
    var daftarRekap = mutableListOf<HashMap<String, String>>()
    val urlRekap = "http://192.168.43.37/myapi/rekapkad.php"
    val urlRumah = "http://192.168.43.37/myapi/latlonrumah.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rekap_kad)
        txRumah.text = intent.getStringExtra("nama").toString()
        rumid = intent.getStringExtra("rumid").toString()
        petid = intent.getStringExtra("petid").toString()
        initRecyclerView()
        val request = object : StringRequest(
            Request.Method.POST, urlRumah,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("LATITUDE", jsonObject.getString("LATITUDE"))
                    rmh.put("LONGITUDE", jsonObject.getString("LONGITUDE"))

                    lat.setText(rmh.getValue("LATITUDE").toString())
                    lon.setText(rmh.getValue("LONGITUDE").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("rumid",rumid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    private fun initRecyclerView(){
        RekapAdapter = AdapterRek(daftarRekap)

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        listrekap.layoutManager = layoutManager
        listrekap.adapter = RekapAdapter

        listrekap.addItemDecoration(DividerItemDecoration(listrekap.context,layoutManager.orientation))

        RekapAdapter.setOnItemClickListener(object: AdapterRek.OnItemClickListener {
            override fun setOnLongClickListener(v: View) {
            }
        })
    }
    override fun onStart() {
        super.onStart()
        ShowDataRekap(rumid,petid)
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
                    rkp.put("TGL_KUNJ", jsonObject.getString("TGL_KUNJ"))
                    rkp.put("STATUS", jsonObject.getString("STATUS"))
                    rkp.put("LATITUDE", jsonObject.getString("LATITUDE"))
                    rkp.put("LONGITUDE", jsonObject.getString("LONGITUDE"))
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
}