package com.example.proyekstarling

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragaboutownerqrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder

class fragaboutownerqr : Fragment(), View.OnClickListener {
    private lateinit var binding: FragaboutownerqrBinding
    lateinit var intentIntegrator: IntentIntegrator
    lateinit var preference: SharedPreferences

    val pref_name = "setting"
    val field_font_size = "font_size"
    val field_text = "text"
    val def_font_size = 12
    val def_text = "isi text"

    val onSeek = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            binding.edText.setTextSize(progress.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragaboutownerqrBinding.inflate(inflater, container, false)
        val view = binding.root

        intentIntegrator = IntentIntegrator.forSupportFragment(this)
        binding.btnScanqr.setOnClickListener(this)
        binding.btnGenqr.setOnClickListener(this)

        preference = requireContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        binding.edText.setText(preference.getString(field_text, def_text))
        binding.edText.textSize = preference.getInt(field_font_size, def_font_size).toFloat()
        binding.btnSeek.progress = preference.getInt(field_font_size, def_font_size)
        binding.btnSeek.setOnSeekBarChangeListener(onSeek)
        binding.btnSim.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {

        preference = requireContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        val prefEdit = preference.edit()
        prefEdit.putString(field_text, binding.edText.text.toString())
        prefEdit.putInt(field_font_size, binding.btnSeek.progress)
        prefEdit.commit()
        Toast.makeText(requireContext(), "Perubahan Disimpan", Toast.LENGTH_SHORT).show()

        when (v?.id) {
            R.id.btnScanqr -> {
                intentIntegrator.setBeepEnabled(true).initiateScan() // ada suaranya sih klo di scan
            }
            R.id.btnGenqr -> {
                val barcodeEncoder = BarcodeEncoder()
                val bitMap = barcodeEncoder.encodeBitmap(
                    binding.edText.text.toString(),
                    BarcodeFormat.QR_CODE, 400, 400
                )
                binding.imageVQR.setImageBitmap(bitMap)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (intentResult != null) {
            if (intentResult.contents != null) {
                binding.edText.setText(intentResult.contents)
            } else {
                Toast.makeText(requireContext(), "Dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
