package ru.kamaz.music.ui.category.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.kamaz.music.view_models.DialogViewModel
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider


class DialogAddPlaylistFragment: DialogFragment() {

    private lateinit var viewModel: DialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DialogViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val viewDialog= inflater.inflate(ru.kamaz.music.R.layout.dialog_add_playlist, null)
            builder.setView(viewDialog)

                .setPositiveButton("Заебись",
                    DialogInterface.OnClickListener { dialog, id ->
                        val editText: EditText = viewDialog.findViewById(ru.kamaz.music.R.id.etPlaylist)
                        viewModel.sendName(editText.text.toString())
                    })
                .setNegativeButton("Хуйня",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }



}