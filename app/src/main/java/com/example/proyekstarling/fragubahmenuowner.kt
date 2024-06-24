package com.example.proyekstarling

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.proyekstarling.databinding.FrageditmenuownerBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class fragubahmenuowner : Fragment() {
    private lateinit var binding: FrageditmenuownerBinding
    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null
    private var menuId: String? = null
    private lateinit var currentPhotoPath: String

    private val selectImageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imageView14.setImageURI(uri)
        }
    }

    private val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri?.let {
                binding.imageView14.setImageURI(it)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrageditmenuownerBinding.inflate(inflater, container, false)
        val view = binding.root
        database = FirebaseDatabase.getInstance().getReference("menu")

        menuId = arguments?.getString("menuId")
        if (menuId != null) {
            loadMenuData(menuId!!)
        }

        setupSpinner()

        binding.btnUbahMenu.setOnClickListener {
            ubahMenu()
        }

        binding.btnFoto.setOnClickListener {
            showImagePickerDialog()
        }

        return view
    }

    private fun loadMenuData(menuId: String) {
        database.child(menuId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val menu = dataSnapshot.getValue(menu::class.java)
                if (menu != null) {
                    binding.ubahIdMenu.setText(menuId)
                    binding.ubahNamaMenu.setText(menu.nama_menu)
                    binding.ubahHargaMenu.setText(menu.harga.toString())
                    binding.ubahStokMenu.setText(menu.stock.toString())
                    Picasso.get().load(menu.gambar_menu).into(binding.imageView14)

                    when (menu.id_kategori) {
                        "kat1" -> binding.spinnerKategori.setSelection(0)
                        "kat2" -> binding.spinnerKategori.setSelection(1)
                        "kat3" -> binding.spinnerKategori.setSelection(2)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data menu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner() {
        val kategoriOptions = arrayOf("Makanan", "Minuman", "Snack")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter
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

    private fun ubahMenu() {
        val nama = binding.ubahNamaMenu.text.toString().trim()
        val harga = binding.ubahHargaMenu.text.toString().trim()
        val stok = binding.ubahStokMenu.text.toString().toIntOrNull()
        val kategori = when (binding.spinnerKategori.selectedItemPosition) {
            0 -> "kat1"
            1 -> "kat2"
            2 -> "kat3"
            else -> ""
        }

        if (menuId != null) {
            database.child(menuId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val menu = dataSnapshot.getValue(menu::class.java)
                    if (menu != null) {
                        val updatedNama = if (nama.isNotEmpty()) nama else menu.nama_menu
                        val updatedHarga = if (harga.isNotEmpty()) harga.toInt() else menu.harga
                        val updatedStok = stok ?: menu.stock
                        val updatedKategori = if (kategori.isNotEmpty()) kategori else menu.id_kategori

                        if (selectedImageUri != null) {
                            val storageReference = FirebaseStorage.getInstance().reference
                            val imageRef = storageReference.child("menu_images/${menuId}.jpg")
                            imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val updatedGambar = uri.toString()
                                    saveUpdatedMenu(menuId!!, updatedNama, updatedHarga, updatedStok, updatedGambar, updatedKategori)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val updatedGambar = menu.gambar_menu
                            saveUpdatedMenu(menuId!!, updatedNama, updatedHarga, updatedStok, updatedGambar, updatedKategori)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data menu", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "ID menu tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUpdatedMenu(menuId: String, nama: String, harga: Int, stok: Int, gambar: String, kategori: String) {
        val updatedMenu = menu(
            nama_menu = nama,
            harga = harga,
            stock = stok,
            gambar_menu = gambar,
            id_kategori = kategori
        )

        database.child(menuId).setValue(updatedMenu).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Menu diperbarui", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Gagal memperbarui menu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
