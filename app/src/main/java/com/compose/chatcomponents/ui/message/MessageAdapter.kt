package com.compose.chatcomponents.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.compose.chatcomponents.base.App
import com.compose.chatcomponents.databinding.RvChatReceiverBinding
import com.compose.chatcomponents.databinding.RvChatSenderBinding
import io.getstream.chat.android.models.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val diffUtil = object : DiffUtil.ItemCallback<MessageBean>() {
        override fun areItemsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
            return oldItem.message.id == newItem.message.id
        }

        override fun areContentsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
            return oldItem.message == newItem.message
        }

    }
    val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    class ReceiverViewHolder(val binding: RvChatReceiverBinding) :
        RecyclerView.ViewHolder(binding.root)

    class SenderViewHolder(val binding: RvChatSenderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 ->
                ReceiverViewHolder(
                    RvChatReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            else -> SenderViewHolder(
                RvChatSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageBean = asyncListDiffer.currentList[position]
        val bean = messageBean.message
        if (holder is ReceiverViewHolder) {
            holder.binding.tvMessage.text = bean.text
            bean.createdAt?.let {
                holder.binding.tvTime.text = SimpleDateFormat("hh:mm a").format(it)
            }
            holder.binding.tvTimeHeader.visibility = messageBean.isVisible
            holder.binding.tvTimeHeader.text = messageBean.date
            holder.binding.ivSenderImage.setUser(bean.user)
        } else if (holder is SenderViewHolder) {
            holder.binding.tvMessage.text = bean.text
            holder.binding.tvTimeHeader.text = messageBean.date
            holder.binding.tvTimeHeader.visibility = messageBean.isVisible
            bean.createdAt?.let {
                holder.binding.tvTime.text = SimpleDateFormat("hh:mm a").format(it)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (asyncListDiffer.currentList[position].message.user.id == App.userId) {
            return 1
        } else {
            0
        }
    }


    fun setList(list: ArrayList<Message>) {
        asyncListDiffer.submitList(getMessageBean(list))
    }

    private fun getMessageBean(list: java.util.ArrayList<Message>): ArrayList<MessageBean> {
        val messageBeanList = arrayListOf<MessageBean>()

        list.forEachIndexed { index, message ->
            val currentModel = MessageBean(message)
            val beforeModel: MessageBean? = if (index == 0) {
                null
            } else {
                messageBeanList[index - 1]
            }
            currentModel.date = getToday(message.createdAt ?: message.createdLocallyAt ?: Date())

            if (beforeModel == null) {
                currentModel.isVisible = View.VISIBLE
            } else {
                currentModel.isVisible =
                    if (beforeModel.date == currentModel.date) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
            messageBeanList.add(currentModel)
        }


        return messageBeanList

    }

    data class MessageBean(
        val message: Message,
        var date: String? = null,
        var isVisible: Int = View.GONE
    )

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private fun getToday(dateString: Date): String? {

        try {
            // Attempt to parse the date string

            val calendar = Calendar.getInstance()
            var cDate = calendar.time
            var sDate = simpleDateFormat.format(cDate)
            if (sDate.equals(simpleDateFormat.format(dateString))) {
                // If parsed date is today, return "Today"
                return "Today"
            }
            // Check if parsed date is yesterday
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            cDate = calendar.time
            sDate = simpleDateFormat.format(cDate)
            if (sDate.equals(simpleDateFormat.format(dateString))) {
                // If parsed date is yesterday, return "Yesterday"
                return "Yesterday"
            }
        } catch (e: Exception) {
            // Print stack trace if parsing fails
            e.printStackTrace()
        }
// Return original date string if parsing fails or date doesn't match today or yesterday
        return simpleDateFormat.format(dateString)

    }
}