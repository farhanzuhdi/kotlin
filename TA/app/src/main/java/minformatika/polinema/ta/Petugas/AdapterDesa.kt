package minformatika.polinema.ta.Petugas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import minformatika.polinema.ta.R
import org.json.JSONArray


class AdapterDesa (val dataDesa : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterDesa.HolderDataDesa>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterDesa.HolderDataDesa {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_desa,p0,false)
        return HolderDataDesa(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataDesa.size
    }

    override fun onBindViewHolder(p0: AdapterDesa.HolderDataDesa, p1: Int) {
        val data  = dataDesa.get(p1)
        p0.txId.setText(data.getValue("WIL_ID"))
        p0.txnamadesa.setText(data.getValue("DESA"))
    }

    class HolderDataDesa(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val txId = v.findViewById<TextView>(R.id.txIddesa)
        val txnamadesa = v.findViewById<TextView>(R.id.txDesa)

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(p0: View): Boolean {
            if(mlistener != null){
                mlistener.setOnLongClickListener(p0)
            }
            return true
        }
    }
    interface OnItemClickListener
    {
        fun setOnLongClickListener(v: View)
    }

    fun setOnItemClickListener(mListener:OnItemClickListener)
    {
        this.mListener = mListener
    }
}


