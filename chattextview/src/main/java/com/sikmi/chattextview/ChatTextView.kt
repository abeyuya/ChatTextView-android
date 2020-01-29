package com.sikmi.chattextview

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.sunhapper.glide.drawable.DrawableTarget

import com.sunhapper.x.spedit.createResizeGifDrawableSpan
import com.sunhapper.x.spedit.gif.drawable.ProxyDrawable
import com.sunhapper.x.spedit.insertSpannableString
import com.sunhapper.x.spedit.view.SpXEditText
import java.io.IOException

class ChatTextView : RelativeLayout {
    interface ChatTextViewListener {
        fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>)
    }

    private val spEditText: SpXEditText
    private lateinit var listener: ChatTextViewListener

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.spEditText = SpXEditText(context)
        this.spEditText.maxLines = 3
        addView(
            this.spEditText,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun setup(listener: ChatTextViewListener) {
        spEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        spEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    listener.didChange(this@ChatTextView, listOf())
                    return
                }

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
        spEditText?.text?.let {
            val charSequence = createGlideText(emoji)
            insertSpannableString(it, charSequence)
        }
    }

    fun insertMention(mention: TextBlockMention) {
        val ss = getMentionSppanableString(mention.displayString)
        replace(ss)
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

    //
    // private methods
    //

    private fun getMentionSppanableString(displayString: String): Spannable {
        val styleSpan = ForegroundColorSpan(Color.MAGENTA)
        val spannableString = SpannableString(displayString)
        spannableString.setSpan(
            styleSpan, 0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            this,
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(spannableString)
        return stringBuilder
    }

    private fun replace(charSequence: CharSequence) {
        spEditText?.text?.let {
            insertSpannableString(it, charSequence)
        }
    }

    @Throws(IOException::class)
    private fun createGlideText(emoji: TextBlockCustomEmoji): CharSequence {
        val d = context.getDrawable(R.drawable.emoji)
        val proxyDrawable = ProxyDrawable()
        Glide.with(this)
            .load(emoji.displayImageUrl)
            .placeholder(d)
            .into(DrawableTarget(proxyDrawable))
        return createResizeGifDrawableSpan(proxyDrawable, emoji.escapedString)
    }
}