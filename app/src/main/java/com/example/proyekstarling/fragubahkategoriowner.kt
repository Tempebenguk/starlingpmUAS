package com.example.proyekstarling

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyekstarling.databinding.FrageditkategoriownerBinding
import org.json.JSONObject
import java.util.HashMap


class fragubahkategoriowner : Fragment() {
    private lateinit var binding: FrageditkategoriownerBinding

    val urlRoot = "http://localhost"
    val url3 = "$urlRoot/starling/cud_kategori.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrageditkategoriownerBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnubahktg.setOnClickListener {
            queryInsertUpdateDelete("update")
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
}