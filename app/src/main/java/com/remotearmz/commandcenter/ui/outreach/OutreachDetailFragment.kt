package com.remotearmz.commandcenter.ui.outreach

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
import com.remotearmz.commandcenter.data.model.Outreach
import com.remotearmz.commandcenter.data.model.OutreachStatus
import com.remotearmz.commandcenter.data.model.OutreachType
import com.remotearmz.commandcenter.databinding.FragmentOutreachDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class OutreachDetailFragment : Fragment() {
    private var _binding: FragmentOutreachDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OutreachViewModel by viewModels()
    private val args: OutreachDetailFragmentArgs by navArgs()
    private var currentOutreach: Outreach? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutreachDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupChipGroups()
        setupDatePicker()
        setupSaveButton()

        if (args.outreachId != null) {
            viewModel.selectOutreach(args.outreachId)
            observeOutreach()
        } else {
            setupNewOutreach()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupChipGroups() {
        // Setup type chips
        binding.typeChipGroup.apply {
            check(R.id.emailChip)
            
            setOnCheckedChangeListener { group, checkedId ->
                currentOutreach?.outreachType = when (checkedId) {
                    R.id.emailChip -> OutreachType.EMAIL
                    R.id.phoneChip -> OutreachType.PHONE_CALL
                    R.id.linkedinChip -> OutreachType.LINKEDIN_MESSAGE
                    R.id.socialChip -> OutreachType.SOCIAL_MEDIA_POST
                    R.id.meetingChip -> OutreachType.MEETING
                    R.id.otherChip -> OutreachType.OTHER
                    else -> OutreachType.EMAIL
                }
            }
        }

        // Setup status chips
        binding.statusChipGroup.apply {
            check(R.id.pendingChip)
            
            setOnCheckedChangeListener { group, checkedId ->
                currentOutreach?.status = when (checkedId) {
                    R.id.pendingChip -> OutreachStatus.PENDING
                    R.id.completedChip -> OutreachStatus.COMPLETED
                    R.id.scheduledChip -> OutreachStatus.SCHEDULED
                    R.id.cancelledChip -> OutreachStatus.CANCELLED
                    else -> OutreachStatus.PENDING
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.selectDateButton.setOnClickListener {
            val datePicker = DatePickerFragment { date ->
                binding.dateInput.setText(date)
                currentOutreach?.outreachDate = date.toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
            }
            datePicker.show(childFragmentManager, "datePicker")
        }

        // Set initial date
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        binding.dateInput.setText(LocalDateTime.now().format(formatter))
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val outreach = Outreach(
                id = currentOutreach?.id ?: System.currentTimeMillis().toString(),
                clientId = args.clientId,
                leadId = args.leadId,
                outreachType = currentOutreach?.outreachType ?: OutreachType.EMAIL,
                outreachDate = currentOutreach?.outreachDate ?: System.currentTimeMillis(),
                status = currentOutreach?.status ?: OutreachStatus.PENDING,
                notes = binding.notesInput.text.toString()
            )

            if (currentOutreach == null) {
                viewModel.addOutreach(outreach)
            } else {
                viewModel.updateOutreach(outreach)
            }

            findNavController().navigateUp()
        }
    }

    private fun observeOutreach() {
        viewModel.selectedOutreach.observe(viewLifecycleOwner) { outreach ->
            currentOutreach = outreach
            outreach?.let { updateUI(it) }
        }
    }

    private fun updateUI(outreach: Outreach) {
        binding.apply {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            dateInput.setText(LocalDateTime.ofEpochSecond(
                outreach.outreachDate,
                0,
                ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
            ).format(formatter))

            // Update type chip
            when (outreach.outreachType) {
                OutreachType.EMAIL -> typeChipGroup.check(R.id.emailChip)
                OutreachType.PHONE_CALL -> typeChipGroup.check(R.id.phoneChip)
                OutreachType.LINKEDIN_MESSAGE -> typeChipGroup.check(R.id.linkedinChip)
                OutreachType.SOCIAL_MEDIA_POST -> typeChipGroup.check(R.id.socialChip)
                OutreachType.MEETING -> typeChipGroup.check(R.id.meetingChip)
                OutreachType.OTHER -> typeChipGroup.check(R.id.otherChip)
            }

            // Update status chip
            when (outreach.status) {
                OutreachStatus.PENDING -> statusChipGroup.check(R.id.pendingChip)
                OutreachStatus.COMPLETED -> statusChipGroup.check(R.id.completedChip)
                OutreachStatus.SCHEDULED -> statusChipGroup.check(R.id.scheduledChip)
                OutreachStatus.CANCELLED -> statusChipGroup.check(R.id.cancelledChip)
            }

            notesInput.setText(outreach.notes)
        }
    }

    private fun setupNewOutreach() {
        currentOutreach = Outreach(
            id = System.currentTimeMillis().toString(),
            clientId = args.clientId,
            leadId = args.leadId,
            outreachType = OutreachType.EMAIL,
            outreachDate = System.currentTimeMillis(),
            status = OutreachStatus.PENDING,
            notes = ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
