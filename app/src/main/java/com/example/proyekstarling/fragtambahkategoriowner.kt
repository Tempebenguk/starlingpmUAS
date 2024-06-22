package com.example.proyekstarling

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.proyekstarling.admin
import com.example.proyekstarling.databinding.FragtambahkategoriownerBinding
import com.example.proyekstarling.databinding.FragtambahmenuownerBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale

class fragtambahkategoriowner : Fragment() {
    private lateinit var binding: FragtambahkategoriownerBinding

    val urlRoot = "http://192.168.1.24"
    val url3 = "$urlRoot/starling/cud_kategori.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragtambahkategoriownerBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btntbhktg.setOnClickListener {
            queryInsertUpdateDelete("insert")
        }
        return view
    }

    fun queryInsertUpdateDelete(mode: String) {
        val request = object : StringRequest(
            Request.Method.POST, url3,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if (error.equals("BERHASIL")) {
                    when (mode) {
                        "insert" -> Toast.makeText(
                            requireContext(),
                            "Berhasil menambah data",
                            Toast.LENGTH_SHORT
                        ).show()
                        "update" -> Toast.makeText(
                            requireContext(),
                            "Berhasil memperbarui data",
                            Toast.LENGTH_SHORT
                        ).show()
                        "delete" -> Toast.makeText(
                            requireContext(),
                            "Berhasil menghapus data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    kosong()
                } else {
                    when (mode) {
                        "insert" -> Toast.makeText(
                            requireContext(),
                            "Gagal menambah data",
                            Toast.LENGTH_SHORT
                        ).show()
                        "update" -> Toast.makeText(
                            requireContext(),
                            "Gagal memperbarui data",
                            Toast.LENGTH_SHORT
                        ).show()
                        "delete" -> Toast.makeText(
                            requireContext(),
                            "Gagal menghapus data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    requireContext(),
                    "Tidak dapat terhubung ke server",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                when (mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("id_kategori", binding.tambahIdKtg.text.toString())
                        hm.put("nama_kategori", binding.tambahNamaKtg.text.toString())
                    }
                    "update" -> {
                        hm.put("mode", "update")
                        hm.put("id_kategori", binding.tambahIdKtg.text.toString())
                        hm.put("nama_kategori", binding.tambahNamaKtg.text.toString())
                    }
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("id_kategori", binding.tambahIdKtg.text.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(requireContext())
        q.add(request)
    }

    private fun kosong() {
        binding.tambahIdKtg.text.clear()
        binding.tambahNamaKtg.text.clear()
    }
}