package com.sikmi.chattextview

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UpdateLayout
import com.sunhapper.x.spedit.gif.span.ResizeIsoheightImageSpan
import java.util.*

class CustomEmojiSpan(customEmoji: TextBlockCustomEmoji): UpdateLayout {

    val customEmojiId = UUID.randomUUID().toString()
    val customEmoji = customEmoji

    companion object {
        fun createResizeGifDrawableSpan(
            gifDrawable: Drawable,
            customEmoji: TextBlockCustomEmoji
        ): Spannable {
            val spannable = SpannableString(customEmoji.escapedString)

            spannable.setSpan(
                CustomEmojiSpan(customEmoji),
                0,
                customEmoji.escapedString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ResizeIsoheightImageSpan(gifDrawable),
                0,
                customEmoji.escapedString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return spannable
        }
    }
}