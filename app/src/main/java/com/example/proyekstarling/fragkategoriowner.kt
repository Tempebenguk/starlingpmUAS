package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragkategoriownerBinding
import com.google.firebase.database.*

class fragkategoriowner : Fragment() {
    private lateinit var binding: FragkategoriownerBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragkategoriownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("kategori")

        binding.btnTambahKtg.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragtambahkategoriowner())
                .addToBackStack(null)
                .commit()
        }

        binding.btnCarika.setOnClickListener {
            val query = binding.cariKtg.text.toString()
            cariKategori(query)
        }

        ambilDataKategori()
        return view
    }

    private fun ambilDataKategori() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.ktgListContainer.removeAllViews()
                for (kategoriSnapshot in snapshot.children) {
                    val kategoriId = kategoriSnapshot.key
                    val kategori = kategoriSnapshot.getValue(kategori::class.java)
                    if (kategori != null && kategoriId != null) {
                        kategori.id = kategoriId
                        tambahkanKategoriKeView(kategori)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cariKategori(query: String) {
        database.orderByChild("nama_kategori").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.ktgListContainer.removeAllViews()
                    for (kategoriSnapshot in snapshot.children) {
                        val kategoriId = kategoriSnapshot.key
                        val kategori = kategoriSnapshot.getValue(kategori::class.java)
                        if (kategori != null && kategoriId != null) {
                            kategori.id = kategoriId
                            tambahkanKategoriKeView(kategori)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun tambahkanKategoriKeView(kategori: kategori) {
        val kategoriView = LayoutInflater.from(context).inflate(R.layout.kategori_item, binding.ktgListContainer, false)
        val kategoriNameTextView = kategoriView.findViewById<TextView>(R.id.KtgNameTextView)
        val editButton = kategoriView.findViewById<Button>(R.id.editButtonktg)
        val deleteButton = kategoriView.findViewById<Button>(R.id.deleteButtonktg)

        kategoriNameTextView.text = kategori.nama_kategori

        editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("kategoriId", kategori.id)
            val fragubahkategoriowner = fragubahkategoriowner()
            fragubahkategoriowner.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragubahkategoriowner)
                .addToBackStack(null)
                .commit()
        }

        deleteButton.setOnClickListener {
            database.child(kategori.id).removeValue().addOnCompleteListener {
                Toast.makeText(context, "Kategori dihapus", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ktgListContainer.addView(kategoriView)
    }
}
