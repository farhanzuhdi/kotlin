package minformatika.polinema.ta.Petugas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.data_frag_desa.*
import kotlinx.android.synthetic.main.data_frag_desa.view.*
import kotlinx.android.synthetic.main.data_frag_desa.view.barChart
import kotlinx.android.synthetic.main.data_frag_desa.view.imageButton
import kotlinx.android.synthetic.main.data_frag_desa.view.thn
import kotlinx.android.synthetic.main.data_frag_graf.view.*
import kotlinx.android.synthetic.main.konf_logout.view.*
import kotlinx.android.synthetic.main.row_desa.view.*
import minformatika.polinema.ta.Kader.rekap
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray

class FragmentDesa : Fragment() {
    lateinit var v : View
    lateinit var thisParent : Dashboard
    lateinit var session: SharedPref
    val urldesa = "http://192.168.43.37/myapi/showdesa.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    val urlkec = "http://192.168.43.37/myapi/namakec.php"
    var urltahun = "http://192.168.43.37/myapi/thnpet.php"
    var grafpet = "http://192.168.43.37/myapi/grafpet.php"
    var grafpetdes = "http://192.168.43.37/myapi/grafpetdes.php"
    lateinit var tahunAdapter: ArrayAdapter<String>
    lateinit var bulanadapater: ArrayAdapter<String>
    var daftartahun= mutableListOf<String>()
    var daftarbulan = mutableListOf<String>()
    var pilihtahun = ""
    var pilihbulan = ""
    var nilai = mutableListOf<Int>()
    var namadesa = mutableListOf<String>()
    val thnpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            thn.setSelection(0)
            pilihtahun = daftartahun.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihtahun = daftartahun.get(position)
        }
    }
    val blnpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            bul.setSelection(0)
            pilihbulan = ""
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihbulan = daftarbulan.get(position)
        }
    }
    lateinit var desaAdapter : AdapterDesa
    var daftardesa= mutableListOf<HashMap<String, String>>()
    var _id = ""
    var kec = ""
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard

        v = inflater.inflate(R.layout.data_frag_desa, container, false)
        _id = SharedPref.getInstance(thisParent).user.id.toString()

        tahunAdapter = ArrayAdapter(
            thisParent, android.R.layout.simple_dropdown_item_1line,
            daftartahun
        )
        bulanadapater = ArrayAdapter(
            thisParent, android.R.layout.simple_dropdown_item_1line,
            daftarbulan
        )

        v.thn.adapter = tahunAdapter
        v.thn.onItemSelectedListener = thnpilih

        v.bul.adapter = bulanadapater
        v.bul.onItemSelectedListener = blnpilih

        initRecyclerView()
        session = SharedPref(thisParent)
        addRightCancelDrawable(v.findViewById(R.id.txcaridesa))
        v.txcaridesa.onRightDrawableClicked {
            it.text.clear()
            Showdesa(kec,"")
        }

        v.carides.setOnClickListener {
            Showdesa(kec,v.txcaridesa.text.toString())
        }
        val entries = ArrayList<BarEntry>()

        for (x in 0..nilai.count()-1) {
            entries.add(BarEntry(nilai[x].toFloat(), x))
        }
        val barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

        val labels = ArrayList<String>()
        for (x in 0..namadesa.count()-1) {
            labels.add(namadesa[x])
        }
        val data = BarData(labels, barDataSet)
        v.barChart.data = data

        v.barChart.animateY(1500)
        v.imageButton.setOnClickListener {
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_SHORT).show()
            }else{
            if (pilihbulan=="All"){
                pilihbulan=""
                grafpetabj(kec,pilihtahun,pilihbulan)
                grafpetdes(kec,pilihtahun,pilihbulan)
                val hand = Handler()
                hand.postDelayed({
                    val entries = ArrayList<BarEntry>()

                    for (x in 0..nilai.count()-1) {
                        entries.add(BarEntry(nilai[x].toFloat(), x))
                    }
                    val barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

                    val labels = ArrayList<String>()
                    for (x in 0..namadesa.count()-1) {
                        labels.add(namadesa[x])
                    }
                    val data = BarData(labels, barDataSet)
                    v.barChart.data = data

                    v.barChart.animateY(1500)
                },2000)
            }else{
                grafpetabj(kec,pilihtahun,pilihbulan)
                grafpetdes(kec,pilihtahun,pilihbulan)
                val hand = Handler()
                hand.postDelayed({
                    val entries = ArrayList<BarEntry>()

                    for (x in 0..nilai.count()-1) {
                        entries.add(BarEntry(nilai[x].toFloat(), x))
                    }
                    val barDataSet = BarDataSet(entries, "Angka Bebas Jentik dalam Persen")

                    val labels = ArrayList<String>()
                    for (x in 0..namadesa.count()-1) {
                        labels.add(namadesa[x])
                    }
                    val data = BarData(labels, barDataSet)
                    v.barChart.data = data

                    v.barChart.animateY(1500)
                },2000)
            }
            }
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
        desaAdapter = AdapterDesa(daftardesa)

        layoutManager = LinearLayoutManager(thisParent, RecyclerView.VERTICAL,false)

        v.lisdesa.layoutManager = layoutManager
        v.lisdesa.adapter = desaAdapter

        v.lisdesa.addItemDecoration(DividerItemDecoration(v.lisdesa.context,layoutManager.orientation))

        desaAdapter.setOnItemClickListener(object: AdapterDesa.OnItemClickListener{
            override fun setOnLongClickListener(v: View) {
                val intent = Intent(thisParent, detaildesa::class.java)
                intent.putExtra("id",v.txIddesa.text.toString())
                intent.putExtra("desa",v.txDesa.text.toString())
                startActivity(intent)
            }
        })
    }
    override fun onStart() {
        super.onStart()
        ShowWil(_id)
        val han = Handler()
        han.postDelayed({
            if (pilihtahun==""){
                Toast.makeText(thisParent,"Data Kosong!",Toast.LENGTH_SHORT).show()
            }else{
            if (pilihbulan=="All"){
                pilihbulan=""
                grafpetabj(kec,pilihtahun,pilihbulan)
                grafpetdes(kec,pilihtahun,pilihbulan)
            }else{
                grafpetabj(kec,pilihtahun,pilihbulan)
                grafpetdes(kec,pilihtahun,pilihbulan)
            }
            }
        },2000)
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

                    kec = wil.getValue("KEC").toString()
                    Showdesa(kec,"")
                    getthn(kec)
                    val han = Handler()
                    han.postDelayed({
                    if (pilihtahun==""){
                    }else {
                        getbln(kec, pilihtahun)
                    }
                    }, 1000)

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
    fun getbln(kec : String,tahun:String) {
        val request = object : StringRequest(
            Request.Method.POST, grafpet,
            Response.Listener { response ->
                daftarbulan.clear()
                daftarbulan.add(0,"All")
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarbulan.add(jsonObject.getString("bulan"))
                }
                bulanadapater.notifyDataSetChanged()
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
    fun Showdesa(nawid : String, nama:String) {
        val request = object : StringRequest(Request.Method.POST, urldesa,
            Response.Listener { response ->
                daftardesa.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("WIL_ID", jsonObject.getString("WIL_ID"))
                    rmh.put("DESA", jsonObject.getString("DESA"))
                    daftardesa.add(rmh)
                }
                desaAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("nawid",nawid)
                hm.put("nama",nama)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun grafpetabj(kec : String,tahun: String,bulan:String) {
        val request = object : StringRequest(
            Request.Method.POST, grafpetdes,
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
                hm.put("bulan",bulan)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    fun grafpetdes(kec : String,tahun: String,bulan:String) {
        val request = object : StringRequest(
            Request.Method.POST, grafpetdes,
            Response.Listener { response ->
                namadesa.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    namadesa.add(jsonObject.getString("DESA"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",kec)
                hm.put("tahun",tahun)
                hm.put("bulan",bulan)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}