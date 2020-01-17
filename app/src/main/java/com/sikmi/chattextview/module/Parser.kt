package com.sikmi.chattextview.module

data class Size(
    val width: Float,
    val height: Float
)

enum class TextBlockType {
    PLAIN, MENTION, CUSTOM_EMOJI
}

interface TextBlock {
    val type: TextBlockType
}

data class TextBlockPlain(
    override val type: TextBlockType,
    val text: String
) : TextBlock

data class TextBlockCustomEmoji(
    override val type: TextBlockType,
    val displayImageUrl: String,
    val escapedString: String,
    val size: Size
) : TextBlock

data class TextBlockMention(
    override val type: TextBlockType,
    val displayString: String,
    val hiddenString: String
) : TextBlock

