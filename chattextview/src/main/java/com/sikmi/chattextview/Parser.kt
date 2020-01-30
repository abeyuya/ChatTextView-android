package com.sikmi.chattextview

import android.text.Spannable
import androidx.core.text.getSpans

data class Size(
    val width: Float,
    val height: Float
)

enum class TextBlockType {
    PLAIN,
    MENTION,
    CUSTOM_EMOJI
}

interface TextBlock {
    val type: TextBlockType
}

data class TextBlockPlain(
    override val type: TextBlockType = TextBlockType.PLAIN,
    val text: String
) : TextBlock

data class TextBlockCustomEmoji(
    override val type: TextBlockType = TextBlockType.CUSTOM_EMOJI,
    val displayImageUrl: String,
    val escapedString: String,
    val size: Size
) : TextBlock

data class TextBlockMention(
    override val type: TextBlockType = TextBlockType.MENTION,
    val displayString: String,
    val hiddenString: String
) : TextBlock

object Parser {
    fun parse(spannable: Spannable): List<TextBlock> {
        var result = mutableListOf<TextBlock>()

        for (i in 0 until (spannable.length - 1)) {
            val spans = spannable.getSpans<Any>(i, i + 1)
            val string = spannable[i].toString()

            // mention
            val mentionBlock = spans
                .find { it is MentionSpan }
                ?.run {
                    TextBlockMention(
                        displayString = string,
                        hiddenString = string
                    )
                }
            if (mentionBlock != null) {
                result.add(mentionBlock)
                continue
            }

            // custom emoji
            // TODO

            val plainBlock = TextBlockPlain(text = string)
            result.add(plainBlock)
        }

        return bundle(result)
    }

    private fun bundle(parsedResult: List<TextBlock>): List<TextBlock> {
        var result = mutableListOf<TextBlock>()
        var prev: TextBlockType? = null
        var bundlingPlain: String? = null
        var bundlingMention: String? = null

        fun insertBundlingPlain() {
            bundlingPlain?.let {
                result.add(TextBlockPlain(text = it))
                bundlingPlain = null
            }
        }
        fun insertBundlingMention() {
            bundlingMention?.let {
//                guard let usedMention = usedMentions.first(where: { $0.displayString == b }) else { return }
                val m = TextBlockMention(displayString = it, hiddenString = it)
                result.add(m)
                bundlingMention = null
            }
        }

        for (t in parsedResult) {
            try {
                if (t is TextBlockPlain) {
                    val plain = t as? TextBlockPlain ?: continue

                    prev?.let {
                        if (prev == TextBlockType.PLAIN) {
                            bundlingPlain += t.text
                        }
                        if (prev == TextBlockType.CUSTOM_EMOJI) {
                            bundlingPlain = t.text
                        }
                        if (prev == TextBlockType.MENTION) {
                            insertBundlingMention()
                            bundlingPlain = t.text
                        }
                    } ?: run {
                        bundlingPlain = plain.text
                    }

                    continue
                }

                if (t is TextBlockCustomEmoji) {
                    insertBundlingMention()
                    insertBundlingPlain()
                    result.add(t)
                    continue
                }

                if (t is TextBlockMention) {
                    val mention = t as? TextBlockMention ?: continue

                    prev?.let {
                        if (prev == TextBlockType.PLAIN || prev == TextBlockType.CUSTOM_EMOJI) {
                            insertBundlingMention()
                            insertBundlingPlain()
                            bundlingMention = mention.displayString
                        }
                        if (prev == TextBlockType.MENTION) {
                            bundlingMention += t.displayString
                        }
                    } ?: run {
                        bundlingMention = mention.displayString
                    }

                    continue
                }

            } finally {
                prev = t.type
            }
        }

        insertBundlingPlain()
        insertBundlingMention()

        return result
    }
}

