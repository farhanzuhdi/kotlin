package minformatika.polinema.ta.Kader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import minformatika.polinema.ta.R


class AdapterRumah (val dataRumah : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterRumah.HolderDataRumah>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterRumah.HolderDataRumah {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_rum,p0,false)
        return HolderDataRumah(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataRumah.size
    }

    override fun onBindViewHolder(p0: AdapterRumah.HolderDataRumah, p1: Int) {
        val data  = dataRumah.get(p1)
        p0.txId.setText(data.getValue("RUM_ID"))
        p0.txNAMA.setText(data.getValue("PEMILIK"))
        p0.txAlamat.setText(data.getValue("ALAMAT"))
        p0.txNo.setText(data.getValue("no_TELP"))
    }

    class HolderDataRumah(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val txId = v.findViewById<TextView>(R.id.txId)
        val txNAMA = v.findViewById<TextView>(R.id.txNAMA)
        val txAlamat = v.findViewById<TextView>(R.id.txAlamat)
        val txNo = v.findViewById<TextView>(R.id.txNo)

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


