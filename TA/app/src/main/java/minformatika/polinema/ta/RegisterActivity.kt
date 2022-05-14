package minformatika.polinema.ta

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {

    lateinit var kecAdapter: ArrayAdapter<String>
    lateinit var desaAdapter: ArrayAdapter<String>
    var daftarkec = mutableListOf<String>()
    var daftardesa = mutableListOf<String>()
    var urlMaxpetid = "http://192.168.43.37/myapi/max.php"
    var urlkec = "http://192.168.43.37/myapi/kec.php"
    var urldesa = "http://192.168.43.37/myapi/desa.php"
    val urlreg = "http://192.168.43.37/myapi/register_api.php?apicall=signup"
    var maxpetid = 0
    var maxp = 0
    var pilihkec = ""
    var pilihdesa= ""
    val kecpilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            regKec.setSelection(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihkec = daftarkec.get(position)
            getdesa(pilihkec)
            regDes.setSelection(0)
        }
    }
    val desapilih = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            regDes.setSelection(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihdesa = daftardesa.get(position)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        kecAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line,
            daftarkec
        )
        regKec.adapter = kecAdapter
        regKec.onItemSelectedListener = kecpilih

        desaAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line,
            daftardesa
        )
        daftardesa.add(0,"Desa")
        regDes.adapter = desaAdapter
        regDes.onItemSelectedListener = desapilih

        txtlogin.setOnClickListener {  startActivity(Intent(this@RegisterActivity,act_login::class.java)) }

        btregister.setOnClickListener {
            registerUser()
        }

    }

    override fun onStart() {
        super.onStart()
        maxpet("1")
        getKec()
    }

    fun maxpet(pet : String) {
        val request = object : StringRequest(
            Request.Method.POST, urlMaxpetid,
            com.android.volley.Response.Listener { response ->
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var max = HashMap<String, String>()
                    max.put("max", jsonObject.getString("max"))

                    maxpetid = (max.getValue("max").toString()).toInt()
                    maxp = maxpetid+1
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("pet",pet)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun getKec() {
        val request = StringRequest(Request.Method.POST, urlkec,
            com.android.volley.Response.Listener { response ->
                daftarkec.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarkec.add(jsonObject.getString("kec"))
                }
                daftarkec.add(0,"Kecamatan")
                kecAdapter.notifyDataSetChanged()
            },
            com.android.volley.Response.ErrorListener { error -> }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    fun getdesa(kec : String) {
        val request = object : StringRequest(
            Request.Method.POST, urldesa,
            com.android.volley.Response.Listener { response ->
                daftardesa.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)

                    daftardesa.add(jsonObject.getString("desa"))
                }
                daftardesa.add(0,"Desa")
                desaAdapter.notifyDataSetChanged()
            },
            com.android.volley.Response.ErrorListener { error -> }
        ){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("kec",kec)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun registerUser() {
        val regnama = regNama.text.toString()
        val regpass = regPassword.text.toString()
        val konfirpas = konfir.text.toString()
        val reguser = regUser.text.toString()
        val regemail = regEmail.text.toString()
        val regno = regNo.text.toString()
        val regalamat = regAlamat.text.toString()

        if (TextUtils.isEmpty(reguser)) {
            regUser.error = "Tidak boleh kosong"
            regUser.requestFocus()
            return
        }
        if (TextUtils.isEmpty(regpass)) {
            regPassword.error = "Tidak boleh kosong"
            regPassword.requestFocus()
            return
        }
        if (konfirpas!=regpass) {
            konfir.error = "ulangi konfirm password"
            konfir.requestFocus()
            return
        }
        if (TextUtils.isEmpty(regnama)) {
            regNama.error = "Tidak boleh kosong"
            regNama.requestFocus()
            return
        }
        if (TextUtils.isEmpty(regemail)) {
            regEmail.error = "Tidak boleh kosong"
            regEmail.requestFocus()
            return
        }
        if (TextUtils.isEmpty(regno)) {
            regNo.error = "Tidak boleh kosong"
            regNo.requestFocus()
            return
        }
        if (TextUtils.isEmpty(regalamat)) {
            regAlamat.error = "Tidak boleh kosong"
            regAlamat.requestFocus()
            return
        }
        if (regKec.selectedItemPosition==0){
            val errorTextview = regKec.getSelectedView() as TextView
            errorTextview.error = ""
            errorTextview.setTextColor(Color.BLACK)
            errorTextview.text = "Pilih Kecamatan"
            return
        }
        if (regDes.selectedItemPosition==0){
            val errorTextview = regDes.getSelectedView() as TextView
            errorTextview.error = ""
            errorTextview.setTextColor(Color.BLACK)
            errorTextview.text = "Pilih Desa"
            return
        }

        val stringRequest = object : StringRequest(Request.Method.POST,urlreg,
            com.android.volley.Response.Listener { response ->

                try {
                    //converting response to json object
                    val obj = JSONObject(response)
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()

                        //getting the user from the response
                        val userJson = obj.getJSONObject("user")

                        //creating a new user object
                        val user = User(
                            userJson.getInt("pet_id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("level")
                        )
                        var i: Intent = Intent(applicationContext,act_login::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else {
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            com.android.volley.Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["username"] = reguser
                params["email"] = regemail
                params["password"] = regpass
                params["petid"] = maxp.toString()
                params["nama"] = regnama
                params["desa"] = pilihdesa
                params["kec"] = pilihkec
                params["no"] = regno
                params["alamat"] = regalamat
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}
