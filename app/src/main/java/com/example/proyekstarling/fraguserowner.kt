package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FraguserownerBinding
import com.google.firebase.database.*

class fraguserowner : Fragment() {
    private lateinit var binding: FraguserownerBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FraguserownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("Admin")

        binding.btnTambahMhs.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragtambahuserowner())
                .addToBackStack(null)
                .commit()
        }
        ambilDataAdmin()
        return view
    }

    private fun ambilDataAdmin() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.adminListContainer.removeAllViews()
                for (adminSnapshot in snapshot.children) {
                    val adminId = adminSnapshot.key
                    val admin = adminSnapshot.getValue(admin::class.java)
                    if (admin != null && adminId != null) {
                        admin.id = adminId
                        tambahkanAdminKeView(admin)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tambahkanAdminKeView(admin: admin) {
        val adminView = LayoutInflater.from(context).inflate(R.layout.user_item, binding.adminListContainer, false)
        val adminNameTextView = adminView.findViewById<TextView>(R.id.AdminNameTextView)
        val editButton = adminView.findViewById<Button>(R.id.editButtonadm)
        val deleteButton = adminView.findViewById<Button>(R.id.deleteButtonadm)

        adminNameTextView.text = admin.nama

        editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("adminId", admin.id)
            val fragubahuserowner = fragubahuserowner()
            fragubahuserowner.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragubahuserowner)
                .addToBackStack(null)
                .commit()
        }

        deleteButton.setOnClickListener {
            database.child(admin.id).removeValue().addOnCompleteListener {
                Toast.makeText(context, "Admin dihapus", Toast.LENGTH_SHORT).show()
            }
        }
        binding.adminListContainer.addView(adminView)
    }
}
