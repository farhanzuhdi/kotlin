package minformatika.polinema.ta.Petugas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.data_frag_pet.view.*
import kotlinx.android.synthetic.main.data_frag_prof.view.*
import kotlinx.android.synthetic.main.data_frag_rumah.view.*
import kotlinx.android.synthetic.main.mod_non.view.*
import kotlinx.android.synthetic.main.mod_opsi_pet.view.*
import kotlinx.android.synthetic.main.mod_verif.view.*
import kotlinx.android.synthetic.main.mod_verif.view.imageButton3
import kotlinx.android.synthetic.main.mod_verif.view.txkaderid
import kotlinx.android.synthetic.main.mod_verif.view.ver
import kotlinx.android.synthetic.main.row_data_kader.view.*
import minformatika.polinema.ta.Kader.rekap
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray
import org.json.JSONObject

class FragmentDataPetugas : Fragment() {

    lateinit var session: SharedPref
    lateinit var thisParent : Dashboard
    lateinit var v : View
    lateinit var kaderAdapter : AdapterKader
    lateinit var kaderBelumAdapter : AdapterbelumKader
    var daftarKader = mutableListOf<HashMap<String, String>>()
    var daftarKaderBelum = mutableListOf<HashMap<String, String>>()
    val urlKader = "http://192.168.43.37/myapi/showpet.php"
    val urlverifkad = "http://192.168.43.37/myapi/verifkad.php"
    val urlhapusverifkad = "http://192.168.43.37/myapi/hapusverifkad.php"
    val urlKaderbelum = "http://192.168.43.37/myapi/showpetbelum.php"
    val urlWil = "http://192.168.43.37/myapi/wil.php"
    val urlkec = "http://192.168.43.37/myapi/namakec.php"
    var urlProfil = "http://192.168.43.37/myapi/profil.php"
    var _id = ""
    var kec = ""
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var lmanager : LinearLayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard

        v = inflater.inflate(R.layout.data_frag_pet,container,false)
        session = SharedPref(thisParent)
        _id = SharedPref.getInstance(thisParent).user.id.toString()

        initRecycler()
        initRecyclerView()

        addRightCancelDrawable(v.findViewById(R.id.txcaripetugas))
        v.txcaripetugas.onRightDrawableClicked {
            it.text.clear()
            Showkad(kec,"")
            Showkadbelum(kec,"")
        }

