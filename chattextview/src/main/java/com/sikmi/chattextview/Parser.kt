package com.sikmi.chattextview

public data class Size(
    val width: Float,
    val height: Float
)

public enum class TextBlockType {
    PLAIN, MENTION, CUSTOM_EMOJI
}

public interface TextBlock {
    val type: TextBlockType
}

public data class TextBlockPlain(
    override val type: TextBlockType,
    val text: String
) : TextBlock

public data class TextBlockCustomEmoji(
    override val type: TextBlockType,
    val displayImageUrl: String,
    val escapedString: String,
    val size: Size
) : TextBlock

public data class TextBlockMention(
    override val type: TextBlockType,
    val displayString: String,
    val hiddenString: String
) : TextBlock

