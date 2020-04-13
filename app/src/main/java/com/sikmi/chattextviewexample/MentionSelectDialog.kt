package com.sikmi.chattextviewexample

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sikmi.chattextview.TextBlockMention
import com.sikmi.chattextview.TextBlockType

class MentionSelectDialog : DialogFragment() {
    private lateinit var listener: MentionDialogListener

    interface MentionDialogListener {
        fun onMentionClick(mention: TextBlockMention)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val arr = arrayOf<CharSequence>("@channel", "@joe")
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("Mention")
                .setItems(arr) { _, which ->
                    val s = arr[which]
                    val m = TextBlockMention(
                        type = TextBlockType.MENTION,
                        displayString = s.toString(),
                        metadata = ""
                    )
                    listener.onMentionClick(m)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as MentionDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement MentionDialogListener"))
        }
    }
}