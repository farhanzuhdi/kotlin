package minformatika.polinema.ta.Kader

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.data_frag_prof.*
import kotlinx.android.synthetic.main.data_frag_prof.view.*
import kotlinx.android.synthetic.main.konf_logout.view.*
import kotlinx.android.synthetic.main.mod_edit.view.*
import kotlinx.android.synthetic.main.mod_ubah_foto.*
import kotlinx.android.synthetic.main.mod_ubah_foto.view.*
import kotlinx.android.synthetic.main.ubahjenkel.view.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FragmentProfil : Fragment() {

    lateinit var session: SharedPref
    lateinit var thisParent : Dashboard
    lateinit var v: View
    var nama = ""
    var _id = ""
    var jenkel=""
    var imStr = ""
    var urlProfil = "http://192.168.43.37/myapi/profil.php"
    var urlnama = "http://192.168.43.37/myapi/updatenama.php"
    var urlno = "http://192.168.43.37/myapi/updateno.php"
    var urlemail = "http://192.168.43.37/myapi/updateemail.php"
    var urlalamat = "http://192.168.43.37/myapi/updatealamat.php"
    var urlEmail = "http://192.168.43.37/myapi/email.php"
    var urlfoto = "http://192.168.43.37/myapi/updatefoto.php"
    var urljenkel = "http://192.168.43.37/myapi/updatejenkel.php"
    lateinit var mediaHelper: MediaHelper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as Dashboard
        session = SharedPref(thisParent)
        v = inflater.inflate(R.layout.data_frag_prof, container, false)
        val jen= arrayOf("Laki-laki","Perempuan")
        nama = SharedPref.getInstance(thisParent).user.username.toString()
        _id = SharedPref.getInstance(thisParent).user.id.toString()

        mediaHelper = MediaHelper(thisParent)
        v.profusername.setText(nama)
        v.profid.setText(_id)
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

        v.ubahnama.setOnClickListener{
            val UNama = LayoutInflater.from(thisParent).inflate(R.layout.mod_edit,null)
            val builder = AlertDialog.Builder(thisParent).setView(UNama)
            val dial = builder.show()

            UNama.txModUbah.setText("Nama")
            UNama.edit.setText(v.profnama.text.toString())
            UNama.button3.setOnClickListener {
                dial.dismiss()
            }
            UNama.button2.setOnClickListener {
                updatenama("update",_id,UNama.edit.text.toString())
                val han = Handler()
                han.postDelayed({
                    ShowProfil(_id)
                    dial.dismiss()
                },1000)
            }
        }
        v.ubahalamat.setOnClickListener{
            val Ualamat = LayoutInflater.from(thisParent).inflate(R.layout.mod_edit,null)
            val builder = AlertDialog.Builder(thisParent).setView(Ualamat)
            val dial = builder.show()

            Ualamat.txModUbah.setText("Alamat")
            Ualamat.edit.setText(v.profalamat.text.toString())

            Ualamat.button3.setOnClickListener {
                dial.dismiss()
            }
            Ualamat.button2.setOnClickListener {
                updatealamat("update",_id,Ualamat.edit.text.toString())
                val han = Handler()
                han.postDelayed({
                    ShowProfil(_id)
                    dial.dismiss()
                },1000)
            }
        }
        v.ubahno.setOnClickListener{
            val UNo = LayoutInflater.from(thisParent).inflate(R.layout.mod_edit,null)
            val builder = AlertDialog.Builder(thisParent).setView(UNo)
            val dial = builder.show()

            UNo.txModUbah.setText("No Telp")
            UNo.edit.setText(v.profno.text.toString())

            UNo.button3.setOnClickListener {
                dial.dismiss()
            }
            UNo.button2.setOnClickListener {
                updateno("update",_id,UNo.edit.text.toString())
                val han = Handler()
                han.postDelayed({
                    ShowProfil(_id)
                    dial.dismiss()
                },1000)
            }
        }
        v.ubahemail.setOnClickListener{
            val Uemail = LayoutInflater.from(thisParent).inflate(R.layout.mod_edit,null)
            val builder = AlertDialog.Builder(thisParent).setView(Uemail)
            val dial = builder.show()

            Uemail.txModUbah.setText("Email")
            Uemail.edit.setText(v.profemail.text.toString())

            Uemail.button3.setOnClickListener {
                dial.dismiss()
            }
            Uemail.button2.setOnClickListener {
                updateemail("update",_id,Uemail.edit.text.toString())
                val han = Handler()
                han.postDelayed({
                    Showemail(_id)
                    dial.dismiss()
                },1000)
            }
        }
        v.ubahjenkel.setOnClickListener{
            val Ujen = LayoutInflater.from(thisParent).inflate(R.layout.ubahjenkel,null)
            val builder = AlertDialog.Builder(thisParent).setView(Ujen)
            val dial = builder.show()

            Ujen.spinjenkel.adapter = ArrayAdapter<String>(thisParent,android.R.layout.simple_list_item_1,jen)

            Ujen.spinjenkel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){
                    Ujen.spinjenkel.setSelection(0)
                    Ujen.spinjenkel.get(0)
                    jenkel = jen.get(0)
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                    jenkel = jen.get(position)
                }
            }
            Ujen.button6.setOnClickListener {
                dial.dismiss()
            }
            Ujen.Ubah.setOnClickListener {
                updatejenkel("update",_id,jenkel)
                val han = Handler()
                han.postDelayed({
                    ShowProfil(_id)
                    dial.dismiss()
                },1000)
            }

        }
        v.ubahfoto.setOnClickListener {
            val Ufot = LayoutInflater.from(thisParent).inflate(R.layout.mod_ubah_foto,null)
            val builder = AlertDialog.Builder(thisParent).setView(Ufot)
            val di = builder.show()

            Ufot.bat.setOnClickListener {
                di.dismiss()
            }
            Ufot.up.setOnClickListener {
                val intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(intent, mediaHelper.getRcGallery())
                val hand = Handler()
                hand.postDelayed({
                    Ufot.fotup.setText("Upload foto berhasil")
                },1000)
            }
            Ufot.sim.setOnClickListener {
                updatefoto("update", _id, v.profusername.text.toString())
                val han =Handler()
                han.postDelayed({
                    ShowProfil(_id)
                },1000)
                di.dismiss()
            }
        }

        return v
    }


        override fun onStart() {
        super.onStart()
        ShowProfil(_id)
        Showemail(_id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaHelper.getRcGallery()) {
                imStr = mediaHelper.getBitmapToString(data!!.data, pp)
            }
        }
    }

    fun ShowProfil(prof_id : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlProfil,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var prof = HashMap<String, String>()
                    prof.put("NAMA", jsonObject.getString("NAMA"))
                    prof.put("GENDER", jsonObject.getString("GENDER"))
                    prof.put("ALAMAT", jsonObject.getString("ALAMAT"))
                    prof.put("NO_TELP", jsonObject.getString("NO_TELP"))
                    prof.put("foto", jsonObject.getString("foto"))

                    v.profnama.setText(prof.getValue("NAMA").toString())
                    v.profjenkel.setText(prof.getValue("GENDER").toString())
                    v.profalamat.setText(prof.getValue("ALAMAT").toString())
                    v.profno.setText(prof.getValue("NO_TELP").toString())
                    if(!prof.getValue("foto").equals(""))
                        Picasso.get().load(prof.getValue("foto")).into(v.pp);
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("profid",prof_id)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun Showemail(prof_id : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlEmail,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var prof = HashMap<String, String>()
                    prof.put("email", jsonObject.getString("email"))

                    v.profemail.setText(prof.getValue("email").toString())

                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("profid",prof_id)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
    fun updatenama(mode:String, petid:String, nama:String){
        val Request = object : StringRequest(Method.POST,urlnama,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah nama berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah nama gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("nama", nama)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
    fun updatealamat(mode:String, petid:String, alamat:String){
        val Request = object : StringRequest(Method.POST,urlalamat,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah alamat berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah alamat gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("alamat", alamat)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
    fun updateno(mode:String, petid:String, no:String){
        val Request = object : StringRequest(Method.POST,urlno,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah No Telp berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah No Telp gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("no", no)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
    fun updateemail(mode:String, petid:String, email:String){
        val Request = object : StringRequest(Method.POST,urlemail,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah Email berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah Email gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("email", email)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }

    fun updatejenkel(mode:String, petid:String, jenkel:String){
        val Request = object : StringRequest(Method.POST,urljenkel,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah gender berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah gender gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("jenkel", jenkel)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }

    fun updatefoto(mode:String, petid:String, nama:String){
        val Request = object : StringRequest(Method.POST,urlfoto,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if(error.equals("000")){
                    Toast.makeText(thisParent, "Ubah foto berhasil",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(thisParent, "Ubah foto gagal",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                val nmfile = nama+"_KD" + SimpleDateFormat("yyyMMddHHmmss", Locale.getDefault())
                    .format(Date())+".jpg"
                when (mode) {
                    "update" -> {
                        hm.put("mode", mode)
                        hm.put("petid", petid)
                        hm.put("image", imStr)
                        hm.put("file", nmfile)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(Request)
    }
}