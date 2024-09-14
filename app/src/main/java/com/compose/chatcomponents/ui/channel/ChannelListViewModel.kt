package com.compose.chatcomponents.ui.channel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.chatcomponents.base.App
import com.compose.chatcomponents.modal.ChannelListUiState
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.state.event.handler.chat.factory.ChatEventHandlerFactory
import io.getstream.chat.android.state.extensions.queryChannelsAsState
import io.getstream.result.call.enqueue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChannelListViewModel(val chatClient: ChatClient = App.getInstance().getClient()) :
    ViewModel() {
    private val _uiState: MutableLiveData<ChannelListUiState> = MutableLiveData()
    val uiState = _uiState





    fun getChannelUpdated() {
        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
            ),
            offset = 0,
            limit = 12,
            messageLimit = 100,
            memberLimit = 10,
            querySort = QuerySortByField.descByName("last_updated")
        )
        val queryChannelsState =
            chatClient.queryChannelsAsState(request, ChatEventHandlerFactory(), viewModelScope)

        viewModelScope.launch {
            queryChannelsState.filterNotNull().collectLatest { queryChannelsState ->
                if (!isActive) {
                    return@collectLatest
                }

                queryChannelsState.channels.collect { messages ->
                    if (messages != null) {
                        _uiState.value = ChannelListUiState(channels = messages, error = null)
                    } else {
                        _uiState.value = ChannelListUiState(error = null)
                    }

                }


            }
        }
    }

}


