package com.example.proyekstarling

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.proyekstarling.databinding.FragaboutownervideoBinding

class fragaboutownervideo : Fragment() {
    lateinit var binding: FragaboutownervideoBinding
    val videoList: IntArray = intArrayOf(R.raw.videoowner)

    var videoNow = 0
    lateinit var mediaController: MediaController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragaboutownervideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    var nextVid = View.OnClickListener { v: View ->
        if (videoNow < (videoList.size - 1)) videoNow++
        else videoNow = 0
        videoSet(videoNow)
    }

    var prevVid = View.OnClickListener { v: View ->
        if (videoNow > 0) videoNow--
        else videoNow = videoList.size - 1
        videoSet(videoNow)
    }

    fun videoSet(pos: Int) {
        binding.vidVMitra.setVideoURI(Uri.parse("android.resource://" + requireContext().packageName + "/" + videoList[pos]))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaController = MediaController(activity)

        mediaController.setPrevNextListeners(nextVid, prevVid)
        mediaController.setAnchorView(binding.vidVMitra)
        binding.vidVMitra.setMediaController(mediaController)
        videoSet(videoNow)
    }
}