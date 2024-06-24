package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragmenuownerBinding
import com.google.firebase.database.*

class fragmenuowner : Fragment() {
    private lateinit var binding: FragmenuownerBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmenuownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("menu")

        binding.btnTambahMenu.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragtambahmenuowner())
                .addToBackStack(null)
                .commit()
        }

        binding.btnCarii.setOnClickListener {
            val query = binding.cariKtgo.text.toString()
            cariMenu(query)
        }

        ambilDataMenu()
        return view
    }

    private fun ambilDataMenu() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.menuListContainer.removeAllViews()
                for (menuSnapshot in snapshot.children) {
                    val menuId = menuSnapshot.key
                    val menu = menuSnapshot.getValue(menu::class.java)
                    if (menu != null && menuId != null) {
                        menu.id = menuId
                        tambahkanMenuKeView(menu)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cariMenu(query: String) {
        database.orderByChild("nama_menu").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.menuListContainer.removeAllViews()
                    for (menuSnapshot in snapshot.children) {
                        val menuId = menuSnapshot.key
                        val menu = menuSnapshot.getValue(menu::class.java)
                        if (menu != null && menuId != null) {
                            menu.id = menuId
                            tambahkanMenuKeView(menu)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun tambahkanMenuKeView(menu: menu) {
        val menuView = LayoutInflater.from(context).inflate(R.layout.menu_item, binding.menuListContainer, false)
        val menuIdTextView = menuView.findViewById<TextView>(R.id.menuIdTextView)
        val menuNameTextView = menuView.findViewById<TextView>(R.id.menuNameTextView)
        val menuPriceTextView = menuView.findViewById<TextView>(R.id.menuPriceTextView)
        val menuStockTextView = menuView.findViewById<TextView>(R.id.menuStockTextView)
        val menuKategoriTextView = menuView.findViewById<TextView>(R.id.menuKategoriTextView)
        val editButton = menuView.findViewById<Button>(R.id.editButton)
        val deleteButton = menuView.findViewById<Button>(R.id.deleteButton)

        menuIdTextView.text = menu.id
        menuNameTextView.text = menu.nama_menu
        menuPriceTextView.text = "Harga: ${menu.harga}"
        menuStockTextView.text = "Stok: ${menu.stock}"
        menuKategoriTextView.text = menu.id_kategori

        editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("menuId", menu.id)
            val fragubahmenuowner = fragubahmenuowner()
            fragubahmenuowner.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragubahmenuowner)
                .addToBackStack(null)
                .commit()
        }

        deleteButton.setOnClickListener {
            database.child(menu.id).removeValue().addOnCompleteListener {
                Toast.makeText(context, "Menu dihapus", Toast.LENGTH_SHORT).show()
            }
        }
        binding.menuListContainer.addView(menuView)
    }
}
