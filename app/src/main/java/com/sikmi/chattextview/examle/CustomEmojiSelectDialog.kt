package com.sikmi.chattextview.examle

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sikmi.chattextview.module.Size
import com.sikmi.chattextview.module.TextTypeCustomEmoji
import com.sikmi.chattextview.module.TextTypeMention

class CustomEmojiSelectDialog: DialogFragment() {
    private lateinit var listener: CustomEmojiDialogListener

    interface CustomEmojiDialogListener {
        fun onCustomEmojiClick(customEmoji: TextTypeCustomEmoji)
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
                    val e = TextTypeCustomEmoji(
                        displayImageUrl = url[which],
                        escapedString = s.toString(),
                        size = Size(width = 17f, height = 17f)
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