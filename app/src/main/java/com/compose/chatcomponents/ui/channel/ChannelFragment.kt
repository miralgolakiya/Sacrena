package com.compose.chatcomponents.ui.channel

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.compose.chatcomponents.R
import com.compose.chatcomponents.databinding.FragmentChannelBinding
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.FilterObject
import io.getstream.chat.android.models.querysort.QuerySorter
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory


class ChannelFragment : Fragment(R.layout.fragment_channel) {


    private lateinit var binding: FragmentChannelBinding
    private val viewModel: com.compose.chatcomponents.ui.channel.ChannelListViewModel by viewModels()
    private lateinit var adapter: ChannelAdapter
    protected val channelListViewModel: ChannelListViewModel by viewModels { createChannelListViewModelFactory() }

    protected fun createChannelListViewModelFactory(): ChannelListViewModelFactory {
        return ChannelListViewModelFactory(filter = getFilter(), sort = getSort())
    }

    protected fun getFilter(): FilterObject? {
        return null
    }

    /**
     * Default query sort for channels. Override the method to provide custom sort.
     */
    protected fun getSort(): QuerySorter<Channel> {
        return ChannelListViewModel.DEFAULT_SORT
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelBinding.bind(view)
        initAdapter()
//
        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it.error.isNullOrEmpty()) {
                adapter.setList(ArrayList(it.channels))
                binding.rvChannels.adapter = adapter
//                binding.viewAnimator.displayedChild = 0
            } else {
//                binding.viewAnimator.displayedChild = 1
            }
        }

        viewModel.getChannelUpdated()

    }


    private fun initAdapter() {
        adapter = ChannelAdapter {
            findNavController().navigate(
                R.id.nav_message, bundleOf(
                    "channel" to it.id
                )
            )
        }

        binding.rvChannels.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.rvChannels.addItemDecoration(
            DividerItemDecoration(
                binding.rvChannels.context,
                DividerItemDecoration.VERTICAL
            ).apply {
                val drawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.channel_divider
                )
                drawable?.let {
                    setDrawable(it)
                }
            }
        )

    }
}

