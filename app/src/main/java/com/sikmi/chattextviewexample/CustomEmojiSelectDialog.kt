package com.sikmi.chattextviewexample

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sikmi.chattextview.TextBlockCustomEmoji
import com.sikmi.chattextview.TextBlockType

class CustomEmojiSelectDialog: DialogFragment() {
    private lateinit var listener: CustomEmojiDialogListener

    interface CustomEmojiDialogListener {
        fun onCustomEmojiClick(customEmoji: TextBlockCustomEmoji)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val arr = arrayOf<CharSequence>(
                ":parrot: (gif)",
                ":octcat: (png)"
            )
            val url = listOf(
                "https://emoji.slack-edge.com/T02DMDKPY/parrot/2c74b5af5aa44406.gif",
                "https://emoji.slack-edge.com/T02DMDKPY/octocat/627964d7c9.png"
            )

            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("Custom Emoji")
                .setItems(arr) { _, which ->
                    val s = arr[which]
                    val e = TextBlockCustomEmoji(
                        type = TextBlockType.CUSTOM_EMOJI,
                        displayImageUrl = url[which],
                        escapedString = s.toString()
                    )
                    listener.onCustomEmojiClick(e)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as CustomEmojiDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement CustomEmojiDialogListener"))
        }
    }
}