package minformatika.polinema.ta.Petugas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import minformatika.polinema.ta.R


class AdapterPerKad (val dataRekap : List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterPerKad.HolderDataRekap>() {

    private lateinit var mListener:OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterPerKad.HolderDataRekap {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_perkad,p0,false)
        return HolderDataRekap(v,mListener)
    }

    override fun getItemCount(): Int {
        return dataRekap.size
    }

    override fun onBindViewHolder(p0: AdapterPerKad.HolderDataRekap, p1: Int) {
        val data  = dataRekap.get(p1)
        p0.nama.setText(data.getValue("NAMA"))
        p0.desa.setText(data.getValue("DESA"))
        p0.jmlkun.setText(data.getValue("kun"))
    }

    class HolderDataRekap(v : View, var mlistener: OnItemClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener,
    View.OnLongClickListener{
        override fun onClick(p0: View?) {

        }
        val nama = v.findViewById<TextView>(R.id.nama)
        val desa = v.findViewById<TextView>(R.id.desa)
        val jmlkun= v.findViewById<TextView>(R.id.jmlkun)

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


