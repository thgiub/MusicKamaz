package ru.kamaz.music.ui.dialog_windows

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class DialogBtSettings: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "Выбор есть всегда"
        val message = "Выбери пищу"
        val button1String = "Вкусная пища"
        val button2String = "Здоровая пища"
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(title) // заголовок
        builder.setMessage(message) // сообщение
        builder.setPositiveButton(button1String,
            DialogInterface.OnClickListener { dialog, id ->
                Toast.makeText(
                    activity, "Вы сделали правильный выбор",
                    Toast.LENGTH_LONG
                ).show()
            })
        builder.setNegativeButton(button2String,
            DialogInterface.OnClickListener { dialog, id ->
                Toast.makeText(activity, "Возможно вы правы", Toast.LENGTH_LONG)
                    .show()
            })
        builder.setCancelable(true)
        return builder.create()
    }
}