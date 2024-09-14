package com.compose.chatcomponents.ui.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.compose.chatcomponents.databinding.RvChannelBinding
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.ui.utils.extensions.getLastMessage

class ChannelAdapter(private val onChannelClick: ((Channel) -> Unit)) :
    RecyclerView.Adapter<ChannelAdapter.ViewHolder>() {

    private var list = ArrayList<Channel>()

    class ViewHolder(val binding: RvChannelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChannelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = list[holder.adapterPosition]
        holder.binding.tvChannelName.text = channel.name
        holder.binding.tvChannelMessage.text = channel.getLastMessage()?.text
        holder.binding.channelAvatarView.setChannel(channel)

        holder.binding.root.setOnClickListener {
            onChannelClick.invoke(channel)
        }

    }


    fun setList(list: ArrayList<Channel>) {
        this.list = list
        notifyDataSetChanged()
    }


}