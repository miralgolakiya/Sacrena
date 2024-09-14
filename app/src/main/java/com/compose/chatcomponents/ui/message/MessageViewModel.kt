package com.compose.chatcomponents.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.chatcomponents.base.App
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.channel.state.ChannelState
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.state.extensions.loadOlderMessages
import io.getstream.chat.android.state.extensions.watchChannelAsState

import io.getstream.result.call.enqueue
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

class MessageViewModel(val chatClient: ChatClient = App.getInstance().getClient()) : ViewModel() {

    private val _uiState: MutableLiveData<MessageListUiState> = MutableLiveData()
    val uiState: MutableLiveData<MessageListUiState> = _uiState

    private val _channelState: MutableLiveData<Channel> = MutableLiveData()
    val channelState: MutableLiveData<Channel> = _channelState

    private val _sendMessageState: MutableLiveData<Boolean> = MutableLiveData()
    val sendMessageState: MutableLiveData<Boolean> = _sendMessageState


//     val channel: MutableLiveData<Channel> = MutableLiveData()


    fun getMessages(id: String) {
        val channelStateFlow: StateFlow<ChannelState?> = chatClient.watchChannelAsState(
            cid = "messaging:$id",
            messageLimit = 20,
            coroutineScope = viewModelScope
        )



        viewModelScope.launch {
            channelStateFlow.collect { channelState ->
                if (channelState != null) {
                    _channelState.value = channelState.toChannel()
                    channelState.messages.collect { messages ->
                        _uiState.value = MessageListUiState(messages = messages, error = null)
                    }
                } else {
                    _uiState.value = MessageListUiState(error = null)
                }
            }
        }
    }

    fun loadMoreMessages(id: String) {
        chatClient.loadOlderMessages(cid = "messaging:$id", messageLimit = 20).enqueue(
            onError = { streamError ->

            }
        )
    }

    fun sendMessage(text: String) {
        val message = Message(text = text)
        channelState.value?.cid?.let {
            chatClient.channel(it).sendMessage(message).enqueue(onError = {
                _sendMessageState.value = false
            }, onSuccess = {
                _sendMessageState.value = true
            })
        }
    }


}

data class MessageListUiState(
    val messages: List<Message> = emptyList(),
    val error: String? = null,
)