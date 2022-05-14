package minformatika.polinema.ta.Petugas

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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.data_frag_graf.view.*
import kotlinx.android.synthetic.main.data_frag_graf.view.barChart
import kotlinx.android.synthetic.main.data_frag_graf.view.txjumabj
import kotlinx.android.synthetic.main.data_frag_graf.view.txjumkun
import kotlinx.android.synthetic.main.data_frag_graf_pet.view.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray

class FragmentGrafik : Fragment() {

    lateinit var thisParent : Dashboard

    lateinit var v: View
    var _id = ""
    var urltahun = "http://192.168.43.37/myapi/thnpet.php"
    lateinit var tahunAdapter: ArrayAdapter<String>
    var daftartahun= mutableListOf<String>()
    var pilihtahun = ""
    val urlkec = "http://192.168.43.37/myapi/namakec.php"
    val urlabj = "http://192.168.43.37/myapi/abj_pet.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    val urljumkad = "http://192.168.43.37/myapi/jum_kad.php"
    val urljumrum = "http://192.168.43.37/myapi/rum_pet.php"
    val urlgrafpet = "http://192.168.43.37/myapi/grafpet.php"
    var kec = ""
    var nilai = mutableListOf<Int>()
    var bulan = mutableListOf<String>()
    val thnpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            regKec.setSelection(0)
            pilihtahun = daftartahun.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihtahun = daftartahun.get(position)
            grafpetabj(kec, pilihtahun)
            grafpetbul(kec, pilihtahun)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard

        v = inflater.inflate(R.layout.data_frag_graf_pet,container,false)
        _id = SharedPref.getInstance(thisParent).user.id.toString()
        tahunAdapter = ArrayAdapter(
            thisParent, android.R.layout.simple_dropdown_item_1line,
            daftartahun
        )

        v.thnpet.adapter = tahunAdapter
        v.thnpet.onItemSelectedListener = thnpilih
        val entries = ArrayList<BarEntry>()

        for (x in 0..nilai.count()-1) {
        entries.add(BarEntry(nilai[x].toFloat(), x))
        }
        val barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

        val labels = ArrayList<String>()
        for (x in 0..bulan.count()-1) {
            labels.add(bulan[x])
        }
        val data = BarData(labels, barDataSet)
        v.barChart.data = data // set the data and list of lables into chart
        // set the description

        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)

        v.barChart.animateY(1500)
        v.imageBut.setOnClickListener {
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_LONG).show()
            }else {
                val entries = ArrayList<BarEntry>()
                for (x in 0..nilai.count()-1) {
                    entries.add(BarEntry(nilai[x].toFloat(), x))
                }

                val barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

                val labels = ArrayList<String>()
                for (x in 0..bulan.count()-1) {
                    labels.add(bulan[x])
                }

                val data = BarData(labels, barDataSet)
                v.barChart.data = data // set the data and list of lables into chart
                // set the description

                //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)

                v.barChart.animateY(1500)
            }
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        ShowWil(_id)
        val hand = Handler()
        hand.postDelayed({
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_LONG).show()
            }else {
                grafpetabj(kec, pilihtahun)
                grafpetbul(kec, pilihtahun)
            }
        },2000)
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
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun grafpetabj(kec : String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, urlgrafpet,
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
                hm.put("kec",kec)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun grafpetbul(kec : String,tahun: String) {
        val request = object : StringRequest(
            Request.Method.POST, urlgrafpet,
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
                hm.put("kec",kec)
                hm.put("tahun",tahun)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
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

                    Showkec(wil.getValue("WIL_ID"))
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

    fun Showkec(petid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlkec,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("KEC", jsonObject.getString("KEC"))

                    jumabj(wil.getValue("KEC"))
                    jumkad(wil.getValue("KEC"))
                    jumrum(wil.getValue("KEC"))
                    getthn(wil.getValue("KEC"))
                    kec = wil.getValue("KEC").toString()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("wilid",petid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
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

                    v.txjumabj.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun jumkad(wilid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urljumkad,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("pet"))

                    v.txjumkun.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun jumrum(wilid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urljumrum,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("jum", jsonObject.getString("rum"))

                    v.txjumrumpet.setText(wil.getValue("jum").toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",wilid)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}