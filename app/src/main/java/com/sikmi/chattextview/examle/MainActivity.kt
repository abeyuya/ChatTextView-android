package com.sikmi.chattextview.examle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.sikmi.chattextview.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ListView>(R.id.message_list)
        val adapter = ArrayAdapter<String>(
            applicationContext,
            R.layout.message_orange,
            mutableListOf("hello", "world")
        )
        list.adapter = adapter

        val editText = findViewById<EditText>(R.id.edittext_chatbox)

        val sendButton = findViewById<Button>(R.id.button_chatbox_send)
        sendButton.setOnClickListener {
            val text = editText.text.toString()
            editText.setText("")
            adapter.add(text)
            adapter.notifyDataSetChanged()
            Log.d("DEBUG", text)
            Log.d("DEBUG", adapter.count.toString())
        }
    }
}
