package com.remotearmz.commandcenter.ui.leads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.data.model.Lead
import com.remotearmz.commandcenter.data.model.LeadSource
import com.remotearmz.commandcenter.data.model.LeadStatus
import com.remotearmz.commandcenter.databinding.FragmentLeadDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadDetailFragment : Fragment() {
    private var _binding: FragmentLeadDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeadViewModel by viewModels()
    private val args: LeadDetailFragmentArgs by navArgs()
    private var currentLead: Lead? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeadDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupChipGroups()
        setupSaveButton()
        setupOutreach()

        if (args.leadId != null) {
            viewModel.selectLead(args.leadId)
            observeLead()
        } else {
            setupNewLead()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupChipGroups() {
        // Setup source chips
        binding.sourceChipGroup.apply {
            check(R.id.emailChip)
            
            setOnCheckedChangeListener { group, checkedId ->
                currentLead?.source = when (checkedId) {
                    R.id.emailChip -> LeadSource.EMAIL
                    R.id.phoneChip -> LeadSource.PHONE
                    R.id.linkedinChip -> LeadSource.LINKEDIN
                    R.id.referralChip -> LeadSource.REFERRAL
                    else -> LeadSource.EMAIL
                }
            }
        }

        // Setup status chips
        binding.statusChipGroup.apply {
            check(R.id.newChip)
            
            setOnCheckedChangeListener { group, checkedId ->
                currentLead?.status = when (checkedId) {
                    R.id.newChip -> LeadStatus.NEW
                    R.id.contactedChip -> LeadStatus.CONTACTED
                    R.id.qualifiedChip -> LeadStatus.QUALIFIED
                    R.id.proposalChip -> LeadStatus.PROPOSAL_SENT
                    R.id.negotiatingChip -> LeadStatus.NEGOTIATING
                    R.id.wonChip -> LeadStatus.WON
                    R.id.lostChip -> LeadStatus.LOST
                    else -> LeadStatus.NEW
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val lead = Lead(
                id = currentLead?.id ?: System.currentTimeMillis().toString(),
                name = binding.nameInput.text.toString(),
                email = binding.emailInput.text.toString(),
                phone = binding.phoneInput.text.toString(),
                company = binding.companyInput.text.toString(),
                designation = binding.designationInput.text.toString(),
                source = currentLead?.source ?: LeadSource.EMAIL,
                status = currentLead?.status ?: LeadStatus.NEW,
                notes = binding.notesInput.text.toString()
            )

            if (currentLead == null) {
                viewModel.addLead(lead)
            } else {
                viewModel.updateLead(lead)
            }

            findNavController().navigateUp()
        }
    }

    private fun setupOutreach() {
        // TODO: Implement outreach list and add outreach button
        binding.addOutreachButton.setOnClickListener {
            // TODO: Navigate to add outreach screen
        }
    }

    private fun observeLead() {
        viewModel.selectedLead.observe(viewLifecycleOwner) { lead ->
            currentLead = lead
            lead?.let { updateUI(it) }
        }
    }

    private fun updateUI(lead: Lead) {
        binding.apply {
            nameInput.setText(lead.name)
            emailInput.setText(lead.email)
            phoneInput.setText(lead.phone)
            companyInput.setText(lead.company)
            designationInput.setText(lead.designation)
            notesInput.setText(lead.notes)

            // Update source chip
            when (lead.source) {
                LeadSource.EMAIL -> binding.sourceChipGroup.check(R.id.emailChip)
                LeadSource.PHONE -> binding.sourceChipGroup.check(R.id.phoneChip)
                LeadSource.LINKEDIN -> binding.sourceChipGroup.check(R.id.linkedinChip)
                LeadSource.REFERRAL -> binding.sourceChipGroup.check(R.id.referralChip)
            }

            // Update status chip
            when (lead.status) {
                LeadStatus.NEW -> binding.statusChipGroup.check(R.id.newChip)
                LeadStatus.CONTACTED -> binding.statusChipGroup.check(R.id.contactedChip)
                LeadStatus.QUALIFIED -> binding.statusChipGroup.check(R.id.qualifiedChip)
                LeadStatus.PROPOSAL_SENT -> binding.statusChipGroup.check(R.id.proposalChip)
                LeadStatus.NEGOTIATING -> binding.statusChipGroup.check(R.id.negotiatingChip)
                LeadStatus.WON -> binding.statusChipGroup.check(R.id.wonChip)
                LeadStatus.LOST -> binding.statusChipGroup.check(R.id.lostChip)
            }
        }
    }

    private fun setupNewLead() {
        currentLead = Lead(
            id = System.currentTimeMillis().toString(),
            name = "",
            email = "",
            phone = "",
            company = "",
            designation = "",
            source = LeadSource.EMAIL,
            status = LeadStatus.NEW,
            notes = ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
