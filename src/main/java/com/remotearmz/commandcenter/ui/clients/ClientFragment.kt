package com.remotearmz.commandcenter.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.databinding.FragmentClientsBinding
import com.remotearmz.commandcenter.ui.clients.adapter.ClientAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : Fragment() {
    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientViewModel by viewModels()
    private val adapter = ClientAdapter(
        onEditClick = { client ->
            viewModel.selectClient(client)
            findNavController().navigate(R.id.action_clientsFragment_to_clientDetailFragment)
        },
        onDeleteClick = { client ->
            viewModel.deleteClient(client)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeClients()
        setupFab()
    }

    private fun setupRecyclerView() {
        binding.clientsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ClientFragment.adapter
        }
    }

    private fun observeClients() {
        viewModel.clients.observe(viewLifecycleOwner) { clients ->
            adapter.submitList(clients)
        }
    }

    private fun setupFab() {
        binding.addClientButton.setOnClickListener {
            viewModel.clearSelectedClient()
            findNavController().navigate(R.id.action_clientsFragment_to_clientDetailFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
