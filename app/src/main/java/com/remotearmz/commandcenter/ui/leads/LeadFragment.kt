package com.remotearmz.commandcenter.ui.leads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.databinding.FragmentLeadsBinding
import com.remotearmz.commandcenter.ui.leads.adapter.LeadAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadFragment : Fragment() {
    private var _binding: FragmentLeadsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeadViewModel by viewModels()
    private val adapter = LeadAdapter(
        onEditClick = { lead ->
            viewModel.selectLead(lead)
            findNavController().navigate(R.id.action_leadsFragment_to_leadDetailFragment)
        },
        onDeleteClick = { lead ->
            viewModel.deleteLead(lead)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeLeads()
        setupFab()
        setupStatusFilter()
    }

    private fun setupRecyclerView() {
        binding.leadsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LeadFragment.adapter
        }
    }

    private fun observeLeads() {
        viewModel.leads.observe(viewLifecycleOwner) { leads ->
            adapter.submitList(leads)
        }
    }

    private fun setupFab() {
        binding.addLeadButton.setOnClickListener {
            viewModel.clearSelectedLead()
            findNavController().navigate(R.id.action_leadsFragment_to_leadDetailFragment)
        }
    }

    private fun setupStatusFilter() {
        binding.statusFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.allChip -> viewModel.filterLeadsByStatus(null)
                R.id.newChip -> viewModel.filterLeadsByStatus(LeadStatus.NEW)
                R.id.contactedChip -> viewModel.filterLeadsByStatus(LeadStatus.CONTACTED)
                R.id.qualifiedChip -> viewModel.filterLeadsByStatus(LeadStatus.QUALIFIED)
                R.id.proposalChip -> viewModel.filterLeadsByStatus(LeadStatus.PROPOSAL_SENT)
                R.id.negotiatingChip -> viewModel.filterLeadsByStatus(LeadStatus.NEGOTIATING)
                R.id.wonChip -> viewModel.filterLeadsByStatus(LeadStatus.WON)
                R.id.lostChip -> viewModel.filterLeadsByStatus(LeadStatus.LOST)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
