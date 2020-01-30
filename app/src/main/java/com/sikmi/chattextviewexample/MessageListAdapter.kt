package com.sikmi.chattextviewexample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.sikmi.chattextview.ChatTextView
import com.sikmi.chattextview.TextBlock

class MessageListAdapter(
    context: Context,
    resource: Int = 0
) : ArrayAdapter<List<TextBlock>>(context, resource) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val messageView  = layoutInflater.inflate(R.layout.message_orange, parent, false)
        val chatTextView = messageView.findViewById<ChatTextView>(R.id.text_message_body)
        chatTextView.enableRenderOnlyStyle()

        val textBlocks = getItem(position)
        textBlocks?.let {
            chatTextView.render(it)
        }

        return chatTextView
    }
}