package com.sn.secretive

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sn.biometric.Biometric
import com.sn.biometric.BiometricListener
import com.sn.secretive.LoginViewModel.Companion.DB_ERR
import com.sn.secretive.LoginViewModel.Companion.SUCCESS
import com.sn.secretive.LoginViewModel.Companion.WRONG_PIN
import com.sn.secretive.databinding.LoginFragmentBinding
import com.sn.secretive.extensions.click
import com.sn.secretive.extensions.gone
import com.sn.secretive.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var biometric: Biometric
    private lateinit var biometricListener: BiometricListener
    private val vm: LoginViewModel by viewModels()
    private val binding by lazy {
        LoginFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        biometric.subscribe(biometricListener)
    }

    override fun onPause() {
        super.onPause()
        biometric.unSubscribe()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
        initBiometric()
        hasFingerprintEnrolled()

        binding.btnContinue.click {
            when (vm.checkPIN(binding.etPin.text.toString())) {
                SUCCESS -> {
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                }
                WRONG_PIN -> {
                    //todo wrong pin
                }
                DB_ERR -> {
                    //todo db error
                }
            }

        }

        binding.ivFingerPrint.click {
            showBiometricDialog()
        }
    }

    private fun initBiometric() {
        biometric = Biometric(requireContext())
        biometricListener = object : BiometricListener {
            override fun onFingerprintAuthenticationSuccess() {
            }

            override fun onFingerprintAuthenticationFailure(errorMessage: String, errorCode: Int) {
            }
        }
        biometric.subscribe(biometricListener)
    }

    private fun showBiometricDialog() {
        biometric.showDialog(
            activity = requireActivity(),
            strings = Biometric.DialogStrings(
                title = resources.getString(R.string.login_finger_print),
                subTitle = "",
                description = "",
            )
        )
    }

    private fun hasFingerprintEnrolled() {
        if (biometric.hasFingerprintEnrolled())
            binding.ivFingerPrint.visible()
        else
            binding.ivFingerPrint.gone()
    }

    private fun initObserve() {
        vm.session.observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled = true
        }
    }

}