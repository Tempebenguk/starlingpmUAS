package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragedituserownerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class fragubahuserowner : Fragment() {
    private lateinit var binding: FragedituserownerBinding
    private lateinit var database: DatabaseReference
    private var adminId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragedituserownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("Admin")

        // Get adminId from arguments
        adminId = arguments?.getString("adminId")
        if (adminId != null) {
            loadAdminData(adminId!!)
        }

        binding.btnUbahUser.setOnClickListener {
            updateAdmin()
        }

        return view
    }

    private fun loadAdminData(adminId: String) {
        database.child(adminId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val admin = dataSnapshot.getValue(admin::class.java)
                if (admin != null) {
                    binding.editIdAdmin.setText(adminId)
                    binding.editNamaAdmin.setText(admin.nama)
                    binding.editAlamatAdmin.setText(admin.alamat)
                    binding.editTeleponAdmin.setText(admin.noTelp)
                    binding.editPasswordAdmin.setText(admin.password)
                    if (admin.jenkel == "Laki-laki") {
                        binding.radioButtonL.isChecked = true
                    } else {
                        binding.radioButtonP.isChecked = true
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAdmin() {
        val id = binding.editIdAdmin.text.toString()
        val nama = binding.editNamaAdmin.text.toString().trim()
        val alamat = binding.editAlamatAdmin.text.toString().trim()
        val noTelp = binding.editTeleponAdmin.text.toString().trim()
        val password = binding.editPasswordAdmin.text.toString().trim()
        val selectedId = binding.radioGroupJenkel.checkedRadioButtonId
        val radioButton: RadioButton = binding.root.findViewById(selectedId)
        val jenkel = radioButton.text.toString()

        if (nama.isEmpty() || alamat.isEmpty() || noTelp.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val admin = admin(id, nama, alamat, noTelp, password, jenkel)

        database.child(id).setValue(admin).addOnCompleteListener {
            Toast.makeText(context, "Data Admin telah diperbarui", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()

        }.addOnFailureListener {
            Toast.makeText(context, "Gagal memperbarui data Admin", Toast.LENGTH_SHORT).show()
        }
    }
}
