package com.sn.secretive.ui.password.add

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sn.secretive.adapter.IconsAdapter
import com.sn.secretive.data.model.PasswordItemModel
import com.sn.secretive.databinding.FragmentAddPasswordBinding
import com.sn.secretive.extensions.click
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddPasswordFragment() : Fragment() {

    private val vm: AddPasswordViewModel by viewModels()
    private val binding: FragmentAddPasswordBinding by lazy {
        FragmentAddPasswordBinding.inflate(layoutInflater)
    }

    private val iconsAdapter by lazy {
        IconsAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvIcons.adapter = iconsAdapter
        binding.vm = vm
        initObserve()

        binding.etTitle.doOnTextChanged { title, _, _, _ ->
            vm.onInfoChange(title.toString(), binding.etPassword.text.toString())
        }

        binding.etPassword.doOnTextChanged { password, _, _, _ ->
            vm.onInfoChange(binding.etTitle.text.toString(), password.toString())
        }

        iconsAdapter.onClick = { iconName ->
            vm.iconName = iconName
            vm.onInfoChange(binding.etTitle.text.toString(), binding.etPassword.text.toString())
        }

        binding.btnSave.click {
            val password = PasswordItemModel(
                null,
                binding.etTitle.text.toString(),
                binding.etPassword.text.toString(),
                binding.etNote.text.toString(),
                vm.iconName!!
            )
            vm.insert(password)
        }

    }

    private fun initObserve() {
        vm.insertLiveData.observe(viewLifecycleOwner) { item ->
            val action =
                AddPasswordFragmentDirections.actionAddToSuccess(
                    item
                )
            findNavController().navigate(action)
            iconsAdapter.resetIcons()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    iconsAdapter.resetIcons()
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }
}