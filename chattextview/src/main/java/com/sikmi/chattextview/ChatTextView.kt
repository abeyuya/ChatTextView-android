package com.sikmi.chattextview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.sunhapper.x.spedit.view.SpXEditText

class ChatTextView : RelativeLayout {
    private val spEditText: SpXEditText

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.spEditText = SpXEditText(context)
        addView(this.spEditText)
    }

    private lateinit var listener: ChatTextViewListener

    interface ChatTextViewListener {
        fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>)
    }

    fun setup(listener: ChatTextViewListener) {
        spEditText.addTextChangedListener(object: TextWatcher {
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
        this.spEditText.text?.append(text)
    }

    fun insertcustomEmoji(emoji: TextBlockCustomEmoji) {
        // TODO
        this.spEditText.text?.append(emoji.escapedString)
    }

    fun insertMention(mention: TextBlockMention) {
        // TODO
        this.spEditText.text?.append(mention.displayString)
        this.insertPlain(" ")
    }

    fun getCurrentTextBlocks(): List<TextBlock> {
        // TODO
        val text = this.spEditText.text.toString()
        val block = TextBlockPlain(
            type = TextBlockType.PLAIN,
            text = text
        )

        return listOf(block)
    }

    fun clear() {
        this.spEditText.setText("")
    }

    fun render(textBlocks: List<TextBlock>) {
        // TODO
    }
}