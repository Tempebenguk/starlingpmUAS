package com.example.proyekstarling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.proyekstarling.databinding.FragaboutownergpsBinding

class fragaboutownergps1 : Fragment(), View.OnClickListener {
    companion object {
        private const val REQUEST_MAPS = 3
    }

    private var _binding: FragaboutownergpsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragaboutownergpsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menangkap latitude dan longitude dari Bundle atau Arguments
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        // Set nilai ke EditText
        binding.edLat.setText(latitude.toString())
        binding.edLong.setText(longitude.toString())

        // Set OnClickListener
        binding.btnMaps.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("latitude", binding.edLat.text.toString())
        outState.putString("longitude", binding.edLong.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            binding.edLat.setText(savedInstanceState.getString("latitude", ""))
            binding.edLong.setText(savedInstanceState.getString("longitude", ""))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMaps -> {
                val fragaboutownergps2 = fragaboutownergps2()
                fragaboutownergps2.arguments = Bundle().apply {
                    putDouble("latitude", binding.edLat.text.toString().toDouble())
                    putDouble("longitude", binding.edLong.text.toString().toDouble())
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragaboutownergps2)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
        }
    }

    private fun clearFields() {
        binding.edLong.setText("") // Kosongkan edLong
        binding.edLat.setText("") // Kosongkan edLat
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
