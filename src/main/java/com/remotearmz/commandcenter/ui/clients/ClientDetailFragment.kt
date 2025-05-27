package com.remotearmz.commandcenter.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.data.model.Client
import com.remotearmz.commandcenter.databinding.FragmentClientDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDetailFragment : Fragment() {
    private var _binding: FragmentClientDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSaveButton()
        setupDeliverables()
        observeSelectedClient()
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val client = createClientFromInputs()
            viewModel.selectedClient.value?.let { existingClient ->
                viewModel.updateClient(client.copy(id = existingClient.id))
            } ?: viewModel.addClient(client)
            findNavController().navigateUp()
        }
    }

    private fun setupDeliverables() {
        binding.deliverablesGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val deliverables = checkedIds.map { id ->
                when (id) {
                    R.id.socialMediaChip -> "Social Media Management"
                    R.id.contentCreationChip -> "Content Creation"
                    R.id.seoChip -> "SEO"
                    R.id.websiteDevChip -> "Website Development"
                    R.id.brandDesignChip -> "Brand Design"
                    else -> ""
                }
            }.filter { it.isNotEmpty() }
            binding.deliverablesGroup.tag = deliverables
        }
    }

    private fun observeSelectedClient() {
        viewModel.selectedClient.observe(viewLifecycleOwner) { client ->
            client?.let { fillForm(it) }
        }
    }

    private fun createClientFromInputs(): Client {
        return Client(
            id = System.currentTimeMillis().toString(),
            name = binding.nameInput.text.toString(),
            whatsapp = binding.whatsappInput.text.toString(),
            email = binding.emailInput.text.toString(),
            instagram = binding.instagramInput.text.toString(),
            monthlyCharge = binding.monthlyChargeInput.text.toString().toDoubleOrNull() ?: 0.0,
            deliverables = binding.deliverablesGroup.tag as? List<String> ?: emptyList(),
            paymentDate = binding.paymentDateInput.text.toString(),
            status = ClientStatus.ACTIVE
        )
    }

    private fun fillForm(client: Client) {
        binding.nameInput.setText(client.name)
        binding.whatsappInput.setText(client.whatsapp)
        binding.emailInput.setText(client.email)
        binding.instagramInput.setText(client.instagram)
        binding.monthlyChargeInput.setText(client.monthlyCharge.toString())
        binding.paymentDateInput.setText(client.paymentDate)

        val deliverables = client.deliverables
        binding.deliverablesGroup.check(
            deliverables.map { deliverable ->
                when (deliverable) {
                    "Social Media Management" -> R.id.socialMediaChip
                    "Content Creation" -> R.id.contentCreationChip
                    "SEO" -> R.id.seoChip
                    "Website Development" -> R.id.websiteDevChip
                    "Brand Design" -> R.id.brandDesignChip
                    else -> -1
                }
            }.filter { it != -1 }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
