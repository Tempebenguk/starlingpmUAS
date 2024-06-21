package com.example.proyekstarling

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragaboutownerqrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder

class fragaboutownerqr : Fragment(), View.OnClickListener {
    private lateinit var binding: FragaboutownerqrBinding
    lateinit var intentIntegrator: IntentIntegrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragaboutownerqrBinding.inflate(inflater, container, false)
        val view = binding.root

        intentIntegrator = IntentIntegrator.forSupportFragment(this)
        binding.btnScanqr.setOnClickListener(this)
        binding.btnGenqr.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScanqr -> {
                intentIntegrator.setBeepEnabled(true).initiateScan() // ada suaranya sih klo di scan
            }
            R.id.btnGenqr -> {
                val barcodeEncoder = BarcodeEncoder()
                val bitMap = barcodeEncoder.encodeBitmap(
                    binding.edTCode.text.toString(),
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
                binding.edTCode.setText(intentResult.contents)
            } else {
                Toast.makeText(requireContext(), "Dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
