package com.example.proyekstarling

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.proyekstarling.R
import com.example.proyekstarling.databinding.ActivityWebserviceBinding
import com.squareup.picasso.Picasso

class AdapterDataMahasiswa(
    val dataMhs: List<HashMap<String, String>>,
    val b: ActivityWebserviceBinding,
    val daftarProdi: List<String>
) :
    RecyclerView.Adapter<AdapterDataMahasiswa.HolderDataMhs>() {

    inner class HolderDataMhs( v: View) : RecyclerView.ViewHolder(v){
        val txNim = v.findViewById<TextView>(R.id.txNim)
        val txNama = v.findViewById<TextView>(R.id.txNama)
        val txProdi = v.findViewById<TextView>(R.id.txProdi)
        val photo = v.findViewById<ImageView>(R.id.imageView)
        val cLayout = v.findViewById<ConstraintLayout>(R.id.layoutListMhs)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataMhs {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_mahasiswa,parent,false)
        return HolderDataMhs(v)
    }

    override fun onBindViewHolder(h: HolderDataMhs, position: Int) {
        val data = dataMhs.get(position)
        h.txNim.setText(data.get("nim"))
        h.txNama.setText(data.get("nama"))
        h.txProdi.setText(data.get("nama_prodi"))
        h.cLayout.setOnClickListener(View.OnClickListener {
            val pos = daftarProdi.indexOf(
                data.get("nama_prodi")
            )
            b.spinProdi.setSelection(pos)
            b.edNim.setText(data.get("nim"))
            b.edNamaMhs.setText(data.get("nama"))
            Picasso.get().load(data.get("url")).into(
                b.imUpload
            );
        })

        if (!data.get("url").equals(""))
            Picasso.get().load(data.get("url")).into(h.photo);
    }

    override fun getItemCount(): Int {
        return dataMhs.size
    }
}