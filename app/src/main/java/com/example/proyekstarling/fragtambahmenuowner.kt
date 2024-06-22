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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyekstarling.databinding.FragtambahmenuownerBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class fragtambahmenuowner : Fragment(), View.OnClickListener {

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
        private const val REQUEST_CODE_STORAGE_PERMISSION = 101
    }

    private lateinit var binding: FragtambahmenuownerBinding
    private var selectedImageUri: Uri? = null

    val urlRoot = "http://localhost"
    val url = "$urlRoot/starling/show_menu.php"
    val url2 = "$urlRoot/starling/get_kategori.php"
    val url3 = "$urlRoot/starling/cud_menu.php"
    val url4 = "$urlRoot/starling/check_service.php"

    private val daftarKategori: ArrayList<String> = ArrayList()
    lateinit var kategoriAdapter: ArrayAdapter<String>
    var fileUri = Uri.parse("")
    lateinit var mediaHelper: MediaHelper
    var imgStr = ""
    var pilihKategori = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragtambahmenuownerBinding.inflate(inflater, container, false)
        val view = binding.root

        kategoriAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daftarKategori)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinKategori.adapter = kategoriAdapter

        binding.btnTambahMenu.setOnClickListener {
            queryInsertUpdateDelete("insert")
        }

        binding.spinKategori.onItemSelectedListener = itemSelected

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tambahgambar -> {
                showImageSourceDialog()  // Memunculkan dialog pilihan
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri", "Batal")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> checkCameraPermissionAndOpenCamera()
                1 -> openGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        val cameraPermission = Manifest.permission.CAMERA
        val internetPermission = Manifest.permission.INTERNET

        if (isPermissionGranted(cameraPermission) && isPermissionGranted(internetPermission)) {
            // Izin kamera sudah diberikan, buka kamera
            openCamera()
        } else {
            // Meminta izin kamera jika belum diberikan
            requestCameraPermission()
        }
    }

    private fun openCamera() {
        fileUri = mediaHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        val internetPermission = Manifest.permission.INTERNET

        if (Build.VERSION.SDK_INT < 23) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Izin kamera tidak diberikan",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    imgStr = mediaHelper.getBitmapToString(fileUri, binding.tambahgambar)
                }
                REQUEST_GALLERY -> {
                    fileUri = data?.data
                    if (fileUri != null) {
                        imgStr = mediaHelper.getBitmapToString(fileUri, binding.tambahgambar)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getNamaKategori()
    }

    val itemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihKategori = daftarKategori.get(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            binding.spinKategori.setSelection(0)
        }
    }

    fun getNamaKategori() {
        val request = object : StringRequest(
            Request.Method.POST, url2,
            Response.Listener { response ->
                daftarKategori.clear()
                val jsonArray = JSONArray(response)
                for (x in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarKategori.add(jsonObject.getString("nama_kategori"))
                }
                kategoriAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return HashMap()
            }
        }
        val q = Volley.newRequestQueue(requireContext())
        q.add(request)
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
                val nmFile =
                    "DC" + SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date()) + ".jpg"
                when (mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("id_prodi", binding.tambahIdMenu.text.toString())
                        hm.put("nama_menu", binding.tambahNamaMenu.text.toString())
                        hm.put("harga", binding.tambahHargaMenu.text.toString())
                        hm.put("stok", binding.tambahStokMenu.text.toString())
                        hm.put("image", imgStr)
                        hm.put("file", nmFile)
                        hm.put("nama_prodi", pilihKategori)
                    }
                    "update" -> {
                        hm.put("mode", "update")
                        hm.put("id_prodi", binding.tambahIdMenu.text.toString())
                        hm.put("nama_menu", binding.tambahNamaMenu.text.toString())
                        hm.put("harga", binding.tambahHargaMenu.text.toString())
                        hm.put("stok", binding.tambahStokMenu.text.toString())
                        hm.put("image", imgStr)
                        hm.put("file", nmFile)
                        hm.put("nama_prodi", pilihKategori)
                    }
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("id_menu", binding.tambahIdMenu.text.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(requireContext())
        q.add(request)
    }
}
