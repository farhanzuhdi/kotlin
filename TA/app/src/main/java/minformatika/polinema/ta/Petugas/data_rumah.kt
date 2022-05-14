package minformatika.polinema.ta.Petugas

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.data_rumah.*
import kotlinx.android.synthetic.main.row_rum.*
import kotlinx.android.synthetic.main.row_rum.view.*
import minformatika.polinema.ta.Kader.AdapterRumah
import minformatika.polinema.ta.R
import org.json.JSONArray

class data_rumah: AppCompatActivity() {

    var pet_id = ""
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var RumahAdapter : AdapterRum
    var daftarRumah = mutableListOf<HashMap<String, String>>()
    val urlRumah = "http://192.168.43.37/myapi/rumahkad.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_rumah)
        pet_id = intent.getStringExtra("petid").toString()

        addRightCancelDrawable(findViewById(R.id.txCariRumah))

        initRecyclerView()

        txCariRumah.onRightDrawableClicked {
            it.text.clear()
            ShowDataRumah(pet_id,"")
        }

        carirum.setOnClickListener {
            ShowDataRumah(pet_id,txCariRumah.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        ShowDataRumah(pet_id,"")
    }

    private fun initRecyclerView(){
        RumahAdapter = AdapterRum(daftarRumah)

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        listrumah.layoutManager = layoutManager
        listrumah.adapter = RumahAdapter

        listrumah.addItemDecoration(DividerItemDecoration(listrumah.context,layoutManager.orientation))

        RumahAdapter.setOnItemClickListener(object: AdapterRum.OnItemClickListener {
            override fun setOnLongClickListener(v: View) {
                val intent = Intent(this@data_rumah, rekap_kader::class.java)
                intent.putExtra("petid",pet_id)
                intent.putExtra("rumid",v.txId.text.toString())
                intent.putExtra("nama",v.txNAMA.text.toString())
                startActivity(intent)
            }
        })
    }


    private fun addRightCancelDrawable(editText: EditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.close)
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

    fun ShowDataRumah(petid:String,nama:String) {
        val request = object : StringRequest(
            Request.Method.POST, urlRumah,
            Response.Listener { response ->
                daftarRumah.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()- 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    var rmh = HashMap<String, String>()
                    rmh.put("RUM_ID", jsonObject.getString("RUM_ID"))
                    rmh.put("PEMILIK", jsonObject.getString("PEMILIK"))
                    rmh.put("ALAMAT", jsonObject.getString("ALAMAT"))
                    rmh.put("no_TELP", jsonObject.getString("no_TELP"))
                    daftarRumah.add(rmh)
                }
                RumahAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("petid",petid)
                hm.put("nama",nama)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}