package com.compose.chatcomponents.modal

import io.getstream.chat.android.models.Channel

data class ChannelListUiState(
    val channels: List<Channel> = emptyList(),
    val error: String? = null,
    )