        v.caripet.setOnClickListener {
            Showkad(kec,v.txcaripetugas.text.toString())
            Showkadbelum(kec,v.txcaripetugas.text.toString())
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
        kaderAdapter = AdapterKader(daftarKader)

        layoutManager = LinearLayoutManager(thisParent, RecyclerView.VERTICAL,false)

        v.listpet.layoutManager = layoutManager
        v.listpet.adapter = kaderAdapter

        v.listpet.addItemDecoration(DividerItemDecoration(v.listpet.context,layoutManager.orientation))

        kaderAdapter.setOnItemClickListener(object: AdapterKader.OnItemClickListener{
            override fun setOnLongClickListener(v: View) {
                val op = LayoutInflater.from(thisParent).inflate(R.layout.mod_opsi_pet,null)
                val bu = AlertDialog.Builder(thisParent).setView(op)
                val di = bu.show()

                op.txX.setOnClickListener {
                    di.dismiss()
                }

                op.rumkad.setOnClickListener {
                    val intent = Intent(thisParent, data_rumah::class.java)
                    intent.putExtra("petid",v.kaderid.text.toString())
                    startActivity(intent)
                }
                op.hapus.setOnClickListener {
                    val dView = LayoutInflater.from(thisParent).inflate(R.layout.mod_non,null)
                    val builder = AlertDialog.Builder(thisParent).setView(dView)
                    val aDialog = builder.show()
                    dView.txkaderid.setText(v.kaderid.text.toString())

                    val request = object : StringRequest(
                        Request.Method.POST, urlProfil,
                        Response.Listener { response ->
                            val jsonArray = JSONArray(response)
                            for (x in 0..(jsonArray.length()- 1)) {
                                val jsonObject = jsonArray.getJSONObject(x)
                                var prof = HashMap<String, String>()
                                prof.put("NAMA", jsonObject.getString("NAMA"))
                                prof.put("ALAMAT", jsonObject.getString("ALAMAT"))
                                prof.put("NO_TELP", jsonObject.getString("NO_TELP"))

                                dView.nonnam.setText(prof.getValue("NAMA").toString())
                                dView.nonalam.setText(prof.getValue("ALAMAT").toString())
                                dView.nonnof.setText(prof.getValue("NO_TELP").toString())
                            }
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String> {
                            val hm = HashMap<String,String>()
                            hm.put("profid",v.kaderid.text.toString())
                            return hm
                        }
                    }
                    val queue = Volley.newRequestQueue(thisParent)
                    queue.add(request)

                    dView.ver.setOnClickListener{
                        hapusverifkad("update",dView.txkaderid.text.toString())
                        val hand = Handler()
                        hand.postDelayed({
                            Showkad(kec,"")
                            Showkadbelum(kec,"")
                            aDialog.dismiss()
                            di.dismiss()
                        },1000)
                    }
                    dView.imageButton3.setOnClickListener {
                        aDialog.dismiss()
                    }
                }
            }

        })
    }

    private fun initRecycler(){
        kaderBelumAdapter = AdapterbelumKader(daftarKaderBelum)

        lmanager = LinearLayoutManager(thisParent, RecyclerView.VERTICAL,false)

        v.petbelum.layoutManager = lmanager
        v.petbelum.adapter = kaderBelumAdapter

        v.petbelum.addItemDecoration(DividerItemDecoration(v.petbelum.context,lmanager.orientation))

        kaderBelumAdapter.setOnItemClickListener(object: AdapterbelumKader.OnItemClickListener{
            override fun setOnLongClickListener(v: View) {
                val dView = LayoutInflater.from(thisParent).inflate(R.layout.mod_verif,null)
                val builder = AlertDialog.Builder(thisParent).setView(dView)
                val aDialog = builder.show()
                dView.txkaderid.setText(v.kaderid.text.toString())
                val request = object : StringRequest(
                    Request.Method.POST, urlProfil,
                    Response.Listener { response ->
                        val jsonArray = JSONArray(response)
                        for (x in 0..(jsonArray.length()- 1)) {
                            val jsonObject = jsonArray.getJSONObject(x)
                            var prof = HashMap<String, String>()
                            prof.put("NAMA", jsonObject.getString("NAMA"))
                            prof.put("ALAMAT", jsonObject.getString("ALAMAT"))
                            prof.put("NO_TELP", jsonObject.getString("NO_TELP"))

                            dView.vefnam.setText(prof.getValue("NAMA").toString())
                            dView.vefalam.setText(prof.getValue("ALAMAT").toString())
                            dView.vefnof.setText(prof.getValue("NO_TELP").toString())
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String> {
                        val hm = HashMap<String,String>()
                        hm.put("profid",v.kaderid.text.toString())
                        return hm
                    }
                }
                val queue = Volley.newRequestQueue(thisParent)
                queue.add(request)

                dView.ver.setOnClickListener{
                    verifkad("update",dView.txkaderid.text.toString())
                    val hand = Handler()
                    hand.postDelayed({
                        Showkad(kec,"")
                        Showkadbelum(kec,"")
                        aDialog.dismiss()
                    },1000)
                }
                dView.imageButton3.setOnClickListener {
                    aDialog.dismiss()
                }

            }

        })
    }

    override fun onStart() {
        super.onStart()
        ShowWil(_id)
        val hand = Handler()
        hand.postDelayed({
            Showkad(kec,"")
            Showkadbelum(kec,"")
        },2000)
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

    fun Showkad(nawid : String,nama:String) {
        val request = object : StringRequest(Request.Method.POST, urlKader,
            Response.Listener { response ->
                daftarKader.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("PET_ID", jsonObject.getString("PET_ID"))
                    rmh.put("NAMA", jsonObject.getString("NAMA"))
                    rmh.put("DESA", jsonObject.getString("DESA"))
                    rmh.put("NO_TELP", jsonObject.getString("NO_TELP"))
                    daftarKader.add(rmh)
                }
                kaderAdapter.notifyDataSetChanged()
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
    fun Showkadbelum(nawid : String, nama:String) {
        val request = object : StringRequest(Request.Method.POST, urlKaderbelum,
            Response.Listener { response ->
                daftarKaderBelum.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("PET_ID", jsonObject.getString("PET_ID"))
                    rmh.put("NAMA", jsonObject.getString("NAMA"))
                    rmh.put("DESA", jsonObject.getString("DESA"))
                    rmh.put("NO_TELP", jsonObject.getString("NO_TELP"))
                    daftarKaderBelum.add(rmh)
                }
                kaderBelumAdapter.notifyDataSetChanged()
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

    fun verifkad(mode:String, petid:String){
        val Request = object : StringRequest(Method.POST,urlverifkad,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Verifikasi kader berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Verifikasi kader gagal",Toast.LENGTH_LONG).show()
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
                        hm.put("petid", petid)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
    fun hapusverifkad(mode:String, petid:String){
        val Request = object : StringRequest(Method.POST,urlhapusverifkad,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Hapus verifikasi kader berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Hapus verifikasi kader gagal",Toast.LENGTH_LONG).show()
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
                        hm.put("petid", petid)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
}