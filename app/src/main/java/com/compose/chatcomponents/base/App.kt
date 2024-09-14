package com.compose.chatcomponents.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.User
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class App : Application() {

    private lateinit var client: ChatClient

    companion object {
        val userId: String = "alice"
        var user: User? = null
        private lateinit var instance: App
        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initClient()
    }

    private fun initClient() {
        val apiKey = "t46whwhh5fus"

        val statePlugin = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = applicationContext,
        )


        client = ChatClient.Builder(apiKey, applicationContext)
            // Change log level
            .logLevel(ChatLogLevel.ALL)
            .withPlugins(statePlugin)
            .build()

        val aliceUser = User(
            id = userId,
            name = "Alice",
            image = "https://picsum.photos/id/64/400/400",
        )






        client.connectUser(
            user = aliceUser,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYWxpY2UifQ.WZkPaUZb84fLkQoEEFw078Xd1RzwR42XjvBISgM2BAk",
        ).enqueue { result ->
            if (result.isSuccess) {
                // Handle success
                user = result.getOrNull()?.user
            } else {
                // Handler error
            }
        }
    }


    fun getClient(): ChatClient {
        return client
    }

}