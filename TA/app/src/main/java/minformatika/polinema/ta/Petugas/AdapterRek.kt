package minformatika.polinema.ta.Petugas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import minformatika.polinema.ta.R


class AdapterRek (val dataRekap : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterRek.HolderDataRekap>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterRek.HolderDataRekap {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_rek_kad,p0,false)
        return HolderDataRekap(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataRekap.size
    }

    override fun onBindViewHolder(p0: AdapterRek.HolderDataRekap, p1: Int) {
        val data  = dataRekap.get(p1)
        p0.txTgl.setText(data.getValue("TGL_KUNJ"))
        p0.txStatus.setText(data.getValue("STATUS"))
        p0.txla.setText(data.getValue("LATITUDE"))
        p0.txlo.setText(data.getValue("LONGITUDE"))
    }

    class HolderDataRekap(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val txTgl = v.findViewById<TextView>(R.id.txTgl)
        val txStatus = v.findViewById<TextView>(R.id.txStatus)
        val txla= v.findViewById<TextView>(R.id.txla)
        val txlo= v.findViewById<TextView>(R.id.txlo)

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


