package com.compose.chatcomponents.ui.message

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.compose.chatcomponents.R
import com.compose.chatcomponents.base.App
import com.compose.chatcomponents.databinding.FragmentMessageBinding
import io.getstream.chat.android.client.extensions.internal.users
import io.getstream.chat.android.ui.feature.messages.list.EndlessMessageListScrollListener


class MessageFragment : Fragment(R.layout.fragment_message), View.OnClickListener {

    private lateinit var binding: FragmentMessageBinding
    private var channelId: String? = null
    private val viewModel: MessageViewModel by viewModels()
    lateinit var adapter: MessageAdapter
    private val LOAD_MORE_THRESHOLD = 10


    private lateinit var loadMoreListener: EndlessMessageListScrollListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageBinding.bind(view)
        channelId = arguments?.getString("channel")



        subscribeToObserver()
        initAdapter()
        setClicks()

        channelId?.let {
            viewModel.getMessages(it)
        }


    }

    private fun subscribeToObserver() {
        loadMoreListener =
            EndlessMessageListScrollListener(LOAD_MORE_THRESHOLD, loadMoreAtTopListener = {
                channelId?.let { viewModel.loadMoreMessages(it) }
            }, loadMoreAtBottomListener = {

            })

        loadMoreListener.enablePagination()

        viewModel.uiState.observe(viewLifecycleOwner) { it ->
            if (!it.error.isNullOrEmpty()) {
                if (adapter.itemCount == 0) {
                    binding.viewAnimator.displayedChild = 1
                }
            } else {

                if (it.messages.isEmpty()) {
                    binding.viewAnimator.displayedChild = 1
                    val name =
                        viewModel.channelState.value?.members?.firstOrNull { it.user.id != App.userId }?.user?.name
                            ?: ""
                    binding.noData.tvMessage.text = getString(R.string.no_message, name)
                } else {
                    binding.viewAnimator.displayedChild = 0
                    adapter.setList(ArrayList(it.messages))
                }

            }
        }
        viewModel.channelState.observe(viewLifecycleOwner) {
            binding.channelAvatarView.setChannel(it)
            binding.tvChannelName.text = it.name
        }


        viewModel.sendMessageState.observe(viewLifecycleOwner) {
            if (it) {
                binding.etMessage.setText("")
            }
        }

        binding.etMessage.doAfterTextChanged {
            binding.ivSend.isEnabled = it.toString().isNotEmpty()
        }
        binding.etMessage.setText("")
    }

    private fun setClicks() {
        binding.ivBack.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
    }

    private fun initAdapter() {
        adapter = MessageAdapter()
        binding.rvMessage.adapter = adapter
        binding.rvMessage.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            setHasFixedSize(false)
            setItemViewCacheSize(20)

            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = resources.getDimensionPixelSize(R.dimen.margin_side)
                marginEnd = resources.getDimensionPixelSize(R.dimen.margin_side)
            }
            overScrollMode = View.OVER_SCROLL_ALWAYS
        }

        binding.rvMessage.addOnScrollListener(loadMoreListener)

        binding.rvMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition =
                    (binding.rvMessage.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
                val item = adapter.asyncListDiffer.currentList[firstVisiblePosition]
                item.date?.let {
                    binding.tvTimeHighlight.post {
                        binding.tvTimeHighlight.hideText(it)
                    }
                }
            }
        })


    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack -> {
                findNavController().popBackStack()
            }

            binding.ivSend -> {
                val message = binding.etMessage.text.toString().trim()
                if (message.isEmpty()) {
                    return
                }

                viewModel.sendMessage(message)
            }
        }
    }


}


