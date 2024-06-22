package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyekstarling.databinding.FragmenuownerBinding
import org.json.JSONArray
import org.json.JSONException

class fragmenuowner : Fragment() {
    private lateinit var binding: FragmenuownerBinding
    private lateinit var menuListContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmenuownerBinding.inflate(inflater, container, false)
        val view = binding.root
        menuListContainer = binding.menuListContainer

        binding.btnTambahMenu.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragtambahmenuowner())
                .addToBackStack(null)
                .commit()
        }

        ambilDataMenu()

        return view
    }

    private fun ambilDataMenu() {
        val url = "http://192.168.1.24/starling/show_menu.php"

        val stringRequest = StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val menuId = jsonObject.getString("id_menu")
                        val menuName = jsonObject.getString("nama_menu")
                        val menuPrice = jsonObject.getInt("harga")
                        val menuStock = jsonObject.getInt("stock")
                        val menuCategory = jsonObject.getString("nama_kategori")

                        tambahkanMenuKeView(menuId, menuName, menuPrice, menuStock, menuCategory)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun tambahkanMenuKeView(menuId: String, menuName: String, menuPrice: Int, menuStock: Int, menuCategory: String) {
        val menuView = LayoutInflater.from(context).inflate(R.layout.list_menu, menuListContainer, false)
        val menuIdTextView = menuView.findViewById<TextView>(R.id.txidmenu)
        val menuNameTextView = menuView.findViewById<TextView>(R.id.txnamamenu)
        val menuPriceTextView = menuView.findViewById<TextView>(R.id.txhargamenu)
        val menuStockTextView = menuView.findViewById<TextView>(R.id.txstokmenu)
        val menuCategoryTextView = menuView.findViewById<TextView>(R.id.txKategori)

        menuIdTextView.text = menuId
        menuNameTextView.text = menuName
        menuPriceTextView.text = "Harga: $menuPrice"
        menuStockTextView.text = "Stok: $menuStock"
        menuCategoryTextView.text = "Kategori: $menuCategory"

        menuListContainer.addView(menuView)
    }
}
