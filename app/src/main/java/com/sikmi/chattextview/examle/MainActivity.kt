package com.sikmi.chattextview.examle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.sikmi.chattextview.R
import com.sikmi.chattextview.module.ChatTextView
import com.sikmi.chattextview.module.TextBlock
import com.sikmi.chattextview.module.TextBlockCustomEmoji
import com.sikmi.chattextview.module.TextBlockMention

class MainActivity : AppCompatActivity(),
    MentionSelectDialog.MentionDialogListener,
    CustomEmojiSelectDialog.CustomEmojiDialogListener {

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
        chatTextView?.setup(object: ChatTextView.ChatTextViewListener {
            override fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>) {
                Log.d("ChatTextView", textBlocks.toString())
            }
        })

        setupSendButton()
        setupMentionButton()
        setupCustomEmojiButton()
    }

    private fun setupSendButton() {
        val sendButton = findViewById<Button>(R.id.button_chatbox_send)
        sendButton.setOnClickListener {
            val text = this.chatTextView?.text.toString()
            this.chatTextView?.clear()

            // TODO
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

    private fun setupCustomEmojiButton() {
        val customEmojiButton = findViewById<Button>(R.id.button_chatbox_custom_emoji)
        customEmojiButton.setOnClickListener {
            val dialog = CustomEmojiSelectDialog()
            dialog.show(supportFragmentManager, "CustomEmojiSelectFragment")
        }
    }

    override fun onMentionClick(mention: TextBlockMention) {
        chatTextView?.insertMention(mention)
    }

    override fun onCustomEmojiClick(customEmoji: TextBlockCustomEmoji) {
        chatTextView?.insertcustomEmoji(customEmoji)
    }
}
