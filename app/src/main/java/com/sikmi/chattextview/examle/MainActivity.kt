package com.sikmi.chattextview.examle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.sikmi.chattextview.R
import com.sikmi.chattextview.module.ChatTextView
import com.sikmi.chattextview.module.TextTypeMention

class MainActivity : AppCompatActivity(),
    MentionSelectDialog.MentionDialogListener {

    private var chatTextView: ChatTextView? = null
    private var messageListAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListAdapter = ArrayAdapter<String>(
            applicationContext,
            R.layout.message_orange,
            mutableListOf("hello", "world")
        )

        val list = findViewById<ListView>(R.id.message_list)
        list.adapter = messageListAdapter
        chatTextView = findViewById(R.id.edittext_chatbox)

        setupSendButton()
        setupMentionButton()
        setupCustomEmojiButton()
    }

    private fun setupSendButton() {
        val sendButton = findViewById<Button>(R.id.button_chatbox_send)
        sendButton.setOnClickListener {
            val text = this.chatTextView?.text.toString()
            this.chatTextView?.setText("")
            messageListAdapter?.add(text)
            messageListAdapter?.notifyDataSetChanged()
        }
    }

    private fun setupMentionButton() {
        val mentionButton = findViewById<Button>(R.id.button_chatbox_mention)
        mentionButton.setOnClickListener {
            val dialog = MentionSelectDialog()
            dialog.show(supportFragmentManager, "MentionSelectFragment")
        }
    }

    private fun setupCustomEmojiButton() {}

    override fun onMentionClick(mention: TextTypeMention) {
        chatTextView?.insertMention(mention)
    }
}
