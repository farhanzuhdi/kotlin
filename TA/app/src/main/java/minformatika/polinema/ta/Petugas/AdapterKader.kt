package minformatika.polinema.ta.Petugas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import minformatika.polinema.ta.R


class AdapterKader (val dataKader : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterKader.HolderDataKader>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterKader.HolderDataKader {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_data_kader,p0,false)
        return HolderDataKader(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataKader.size
    }

    override fun onBindViewHolder(p0: AdapterKader.HolderDataKader, p1: Int) {
        val data  = dataKader.get(p1)
        p0.txId.setText(data.getValue("PET_ID"))
        p0.txnamakader.setText(data.getValue("NAMA"))
        p0.txwilkader.setText(data.getValue("DESA"))
        p0.txnokader.setText(data.getValue("NO_TELP"))
    }

    class HolderDataKader(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val txId = v.findViewById<TextView>(R.id.kaderid)
        val txnamakader = v.findViewById<TextView>(R.id.txNamaKader)
        val txwilkader = v.findViewById<TextView>(R.id.txWilKader)
        val txnokader = v.findViewById<TextView>(R.id.txNoKader)

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


