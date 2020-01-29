package com.sikmi.chattextviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.sikmi.chattextview.*

class MainActivity : AppCompatActivity(),
    MentionSelectDialog.MentionDialogListener,
    CustomEmojiSelectDialog.CustomEmojiDialogListener {

    private lateinit var chatTextView: ChatTextView
    private lateinit var sendButton: Button
    private lateinit var messageListAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListAdapter = ArrayAdapter(
            applicationContext,
            R.layout.message_orange,
            mutableListOf("hello", "world")
        )

        val list = findViewById<ListView>(R.id.message_list)
        list.adapter = messageListAdapter
        chatTextView = findViewById(R.id.edittext_chatbox)
        chatTextView.setup(object: ChatTextView.ChatTextViewListener {
            override fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>) {
                Log.d("ChatTextView", textBlocks.toString())
                sendButton.isEnabled = textBlocks.isNotEmpty()
            }
        })

        setupSendButton()
        setupMentionButton()
        setupCustomEmojiButton()
    }

    private fun setupSendButton() {
        sendButton = findViewById(R.id.button_chatbox_send)
        sendButton.setOnClickListener {
            val textBlocks = this.chatTextView.getCurrentTextBlocks()
            this.chatTextView.clear()

            // TODO
            val text = textBlocks?.first()?.run {
                when (this) {
                    is TextBlockPlain -> { this.text }
                    is TextBlockMention -> { this.displayString }
                    is TextBlockCustomEmoji -> { this.escapedString }
                    else -> { "" }
                }
            }
            messageListAdapter.add(text)
            messageListAdapter.notifyDataSetChanged()
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
        chatTextView.insertcustomEmoji(customEmoji)
    }
}
