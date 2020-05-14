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
import kotlin.math.min

class ChatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    data class Size(
        val width: Float,
        val height: Float
    )

    interface ChatTextViewListener {
        fun didChange(textView: ChatTextView, textBlocks: List<TextBlock>)
        fun didChange(textView: ChatTextView, isFocused: Boolean)
        fun didChange(textView: ChatTextView, contentSize: Size)
    }

    public val spEditText = SpXEditText(context)
    public var MAX_LINE_COUNT = 5

    init {
        isFocusableInTouchMode = true
        spEditText.maxLines = MAX_LINE_COUNT
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

    fun setup(listener: ChatTextViewListener) {
        spEditText.addTextChangedListener(object: TextWatcher {
            private var shouldDeleteMentionSpans = mutableListOf<MentionSpan>()

            override fun afterTextChanged(s: Editable?) {
                for (span in shouldDeleteMentionSpans) {
                    val editable = s ?: return
                    deleteMensionSpan(s, span)
                }
                shouldDeleteMentionSpans = mutableListOf()

                didChangeContentSize(listener)
                scrollForNewLine()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (after < count) {
                    handleTextWillDelete(s, start, count, after)
                    return
                }

                handleTextWillInsertOrUpdate(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    listener.didChange(this@ChatTextView, listOf())
                    return
                }

                val spannable = s as? Spannable ?: return

                val blocks = Parser.parse(spannable)
                listener.didChange(this@ChatTextView, blocks)
            }

            private fun deleteMensionSpan(s: Editable, span: MentionSpan) {
                val start = s.getSpanStart(span) ?: return
                if (start == -1) { return }
                val end = s.getSpanEnd(span)
                if (end == -1) { return }
                s.delete(start, end)
            }

            private fun pickMentionSpans(spannable: Spannable): List<MentionSpan> {
                return spannable
                        .getSpans<MentionSpan>(0, spannable.length)
                        .toList()
            }

            private fun handleTextWillDelete(s: CharSequence?, start: Int, count: Int, after: Int) {
                val deleted = s?.subSequence(
                        start + after,
                        start + after + count
                ) as? Spannable ?: return

                shouldDeleteMentionSpans.addAll(pickMentionSpans(deleted))
                return
            }

            private fun handleTextWillInsertOrUpdate(s: CharSequence?, start: Int, count: Int, after: Int) {
                // ignore insert to top of text
                if (start < 1) {
                    return
                }

                // ignore no insert
                if (after < 1) {
                    return
                }

                // ignore insert to end of text
                if (s.toString().length < start + after) {
                    return
                }

                val beforeSpannable = s?.subSequence(start - 1, start) as? Spannable ?: return
                val beforeMentionSpan = pickMentionSpans(beforeSpannable)
                if (beforeMentionSpan.isEmpty()) {
                    return
                }

                val afterSpannable = s.subSequence(start + count, start + count + 1) as? Spannable ?: return
                val afterMentionSpan = pickMentionSpans(afterSpannable)
                if (afterMentionSpan.isEmpty()) {
                    return
                }

                val allSpannable = s as? Spannable ?: return
                val beforeMentionId = beforeMentionSpan.firstOrNull()?.mentionId ?: return
                val afterMentionId = afterMentionSpan.firstOrNull()?.mentionId ?: return

                if (beforeMentionId == afterMentionId) {
                    val allMentionSpans = pickMentionSpans(allSpannable)
                    val deleteTargetMentionSpan = allMentionSpans.firstOrNull { it.mentionId == afterMentionId }
                    deleteTargetMentionSpan?.let { shouldDeleteMentionSpans.add(it) }
                }

                return
            }
        })

        spEditText.setOnFocusChangeListener { _, hasFocus ->
            listener.didChange(this@ChatTextView, hasFocus)
        }
    }

    fun insertPlain(text: String) {
        this.post {
            this.spEditText.text?.append(text)
        }
    }

    fun insertCustomEmoji(emoji: TextBlockCustomEmoji) {
        spEditText.text?.let {
            val charSequence = createGlideText(emoji)
            this.post {
                insertSpannableString(it, charSequence)
            }
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
        this.post {
            spEditText.setText("")
        }
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
            this.post {
                insertSpannableString(it, charSequence)
            }
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

    private fun didChangeContentSize(listener: ChatTextViewListener) {
        val totalHeight = min(spEditText.lineCount, MAX_LINE_COUNT) *
                (spEditText.lineHeight + spEditText.lineSpacingExtra) *
                spEditText.lineSpacingMultiplier +
                spEditText.compoundPaddingTop +
                spEditText.compoundPaddingBottom

        val size = Size(width = 0f, height = totalHeight)
        listener.didChange(this@ChatTextView, size)
    }

    // https://stackoverflow.com/a/7350267
    private fun scrollForNewLine() {
        val scrollAmount = spEditText.layout.getLineTop(spEditText.lineCount) - spEditText.height + spEditText.lineHeight

        if (scrollAmount > 0) {
            spEditText.scrollTo(0, scrollAmount)
        }
    }
}