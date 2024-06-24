package com.example.proyekstarling

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragtambahmenuownerBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class fragtambahmenuowner : Fragment() {
    private lateinit var binding: FragtambahmenuownerBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private var selectedImageUri: Uri? = null
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragtambahmenuownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("menu")
        storage = FirebaseStorage.getInstance().getReference("menu_images")

        // Load categories from Firebase
        loadCategoriesFromFirebase()

        binding.btnFoto.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnTambahMenu.setOnClickListener {
            tambahMenu()
        }
        return view
    }

    private fun loadCategoriesFromFirebase() {
        val categoriesRef = FirebaseDatabase.getInstance().getReference("kategori")
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val categories = mutableListOf<String>()
                    for (categorySnapshot in snapshot.children) {
                        val categoryName = categorySnapshot.child("nama_kategori").getValue(String::class.java)
                        categoryName?.let {
                            categories.add(it)
                        }
                    }
                    // Update UI with categories
                    updateSpinner(categories)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(context, "Failed to load categories: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSpinner(categories: List<String>) {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = spinnerAdapter
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil Foto", "Pilih Dari Galeri")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                1 -> selectImageResultLauncher.launch("image/*")
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(context, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.proyekstarling.fileprovider",
                it
            )
            selectedImageUri = photoURI
            takePictureResultLauncher.launch(photoURI)
        }
    }

    private val selectImageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.tambahgambar.setImageURI(uri)
        }
    }

    private val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri?.let {
                binding.tambahgambar.setImageURI(it)
            }
        } else {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun tambahMenu() {
        val id = binding.tambahIdMenu.text.toString().trim()
        val nama = binding.tambahNamaMenu.text.toString().trim()
        val harga = binding.tambahHargaMenu.text.toString().trim()
        val stok = binding.tambahStokMenu.text.toString().trim()
        val selectedCategory = binding.spinnerKategori.selectedItem.toString()
        val kategori = when (selectedCategory) {
            "makanan" -> "kat1"
            "minuman" -> "kat2"
            "snack" -> "kat3"
            else -> ""
        }

        if (nama.isEmpty() || harga.isEmpty() || stok.isEmpty() || selectedImageUri == null) {
            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = UUID.randomUUID().toString()
        val imageRef = storage.child("$fileName.jpg")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        val menu = menu(
                            id = id,
                            nama_menu = nama,
                            harga = harga.toInt(),
                            stock = stok.toInt(),
                            gambar_menu = imageUrl.toString(),
                            id_kategori = kategori
                        )

                        database.child(id).setValue(menu).addOnCompleteListener {
                            Toast.makeText(context, "Data Menu telah ditambahkan", Toast.LENGTH_SHORT).show()
                            kosong()
                            requireActivity().supportFragmentManager.popBackStack()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Gagal menambahkan data Menu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun kosong() {
        binding.tambahIdMenu.text.clear()
        binding.tambahNamaMenu.text.clear()
        binding.tambahHargaMenu.text.clear()
        binding.tambahStokMenu.text.clear()
        binding.spinnerKategori.setSelection(0)
        binding.tambahgambar.setImageURI(null)
        selectedImageUri = null
    }
}
