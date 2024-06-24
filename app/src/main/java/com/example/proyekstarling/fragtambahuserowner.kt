package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragtambahuserownerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.proyekstarling.admin

class fragtambahuserowner : Fragment() {
    private lateinit var binding: FragtambahuserownerBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragtambahuserownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("Admin")

        binding.btnUbahUser.setOnClickListener {
            tambahAdmin()
        }
        return view
    }

    private fun tambahAdmin() {
        val id = binding.tambahIdAdmin.text.toString()
        val nama = binding.tambahNamaAdmin.text.toString().trim()
        val alamat = binding.tambahAlamatAdmin.text.toString().trim()
        val noTelp = binding.tambahTeleponAdmin.text.toString().trim()
        val password = binding.tambahPasswordAdmin.text.toString().trim()
        val selectedId = binding.radioGroupJenkel.checkedRadioButtonId
        val radioButton: RadioButton = binding.root.findViewById(selectedId)
        val jenkel = radioButton.text.toString()

        if (nama.isEmpty() || alamat.isEmpty() || noTelp.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val admin = admin(id, nama, alamat, noTelp, password, jenkel)

        database.child(id).setValue(admin).addOnCompleteListener {
            Toast.makeText(context, "Data Admin telah ditambahkan", Toast.LENGTH_SHORT).show()
            kosong()
            requireActivity().supportFragmentManager.popBackStack()

        }.addOnFailureListener {
            Toast.makeText(context, "Gagal menambahkan data Admin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kosong() {
        binding.tambahIdAdmin.text.clear()
        binding.tambahNamaAdmin.text.clear()
        binding.tambahAlamatAdmin.text.clear()
        binding.tambahTeleponAdmin.text.clear()
        binding.tambahPasswordAdmin.text.clear()
        binding.radioGroupJenkel.clearCheck()
    }
}