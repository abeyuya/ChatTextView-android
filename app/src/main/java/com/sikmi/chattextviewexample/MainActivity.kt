package com.sikmi.chattextviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import com.sikmi.chattextview.*

class MainActivity : AppCompatActivity(),
    MentionSelectDialog.MentionDialogListener,
    CustomEmojiSelectDialog.CustomEmojiDialogListener {

    private lateinit var chatTextView: ChatTextView
    private lateinit var sendButton: Button
    private lateinit var messageListAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListAdapter = MessageListAdapter(applicationContext)
        val list = findViewById<ListView>(R.id.message_list)
        list.adapter = messageListAdapter

        chatTextView = findViewById(R.id.edittext_chatbox)
        chatTextView.setup(object: ChatTextView.ChatTextViewListener {
            override fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>) {
                Log.d("ChatTextView", textBlocks.toString())
                sendButton.isEnabled = textBlocks.isNotEmpty()
            }

            override fun didChange(textView: ChatTextView, isFocused: Boolean) {
                Log.d("ChatTextView", "isFocused" + isFocused.toString())
            }
        })

        setupSendButton()
        setupMentionButton()
        setupCustomEmojiButton()
    }

    private fun setupSendButton() {
        sendButton = findViewById(R.id.button_chatbox_send)
        sendButton.setOnClickListener {
            val textBlocks = chatTextView.getCurrentTextBlocks()

            messageListAdapter.add(textBlocks)
            messageListAdapter.notifyDataSetChanged()

            chatTextView.clear()
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
        chatTextView.insertMention(mention)
    }

    override fun onCustomEmojiClick(customEmoji: TextBlockCustomEmoji) {
        chatTextView.insertCustomEmoji(customEmoji)
    }
}
