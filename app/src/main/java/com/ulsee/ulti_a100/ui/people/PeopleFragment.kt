package com.ulsee.ulti_a100.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ulsee.ulti_a100.databinding.FragmentDeviceListBinding

class PeopleFragment : Fragment() {
    lateinit var binding: FragmentDeviceListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceListBinding.inflate(layoutInflater)


        return super.onCreateView(inflater, container, savedInstanceState)
    }


}