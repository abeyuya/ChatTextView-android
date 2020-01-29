package com.sikmi.chattextview

import android.content.Context
import android.graphics.drawable.Drawable
import com.sunhapper.x.spedit.gif.drawable.ProxyDrawable
import com.sunhapper.glide.drawable.DrawableTarget
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable


//
// https://github.com/sunhapper/SpEditTool/blob/master/app/src/main/java/me/sunhapper/spcharedittool/emoji/EmojiManager.kt
//
object EmojiManager {
    private val drawableCacheMap = HashMap<String, Drawable>()

    fun getDrawableByEmoji(context: Context, emoji: TextBlockCustomEmoji): Drawable {
        val drawable: Drawable
        if (drawableCacheMap.containsKey(emoji.displayImageUrl)) {
            drawable = drawableCacheMap[emoji.displayImageUrl]!!
        } else {
            drawable = getDrawable(context, emoji.displayImageUrl)
            drawableCacheMap[emoji.displayImageUrl] = drawable
        }
        return drawable
    }

    private fun getDrawable(context: Context, gifUrl: String): Drawable {
        val gifDrawable = context.getDrawable(R.drawable.emoji)
        val proxyDrawable = ProxyDrawable()

        Glide.with(context)
            .load(gifUrl)
            .placeholder(gifDrawable)
            .into(DrawableTarget(proxyDrawable))

        return proxyDrawable
    }
}