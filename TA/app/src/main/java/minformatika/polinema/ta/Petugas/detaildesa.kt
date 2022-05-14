package minformatika.polinema.ta.Petugas

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.data_frag_graf.view.*
import kotlinx.android.synthetic.main.detaildesa.*
import minformatika.polinema.ta.R
import org.json.JSONArray

class detaildesa : AppCompatActivity(){
    var _id = ""
    var pilihtahun = ""
    var urltahun = "http://192.168.43.37/myapi/thndes.php"
    val urlabj = "http://192.168.43.37/myapi/jumabjdes.php"
    val grafdes = "http://192.168.43.37/myapi/grafdes.php"
    lateinit var tahunAdapter: ArrayAdapter<String>
    var nilai = mutableListOf<Int>()
    var bulan = mutableListOf<String>()
    var daftartahun= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detaildesa)

        _id = intent.getStringExtra("id").toString()
        namdes.setText(intent.getStringExtra("desa").toString())

        tahunAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line,
            daftartahun
        )
        thn2.adapter = tahunAdapter
        thn2.onItemSelectedListener = thnpilih

        var entries = ArrayList<BarEntry>()
        for (x in 0..nilai.count()-1) {
            entries.add(BarEntry(nilai[x].toFloat(), x))
        }

        var barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

        var labels = ArrayList<String>()
        for (x in 0..bulan.count()-1) {
            labels.add(bulan[x])
        }
        var data = BarData(labels, barDataSet)
        barChart.data = data

        barChart.animateY(1500)

        imageButton2.setOnClickListener {
            if (pilihtahun==""){
                Toast.makeText(this,"Data Kosong!",Toast.LENGTH_LONG).show()
            }else {
                var entries = ArrayList<BarEntry>()
                for (x in 0..nilai.count()-1) {
                    entries.add(BarEntry(nilai[x].toFloat(), x))
                }
                var barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

                var labels = ArrayList<String>()
                for (x in 0..bulan.count()-1) {
                    labels.add(bulan[x])
                }
                var data = BarData(labels, barDataSet)
                barChart.data = data

                barChart.animateY(1500)
            }
        }
    }
    val thnpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            thn2.setSelection(0)
            pilihtahun = daftartahun.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihtahun = daftartahun.get(position)
            grafdesabj(_id, pilihtahun)
            grafdesbul(_id, pilihtahun)
        }
    }
    override fun onStart() {
        super.onStart()
        getthn(_id)
        jumabj(_id)
        val hand = Handler()
        hand.postDelayed({
        if (pilihtahun==""){
            Toast.makeText(this,"Data Kosong!",Toast.LENGTH_LONG).show()
        }else {
                grafdesabj(_id, pilihtahun)
                grafdesbul(_id, pilihtahun)
        }
        }, 2000)
    }
    fun getthn(wilid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urltahun,
            Response.Listener { response ->
                daftartahun.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftartahun.add(jsonObject.getString("tahun"))
                }
                tahunAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    fun jumabj(wilid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlabj,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("jum"))

                    tvabj2.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    fun grafdesabj(wilid : String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, grafdes,
            Response.Listener { response ->
                nilai.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    nilai.add(jsonObject.getInt("abj"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun grafdesbul(wilid : String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, grafdes,
            Response.Listener { response ->
                bulan.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    bulan.add(jsonObject.getString("bulan"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}