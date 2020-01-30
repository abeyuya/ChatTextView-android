package com.sikmi.chattextview

import android.content.Context
import android.graphics.Color
import android.text.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.text.getSpans
import java.io.IOException

import com.sunhapper.glide.drawable.DrawableTarget
import com.sunhapper.x.spedit.createResizeGifDrawableSpan
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
    }

    private val spEditText = SpXEditText(context)

    init {
        spEditText.maxLines = 3
        spEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        addView(
            spEditText,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
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
        this.insertPlain(" ")
    }

    fun getCurrentTextBlocks(): List<TextBlock> {
        val spannable = spEditText.text as? Spannable ?: return listOf()
        return Parser.parse(spannable)
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

        return CustomEmojiSpan.createResizeGifDrawableSpan(
            proxyDrawable,
            emoji.escapedString,
            emoji
        )
    }
}