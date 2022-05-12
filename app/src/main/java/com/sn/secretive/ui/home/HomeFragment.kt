package com.sn.secretive.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sn.secretive.databinding.FragmentHomeBinding
import com.sn.secretive.extensions.click
import com.sn.secretive.extensions.gone
import com.sn.secretive.extensions.visible
import com.sn.secretive.ui.password.PasswordActivity
import com.sn.secretive.adapter.PasswordAdapter
import com.sn.secretive.ui.password.PasswordFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navigator: NavController
    private val vm: HomeViewModel by viewModels()
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val passwordAdapter by lazy {
        PasswordAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = findNavController()
        initRecyclerview()

        binding.floatActionButton.click {
            val intent = Intent(requireContext(), PasswordActivity::class.java)
            intent.putExtra("flow", PasswordFlow.ADD_PASSWORD.flow)
            startActivity(intent)
        }

        vm.passwordLiveData.observe(viewLifecycleOwner) { passwords ->
            if (passwords.isEmpty())
                binding.tvMsg.visible()
            else {
                passwordAdapter.items = passwords
                binding.tvMsg.gone()
            }
        }

        vm.deletePassLiveData.observe(viewLifecycleOwner) { position ->
            passwordAdapter.notifyItemRemoved(position)
        }

    }

    private fun initRecyclerview() {
        binding.rcvPass.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rcvPass.adapter = passwordAdapter
        passwordAdapter.onClick = { passwordItem ->
            val intent = Intent(requireContext(), PasswordActivity::class.java)
            intent.putExtra("flow", PasswordFlow.DETAIL_PASSWORD.flow)
            intent.putExtra("passwordItem", passwordItem)
            startActivity(intent)
        }

        passwordAdapter.onLongClick = { passwordItem, position ->
            vm.deletePassword(passwordItem, position)
        }
    }


}