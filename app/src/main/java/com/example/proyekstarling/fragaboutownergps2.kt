package com.example.proyekstarling

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.Fragaboutownergps2Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import mumayank.com.airlocationlibrary.AirLocation

class fragaboutownergps2 : Fragment(), OnMapReadyCallback, View.OnClickListener {

    private var _binding: Fragaboutownergps2Binding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment
    lateinit var airLoc: AirLocation
    private var liveUpdate = true
    private lateinit var gMap: GoogleMap
    private lateinit var ll: LatLng
    private var customMarkers: MutableList<Marker> = mutableListOf()
    private var originalMarker: Marker? = null
    private var houseMarker: Marker? = null

    private val houseLocation = LatLng(-7.5601742, 112.1657297)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Fragaboutownergps2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.fab.setOnClickListener(this)
        binding.chip.setOnClickListener(this)
        binding.btnKirim.setOnClickListener(this)
        binding.fabHome.setOnClickListener(this) // Set click listener for fabHome

        binding.chip.isChecked = true

        // Request location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.setOnMapLongClickListener { latLng ->
            showLocationNameDialog(latLng)
        }

        gMap.setOnMarkerClickListener { marker ->
            if (marker != originalMarker) {
                showMarkerOptionsDialog(marker)
            }
            true
        }

        gMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}

            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                Toast.makeText(activity, "Marker moved to: ${marker.position}", Toast.LENGTH_SHORT).show()
            }
        })

        // Add marker for house location
        houseMarker = gMap.addMarker(MarkerOptions().position(houseLocation).title("Rumah Vira"))

        // Initialize AirLocation
        initializeAirLocation()

        // Get latitude and longitude from arguments
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        // Set marker to passed location
        ll = LatLng(latitude, longitude)
        if (originalMarker != null) originalMarker!!.remove()
        originalMarker = gMap.addMarker(MarkerOptions().position(ll).title("Posisi saya").draggable(false))

        gMap.moveCamera(CameraUpdateFactory.newLatLng(ll))
        binding.editText.setText("Posisi saya : LAT=${latitude}, LNG=${longitude}")
    }

    private fun initializeAirLocation() {
        airLoc = AirLocation(requireActivity(), object : AirLocation.Callback {
            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                Toast.makeText(activity, "Gagal mendapatkan posisi saat ini", Toast.LENGTH_SHORT).show()
                binding.editText.setText("Gagal mendapatkan posisi saat ini")
            }

            override fun onSuccess(locations: ArrayList<Location>) {
                if (liveUpdate) {
                    val location = locations.last()
                    ll = LatLng(location.latitude, location.longitude)
                    if (originalMarker != null) originalMarker!!.remove()
                    originalMarker = gMap.addMarker(MarkerOptions().position(ll).title("Posisi saya").draggable(false))

                    gMap.moveCamera(CameraUpdateFactory.newLatLng(ll))
                    binding.editText.setText("Posisi saya : LAT=${location.latitude}, LNG=${location.longitude}")
                }
            }
        })

        // Start AirLocation only if permissions are granted
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            airLoc.start()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                binding.chip.isChecked = false
                liveUpdate = binding.chip.isChecked
                if (originalMarker != null) {
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originalMarker!!.position, 16.0f))
                    binding.editText.setText("Posisi saya : LAT=${originalMarker!!.position.latitude}, LNG=${originalMarker!!.position.longitude}")
                }
            }
            R.id.chip -> {
                liveUpdate = binding.chip.isChecked
                if (liveUpdate) {
                    initializeAirLocation()
                }
            }
            R.id.btnKirim -> {
                val intent = Intent()
                intent.putExtra("latitude", ll.latitude)
                intent.putExtra("longitude", ll.longitude)
                activity?.setResult(AppCompatActivity.RESULT_OK, intent)
                activity?.finish()
            }
            R.id.fabHome -> {
                // Zoom to the house location
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(houseLocation, 16.0f))
                binding.editText.setText("Lokasi rumah : LAT=${houseLocation.latitude}, LNG=${houseLocation.longitude}")
            }
        }
    }

    private fun showLocationNameDialog(latLng: LatLng) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        builder.setTitle("Nama Lokasi")
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val locationName = input.text.toString()
            addCustomMarker(latLng, locationName)
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addCustomMarker(latLng: LatLng, locationName: String) {
        val newMarker = gMap.addMarker(MarkerOptions().position(latLng).title(locationName).draggable(true))
        if (newMarker != null) {
            customMarkers.add(newMarker)
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        Toast.makeText(activity, "Lokasi $locationName ditambahkan", Toast.LENGTH_SHORT).show()
    }

    private fun showMarkerOptionsDialog(marker: Marker) {
        val options = arrayOf("Ganti Nama", "Hapus")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(marker.title)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> showRenameMarkerDialog(marker)
                1 -> {
                    marker.remove()
                    customMarkers.remove(marker)
                    Toast.makeText(activity, "Marker dihapus", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.show()
    }

    private fun showRenameMarkerDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.setText(marker.title)
        builder.setTitle("Ganti Nama")
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newName = input.text.toString()
            marker.title = newName
            marker.showInfoWindow()
            Toast.makeText(activity, "Nama marker diganti menjadi $newName", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLoc.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLoc.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, start location updates
                airLoc.start()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(requireContext(), "Permission denied to access location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
