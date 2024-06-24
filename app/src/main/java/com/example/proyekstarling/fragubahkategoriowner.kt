package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FrageditkategoriownerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class fragubahkategoriowner : Fragment() {
    private lateinit var binding: FrageditkategoriownerBinding
    private lateinit var database: DatabaseReference
    private var kategoriId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrageditkategoriownerBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseDatabase.getInstance().getReference("kategori")
        kategoriId = arguments?.getString("kategoriId")

        if (kategoriId != null) {
            loadKategoriData(kategoriId!!)
        }

        binding.btnubahktg.setOnClickListener {
            updateKategoriData()
        }

        return view
    }

    private fun loadKategoriData(kategoriId: String) {
        database.child(kategoriId).get().addOnSuccessListener { dataSnapshot ->
            val kategori = dataSnapshot.getValue(kategori::class.java)
            if (kategori != null) {
                binding.tambahIdKtg.setText(kategoriId)
                binding.tambahNamaKtg.setText(kategori.nama_kategori)
            } else {
                Toast.makeText(context, "Gagal memuat data kategori", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal memuat data kategori", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateKategoriData() {
        val idKategori = binding.tambahIdKtg.text.toString()
        val namaKategori = binding.tambahNamaKtg.text.toString()

        if (idKategori.isEmpty() || namaKategori.isEmpty()) {
            Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val kategoriMap = mapOf<String, Any>(
            "nama_kategori" to namaKategori
        )

        database.child(idKategori).updateChildren(kategoriMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
