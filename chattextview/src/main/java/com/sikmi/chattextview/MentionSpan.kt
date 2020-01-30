package com.sikmi.chattextview

import android.text.style.ForegroundColorSpan
import java.util.*

class MentionSpan(
    colorType: Int,
    mention: TextBlockMention
): ForegroundColorSpan(colorType) {

    val mentionId = UUID.randomUUID().toString()
    val mention = mention
}