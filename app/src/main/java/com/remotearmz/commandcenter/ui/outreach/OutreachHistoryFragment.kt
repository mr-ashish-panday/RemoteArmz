package com.remotearmz.commandcenter.ui.outreach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.databinding.FragmentOutreachHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@AndroidEntryPoint
class OutreachHistoryFragment : Fragment() {
    private var _binding: FragmentOutreachHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OutreachViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutreachHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupTabs()
        setupStats()
        setupAddButton()
        observeOutreach()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTabs() {
        binding.viewPager.adapter = OutreachPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All"
                1 -> "Today"
                2 -> "This Week"
                3 -> "This Month"
                4 -> "Custom"
                else -> ""
            }
        }.attach()
    }

    private fun setupStats() {
        viewModel.outreachStats.observe(viewLifecycleOwner) { stats ->
            // TODO: Update stats UI
        }
    }

    private fun setupAddButton() {
        binding.addOutreachButton.setOnClickListener {
            findNavController().navigate(OutreachHistoryFragmentDirections.actionOutreachHistoryFragmentToAddOutreach())
        }
    }

    private fun observeOutreach() {
        viewModel.outreachList.observe(viewLifecycleOwner) { outreachList ->
            // TODO: Update outreach list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class OutreachPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AllOutreachFragment()
                1 -> TimeRangeOutreachFragment(
                    LocalDate.now().atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
                    LocalDate.now().plusDays(1).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
                )
                2 -> TimeRangeOutreachFragment(
                    LocalDate.now().minusDays(7).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
                    LocalDate.now().plusDays(1).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
                )
                3 -> TimeRangeOutreachFragment(
                    LocalDate.now().minusMonths(1).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
                    LocalDate.now().plusDays(1).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
                )
                else -> CustomRangeOutreachFragment()
            }
        }
    }
}
