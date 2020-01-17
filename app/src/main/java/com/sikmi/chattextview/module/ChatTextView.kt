package com.sikmi.chattextview.module

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText

class ChatTextView : EditText {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged", s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val arr = listOf(s.toString(), start.toString(), count.toString(), after.toString())
                Log.d("beforeTextChanged", arr.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val arr = listOf(s.toString(), start.toString(), before.toString(), count.toString())
                Log.d("onTextChanged", arr.toString())
            }
        })
    }

    fun insertPlain(text: String) {
        this.text.append(text)
    }

    fun insertcustomEmoji(emoji: TextTypeCustomEmoji) {
        // TODO
        this.text.append(emoji.escapedString)
    }

    fun insertMention(mention: TextTypeMention) {
        // TODO
        this.text.append(mention.displayString)
        this.insertPlain(" ")
    }
}