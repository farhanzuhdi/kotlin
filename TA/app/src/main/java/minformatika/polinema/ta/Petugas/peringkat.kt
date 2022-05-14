package minformatika.polinema.ta.Petugas

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.konf_logout.view.*
import kotlinx.android.synthetic.main.peringkat.*
import kotlinx.android.synthetic.main.peringkat.view.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray

class peringkat: Fragment() {
    lateinit var thisParent : Dashboard
    lateinit var v: View
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var session: SharedPref
    lateinit var perkadadapter : AdapterPerKad
    var daftarperkad = mutableListOf<HashMap<String, String>>()
    val urlkec = "http://192.168.43.37/myapi/namakec.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    var urlperkad = "http://192.168.43.37/myapi/perkad.php"
    var _id = ""
    var kec=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard

        v = inflater.inflate(R.layout.peringkat, container, false)
        session = SharedPref(thisParent)

        v.log.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val log = LayoutInflater.from(thisParent).inflate(R.layout.konf_logout,null)
                val bil = AlertDialog.Builder(thisParent).setView(log)
                val dil = bil.show()

                log.kya.setOnClickListener {
                    session.logout()
                }
                log.ktidak.setOnClickListener {
                    dil.dismiss()
                }
            }
        })
        initRecyclerView()

        _id = SharedPref.getInstance(thisParent).user.id.toString()
        return v
    }

    private fun initRecyclerView(){
        perkadadapter = AdapterPerKad(daftarperkad)

        layoutManager = LinearLayoutManager(thisParent, RecyclerView.VERTICAL,false)

        v.kadper.layoutManager = layoutManager
        v.kadper.adapter = perkadadapter

        v.kadper.addItemDecoration(DividerItemDecoration(v.kadper.context,layoutManager.orientation))

        perkadadapter.setOnItemClickListener(object: AdapterPerKad.OnItemClickListener {
            override fun setOnLongClickListener(v: View) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        ShowWil(_id)
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

                    kec = wil.getValue("KEC").toString()
                    perkad(kec)
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

    fun perkad(wilid : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlperkad,
            Response.Listener { response ->
                daftarperkad.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var wil = HashMap<String, String>()
                    wil.put("NAMA", jsonObject.getString("NAMA"))
                    wil.put("DESA", jsonObject.getString("DESA"))
                    wil.put("kun", jsonObject.getString("kun"))
                    daftarperkad.add(wil)
                }
                perkadadapter.notifyDataSetChanged()
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