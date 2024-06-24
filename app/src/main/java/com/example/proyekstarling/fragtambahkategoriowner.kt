package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragtambahkategoriownerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class fragtambahkategoriowner : Fragment() {
    private lateinit var binding: FragtambahkategoriownerBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragtambahkategoriownerBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseDatabase.getInstance().getReference("kategori")

        binding.btntbhktg.setOnClickListener {
            tambahKategori()
        }

        return view
    }

    private fun tambahKategori() {
        val idKategori = binding.tambahIdKtg.text.toString().trim()
        val namaKategori = binding.tambahNamaKtg.text.toString().trim()

        if (idKategori.isEmpty() || namaKategori.isEmpty()) {
            Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val kategori = HashMap<String, Any>()
        kategori["nama_kategori"] = namaKategori

        database.child(idKategori).setValue(kategori)
            .addOnSuccessListener {
                Toast.makeText(context, "Berhasil menambahkan kategori", Toast.LENGTH_SHORT).show()
                kosong()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menambahkan kategori", Toast.LENGTH_SHORT).show()
            }
    }

    private fun kosong() {
        binding.tambahIdKtg.text.clear()
        binding.tambahNamaKtg.text.clear()
    }
}
