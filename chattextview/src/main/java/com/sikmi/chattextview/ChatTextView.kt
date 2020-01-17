package com.sikmi.chattextview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText

public class ChatTextView : EditText {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var listener: ChatTextViewListener

    interface ChatTextViewListener {
        fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>)
    }

    fun setup(listener: ChatTextViewListener) {
        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val block = TextBlockPlain(
                    type = TextBlockType.PLAIN,
                    text = s.toString()
                )
                listener.didChange(this@ChatTextView, listOf(block))
            }
        })

        this.listener = listener
    }

    fun insertPlain(text: String) {
        this.text.append(text)
    }

    fun insertcustomEmoji(emoji: TextBlockCustomEmoji) {
        // TODO
        this.text.append(emoji.escapedString)
    }

    fun insertMention(mention: TextBlockMention) {
        // TODO
        this.text.append(mention.displayString)
        this.insertPlain(" ")
    }

    fun getCurrentTextBlocks(): List<TextBlock> {
        // TODO
        val text = this.text.toString()
        val block = TextBlockPlain(
            type = TextBlockType.PLAIN,
            text = text
        )

        return listOf(block)
    }

    fun clear() {
        this.setText("")
    }

    fun render(textBlocks: List<TextBlock>) {
        // TODO
    }
}