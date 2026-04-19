package com.example.campuscircleapp.shared.services

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.campuscircleapp.R
import com.example.campuscircleapp.shared.enums.MessageSeverity
import com.google.android.material.card.MaterialCardView

object MessageService {
    fun show(
        context: Context,
        message: String,
        severity: MessageSeverity = MessageSeverity.INFO,
        durationInSeconds: Int = 3
    ) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.layout_custom_toast, null)

        val cardView = layout.findViewById<MaterialCardView>(R.id.toast_card)
        val iconView = layout.findViewById<ImageView>(R.id.toast_icon)
        val titleView = layout.findViewById<TextView>(R.id.toast_title)
        val messageView = layout.findViewById<TextView>(R.id.toast_message)

        messageView.text = message

        val (backgroundColor, iconRes, title) = when (severity) {
            MessageSeverity.SUCCESS -> Triple("#6FCF97", R.drawable.checkmark, "Success")
            MessageSeverity.ERROR -> Triple("#EB5757", android.R.drawable.ic_dialog_info, "Error")
            MessageSeverity.WARNING -> Triple("#F2C94C", android.R.drawable.ic_dialog_alert, "Warning")
            MessageSeverity.INFO -> Triple("#2F80ED", android.R.drawable.checkbox_on_background, "Info")
        }

        val color = Color.parseColor(backgroundColor)
        cardView.setCardBackgroundColor(color)
        iconView.setImageResource(iconRes)
        iconView.imageTintList = ColorStateList.valueOf(Color.WHITE)
        titleView.text = title

        val toast = Toast(context.applicationContext)
        // Align to Top Right
        toast.setGravity(Gravity.TOP or Gravity.END, 40, 100)
        toast.duration = if (durationInSeconds > 2) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
