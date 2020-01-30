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

data class BlockInfo(
    val textBlock: TextBlock,
    val span: Any?
)

object Parser {
    fun parse(spannable: Spannable): List<TextBlock> {
        var result = mutableListOf<BlockInfo>()

        for (i in 0 until spannable.length) {
            val spans = spannable.getSpans<Any>(i, i + 1)
            val string = spannable[i].toString()

            // mention
            val mentionSpan = spans
                .find { it is MentionSpan }
                .run { this as? MentionSpan }
            if (mentionSpan != null) {
                val blockInfo = BlockInfo(
                    textBlock = mentionSpan.mention,
                    span = mentionSpan
                )
                result.add(blockInfo)
                continue
            }

            // custom emoji
            val customEmojiSpan = spans
                .find { it is CustomEmojiSpan }
                .run { this as? CustomEmojiSpan }
            if (customEmojiSpan != null) {
                val blockInfo = BlockInfo(
                    textBlock = customEmojiSpan.customEmoji,
                    span = customEmojiSpan
                )
                result.add(blockInfo)
                continue
            }

            val plainBlock = TextBlockPlain(text = string)
            result.add(BlockInfo(textBlock = plainBlock, span = null))
        }

        return bundle(result)
    }

    private fun bundle(parsedResult: List<BlockInfo>): List<TextBlock> {
        var result = mutableListOf<TextBlock>()
        var prev: BlockInfo? = null
        var bundlingPlain: TextBlockPlain? = null
        var bundlingCustomEmoji: CustomEmojiSpan? = null
        var bundlingMention: MentionSpan? = null

        fun insertBundlingPlain() {
            bundlingPlain?.let {
                result.add(it)
                bundlingPlain = null
            }
        }
        fun insertBundlingMention() {
            bundlingMention?.let {
                result.add(it.mention)
                bundlingMention = null
            }
        }
        fun insertBundlingCustomEmoji() {
            bundlingCustomEmoji?.let {
                result.add(it.customEmoji)
                bundlingCustomEmoji = null
            }
        }

        for (blockInfo in parsedResult) {
            val t = blockInfo.textBlock
            val span = blockInfo?.span

            try {
                if (t is TextBlockPlain) {
                    val newBlock = t as? TextBlockPlain ?: continue

                    bundlingPlain?.let {
                        val newText = it.text + newBlock.text
                        bundlingPlain = TextBlockPlain(text = newText)
                    } ?: run {
                        bundlingPlain = newBlock
                    }

                    prev?.let {
                        if (it.textBlock is TextBlockPlain) {
                            // skip because this custom emoji has already handled
                        } else {
                            insertBundlingCustomEmoji()
                            insertBundlingMention()
                        }
                    }

                    continue
                }

                if (t is TextBlockCustomEmoji) {
                    val newSpan = span as? CustomEmojiSpan ?: continue

                    bundlingCustomEmoji?.let {
                        if (it.customEmojiId == newSpan.customEmojiId) {
                            // skip because this custom emoji has already handled
                        } else {
                            insertBundlingCustomEmoji()
                            bundlingCustomEmoji = newSpan
                        }
                    } ?: run {
                        bundlingCustomEmoji = newSpan
                    }

                    prev?.let {
                        if (it.textBlock is TextBlockCustomEmoji) {
                            // do nothing
                        } else {
                            insertBundlingMention()
                            insertBundlingPlain()
                        }
                    }

                    continue
                }

                if (t is TextBlockMention) {
                    val newSpan = span as? MentionSpan ?: continue

                    bundlingMention?.let {
                        if (it.mentionId == newSpan.mentionId) {
                            // skip because this mention has already handled
                        } else {
                            insertBundlingMention()
                            bundlingMention = newSpan
                        }
                    } ?: run {
                        bundlingMention = newSpan
                    }

                    prev?.let {
                        if (it.textBlock is TextBlockMention) {
                            // do nothing
                        } else {
                            insertBundlingCustomEmoji()
                            insertBundlingPlain()
                        }
                    }

                    continue
                }

            } finally {
                prev = blockInfo
            }
        }

        insertBundlingPlain()
        insertBundlingCustomEmoji()
        insertBundlingMention()

        return result
    }
}

