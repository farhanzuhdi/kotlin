package minformatika.polinema.ta.Kader

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.data_frag_graf.*
import kotlinx.android.synthetic.main.data_frag_graf.view.*
import kotlinx.android.synthetic.main.data_frag_graf.view.barChart
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray

class FragmentGrafik : Fragment() {

    lateinit var thisParent : Dashboard

    lateinit var v: View
    var pet_id = ""
    val urlRum = "http://192.168.43.37/myapi/jumrum.php"
    val urlkun = "http://192.168.43.37/myapi/jumkun.php"
    val urlabj = "http://192.168.43.37/myapi/jumabj.php"
    val grafkad = "http://192.168.43.37/myapi/grafkad.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    var urltahun = "http://192.168.43.37/myapi/thn.php"
    lateinit var tahunAdapter: ArrayAdapter<String>
    var daftartahun= mutableListOf<String>()
    var _idwil = ""
    var pilihtahun = ""
    var nilai = mutableListOf<Int>()
    var bulan = mutableListOf<String>()

    val thnpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            regKec.setSelection(0)
            pilihtahun = daftartahun.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihtahun = daftartahun.get(position)

            grafkadabj(_idwil,pet_id, pilihtahun)
            grafkadbul(_idwil,pet_id, pilihtahun)

        }
    }
    override fun onStart() {
        super.onStart()
        ShowWil(pet_id)
        jumkun(pet_id)
        val hand = Handler()
        hand.postDelayed({
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_LONG).show()
            }else {
                grafkadabj(_idwil,pet_id, pilihtahun)
                grafkadbul(_idwil,pet_id, pilihtahun)
            }
        },1500)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard
        v = inflater.inflate(R.layout.data_frag_graf,container,false)


        pet_id = SharedPref.getInstance(thisParent).user.id.toString()
        tahunAdapter = ArrayAdapter(
            thisParent, android.R.layout.simple_dropdown_item_1line,
            daftartahun
        )
        v.thn.adapter = tahunAdapter
        v.thn.onItemSelectedListener = thnpilih

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
        v.barChart.data = data // set the data and list of lables into chart
        // set the description

        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)

        v.barChart.animateY(1500)

        v.imageButton.setOnClickListener{
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_LONG).show()
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
                v.barChart.data = data

                v.barChart.animateY(1500)
            }
        }
        return v
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

                    jumrum(wil.getValue("WIL_ID").toString(),pet_id)
                    jumabj(wil.getValue("WIL_ID").toString(),pet_id)
                    getthn(wil.getValue("WIL_ID"),pet_id)
                    _idwil = wil.getValue("WIL_ID").toString()

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
    fun getthn(wilid : String, petid: String) {
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
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("petid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun jumrum(wilid : String,petid: String) {
        val request = object : StringRequest(
            Request.Method.POST, urlRum,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("jum"))

                    v.txjumrum.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("petid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun jumkun(petid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlkun,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("jum"))

                    v.txjumkun.setText(wil.getValue("jum").toString())
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
    fun jumabj(wilid : String,petid: String) {
        val request = object : StringRequest(
            Request.Method.POST, urlabj,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("jum"))

                    v.txjumabj.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("petid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun grafkadabj(wilid : String,petid: String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, grafkad,
            Response.Listener { response ->
                nilai.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    nilai.add(jsonObject.getInt("abj"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("petid",petid)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun grafkadbul(wilid : String,petid: String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, grafkad,
            Response.Listener { response ->
                bulan.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    bulan.add(jsonObject.getString("bulan"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",wilid)
                hm.put("petid",petid)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}