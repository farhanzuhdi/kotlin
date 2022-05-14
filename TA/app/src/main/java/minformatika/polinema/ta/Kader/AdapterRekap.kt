package minformatika.polinema.ta.Kader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import minformatika.polinema.ta.R


class AdapterRekap (val dataRekap : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterRekap.HolderDataRekap>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterRekap.HolderDataRekap {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_rek,p0,false)
        return HolderDataRekap(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataRekap.size
    }

    override fun onBindViewHolder(p0: AdapterRekap.HolderDataRekap, p1: Int) {
        val data  = dataRekap.get(p1)
        p0.txId.setText(data.getValue("REK_ID"))
        p0.txTgl.setText(data.getValue("TGL_KUNJ"))
        p0.txStatus.setText(data.getValue("STATUS"))
    }

    class HolderDataRekap(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val txId = v.findViewById<TextView>(R.id.txId)
        val txTgl = v.findViewById<TextView>(R.id.txTgl)
        val txStatus = v.findViewById<TextView>(R.id.txStatus)

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


