package com.sikmi.chattextview

import android.content.Context
import android.graphics.Color
import android.text.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.text.getSpans
import java.io.IOException

import com.sunhapper.glide.drawable.DrawableTarget
import com.sunhapper.x.spedit.gif.drawable.ProxyDrawable
import com.sunhapper.x.spedit.insertSpannableString
import com.sunhapper.x.spedit.view.SpXEditText

import com.bumptech.glide.Glide

class ChatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface ChatTextViewListener {
        fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>)
        fun didChange(textView: ChatTextView, isFocused: Boolean)
        fun didChange(textView: ChatTextView, contentSize: Size)
    }

    private val spEditText = SpXEditText(context)

    init {
        isFocusableInTouchMode = true
        spEditText.maxLines = 3
        spEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        spEditText.background = null
        addView(
            spEditText,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun getContentSize(): Size {
        val width = spEditText.width
        val height = spEditText.height
        return Size(width = width.toFloat(), height = height.toFloat())
    }

    fun setup(listener: ChatTextViewListener) {
        spEditText.addTextChangedListener(object: TextWatcher {
            private var shouldDeleteMentionSpans = listOf<MentionSpan>()

            override fun afterTextChanged(s: Editable?) {
                for (span in shouldDeleteMentionSpans) {
                    val start = s?.getSpanStart(span) ?: continue
                    if (start == -1) { continue }
                    val end = s.getSpanEnd(span)
                    if (end == -1) { continue }
                    s.delete(start, end)
                }
                shouldDeleteMentionSpans = listOf()

                val size = getContentSize()
                listener.didChange(this@ChatTextView, size)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (after < count) {
                    val deleted = s?.subSequence(
                        start + after,
                        start + after + count
                    ) as? Spannable ?: return

                     shouldDeleteMentionSpans = deleted
                         .getSpans<MentionSpan>(0, deleted.length)
                         .toList()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    listener.didChange(this@ChatTextView, listOf())
                    return
                }

                val spannable = s as? Spannable

                spannable?.let {
                    val blocks = Parser.parse(it)
                    listener.didChange(this@ChatTextView, blocks)
                }
            }
        })

        spEditText.setOnFocusChangeListener { _, hasFocus ->
            listener.didChange(this@ChatTextView, hasFocus)
        }
    }

    fun insertPlain(text: String) {
        this.spEditText.text?.append(text)
    }

    fun insertCustomEmoji(emoji: TextBlockCustomEmoji) {
        spEditText.text?.let {
            val charSequence = createGlideText(emoji)
            insertSpannableString(it, charSequence)
        }
    }

    fun insertMention(mention: TextBlockMention) {
        val ss = getMentionSpannableString(mention)
        replace(ss)
        insertPlain(" ")
    }

    fun getCurrentTextBlocks(): List<TextBlock> {
        val spannable = spEditText.text as? Spannable ?: return listOf()
        return Parser.parse(spannable)
    }

    fun clear() {
        spEditText.setText("")
    }

    fun setFocusAndShowKeyboard() {
        spEditText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(spEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun render(textBlocks: List<TextBlock>) {
        textBlocks.forEach {
            when (it) {
                is TextBlockPlain -> { insertPlain(it.text) }
                is TextBlockCustomEmoji -> { insertCustomEmoji(it) }
                is TextBlockMention -> { insertMention(it) }
                else -> {
                    // TODO
                }
            }
        }
    }

    fun enableRenderOnlyStyle() {
        spEditText.isEnabled = false
        spEditText.setTextColor(Color.WHITE)
        spEditText.maxLines = 9999
        spEditText.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun isFocused(): Boolean {
        return spEditText.isFocused
    }

    //
    // private methods
    //

    private fun getMentionSpannableString(mention: TextBlockMention): Spannable {
        val spannableString = SpannableString(mention.displayString)
        spannableString.setSpan(
            MentionSpan(Color.BLUE, mention),
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(spannableString)
        return stringBuilder
    }

    private fun replace(charSequence: CharSequence) {
        spEditText.text?.let {
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

        return CustomEmojiSpan.createResizeGifDrawableSpan(proxyDrawable, emoji)
    }
}