package com.example.proyekstarling

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyekstarling.databinding.ActivityWebserviceBinding
import com.permissionx.guolindev.PermissionX
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class webservice : AppCompatActivity(), View.OnClickListener {
    lateinit var b: ActivityWebserviceBinding
    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
    }

    lateinit var mediaHelper: MediaHelper
    lateinit var mhsAdapter: AdapterDataMahasiswa

    lateinit var prodiAdapter: ArrayAdapter<String>
    private val PERMISSION_REQUEST_CODE = 101

    var daftarMhs = mutableListOf<HashMap<String, String>>()
    var daftarProdi = mutableListOf<String>()

    val urlRoot = "http://192.168.1.24"
    val url = "$urlRoot/datakampus/show_data.php"
    val url2 = "$urlRoot/datakampus/get_data.php"
    val url3 = "$urlRoot/datakampus/cud_data.php"
    val url4 = "$urlRoot/datakampus/check_service.php"
    var imgStr = ""
    var pilihProdi = ""
    var fileUri = Uri.parse("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityWebserviceBinding.inflate(layoutInflater)
        setContentView(b.root)

        mhsAdapter = AdapterDataMahasiswa(daftarMhs, b, daftarProdi)
        mediaHelper = MediaHelper(this)
        b.listMhs.layoutManager = LinearLayoutManager(this)
        b.listMhs.adapter = mhsAdapter

        prodiAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            daftarProdi
        )
        b.spinProdi.adapter = prodiAdapter
        b.spinProdi.onItemSelectedListener = itemSelected

        b.imUpload.setOnClickListener(this)
        b.btnUpdate.setOnClickListener(this)
        b.btnInsert.setOnClickListener(this)
        b.btnDelete.setOnClickListener(this)
        b.btnFind.setOnClickListener(this)

    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri", "Batal")
        val builder = AlertDialog.Builder(this)
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
        val cameraPermission = android.Manifest.permission.CAMERA
        val internetPermission = android.Manifest.permission.INTERNET

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
        startActivityForResult(intent, mediaHelper.getRcCamera())
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, mediaHelper.getRcGallery())
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val cameraPermission = android.Manifest.permission.CAMERA
        val internetPermission = android.Manifest.permission.INTERNET

        PermissionX.init(this)
            .permissions(cameraPermission, internetPermission)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Izin kamera tidak diberikan", Toast.LENGTH_LONG).show()
                }
            }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imUpload -> {
                showImageSourceDialog()  // Memunculkan dialog pilihan
            }

            R.id.btnInsert -> {
                queryInsertUpdateDelete("insert")
            }

            R.id.btnUpdate -> {
                queryInsertUpdateDelete("update")
            }

            R.id.btnDelete -> {
                queryInsertUpdateDelete("delete")
            }

            R.id.btnFind -> {
                showDataMhs(b.edNamaMhs.text.toString().trim())
            }
//
            R.id.imUpload->{
                val permisi = mutableListOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.INTERNET)
                if (Build.VERSION.RELEASE.toInt()<13)
                    permisi.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                else permisi.add(Manifest.permission.READ_MEDIA_IMAGES)
                PermissionX.init(this)
                    .permissions(permisi)
                    .request{allGranted, grantedList, deniedList ->
                        if (allGranted){
                            fileUri = mediaHelper.getOutputMediaFileUri()
                            val intent =
                                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(
                                MediaStore.EXTRA_OUTPUT,fileUri)
                            startActivityForResult(intent,mediaHelper.getRcCamera())
                        }else{
                            Toast.makeText(this,"Denied : $deniedList",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }

        }
    }

    fun checkWebService(){
        val request = object : StringRequest(Method.GET,url4,
            Response.Listener {response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if (error.equals("Metode Post"))
                    Toast.makeText(this, error,
                        Toast.LENGTH_LONG).show()
                else Toast.makeText(this, error,
                    Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                Toast.makeText(this, "${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        ){}
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    override fun onStart() {
        super.onStart()
        showDataMhs("")
        getNamaProdi()
    }

    val itemSelected = object : AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihProdi = daftarProdi.get(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            b.spinProdi.setSelection(0)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                mediaHelper.getRcCamera() -> {
                    imgStr = mediaHelper.getBitmapToString(fileUri, b.imUpload)
                }
                mediaHelper.getRcGallery() -> {
                    fileUri = data?.data
                    if (fileUri != null) {
                        imgStr = mediaHelper.getBitmapToString(fileUri, b.imUpload)
                    }
                }
            }
        }
    }


    fun queryInsertUpdateDelete(mode : String) {
        val request = object : StringRequest(Method.POST, url3,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if (error.equals("BERHASIL")) {
                    when (mode) {
                        "insert" -> Toast.makeText(this, "Berhasil menambah data", Toast.LENGTH_SHORT).show()
                        "update" -> Toast.makeText(this, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                        "delete" -> Toast.makeText(this, "Berhasil menghapus data", Toast.LENGTH_SHORT).show()
                    }
                    showDataMhs("")
                } else {
                    when (mode) {
                        "insert" -> Toast.makeText(this, "Berhasil menambah data", Toast.LENGTH_SHORT).show()
                        "update" -> Toast.makeText(this, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                        "delete" -> Toast.makeText(this, "Berhasil menghapus data", Toast.LENGTH_SHORT).show()
                    }
                    showDataMhs("")

                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    this, "Tidak dapat terhubung ke server",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                val nmFile = "DC" + SimpleDateFormat("yyyyMMddHHmmss",
                    Locale.getDefault()).
                format(Date())+".jpg"
                when(mode){
                    "insert"-> {
                        hm.put("mode","insert")
                        hm.put("nim", b.edNim.text.toString())
                        hm.put("nama", b.edNamaMhs.text.toString())
                        hm.put("image", imgStr)
                        hm.put("file", nmFile)
                        hm.put("nama_prodi", pilihProdi)
                    }"update"-> {
                    hm.put("mode","update")
                    hm.put("nim", b.edNim.text.toString())
                    hm.put("nama", b.edNamaMhs.text.toString())
                    hm.put("image", imgStr)
                    hm.put("file", nmFile)
                    hm.put("nama_prodi", pilihProdi)
                }"delete"-> {
                    hm.put("mode","delete")
                    hm.put("nim", b.edNim.text.toString())
                }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    fun getNamaProdi(){
        val request = StringRequest(
            Request.Method.POST,url2,
            Response.Listener { response ->
                daftarProdi.clear()
                val jsonArray = JSONArray(response)
                for(x in 0 .. (jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarProdi.add(jsonObject.getString("nama_prodi"))
                }
                prodiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(this, "${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        )
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    fun showDataMhs(namaMhs: String) {
        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    daftarMhs.clear()
                    val jsonArray = JSONArray(response)
                    for (x in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(x)
                        val namaMahasiswa = jsonObject.getString("nama")

                        if (namaMahasiswa.contains(namaMhs, ignoreCase = true)) {
                            val hm = HashMap<String, String>()
                            hm["nim"] = jsonObject.getString("nim")
                            hm["nama"] = namaMahasiswa
                            hm["nama_prodi"] = jsonObject.getString("nama_prodi")
                            hm["url"] = jsonObject.getString("url")
                            daftarMhs.add(hm)
                        }
                    }
                    mhsAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    // Tangani kesalahan parsing JSON
                    Toast.makeText(this, "Respons tidak valid: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                // Tangani kesalahan jaringan
                Toast.makeText(this, "Kesalahan jaringan: ${error.message}",
                    Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "nama" to namaMhs
                )
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }
}