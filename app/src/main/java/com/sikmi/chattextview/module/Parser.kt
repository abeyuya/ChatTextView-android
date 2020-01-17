package com.sikmi.chattextview.module

data class Size(
    val width: Float,
    val height: Float
)

data class TextTypeCustomEmoji(
    val displayImageUrl: String,
    val escapedString: String,
    val size: Size
)

data class TextTypeMention(
    val displayString: String,
    val hiddenString: String
)