package com.sikmi.chattextview

import android.text.style.ForegroundColorSpan

class MentionSpan: ForegroundColorSpan {

//    constructor(parcel: Parcel) : super(parcel)
    constructor(colorType: Int) : super(colorType)

//    val mentionId = UUID.randomUUID().toString()
}