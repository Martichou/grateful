package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowFragment : Fragment() {

    private lateinit var viewModel: ShowViewModel
    private lateinit var binding: ShowFragmentBinding

    private var noteId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowFragmentArgs.fromBundle(arguments).noteId
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideShowViewModelFactory(requireContext().applicationContext, noteId)).get(ShowViewModel::class.java)
        binding = ShowFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ShowFragment)
            this.hdl = this@ShowFragment
            this.showModel = viewModel
            executePendingBindings()
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.show, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_note -> {
                true
            }
            R.id.delete_note -> {

                LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.red)
                        .setButtonsColorRes(R.color.black)
                        .setIcon(R.drawable.ic_delete_white)
                        .setTitle("Take a second...")
                        .setMessage("Are you sure you really want to delete this note? After that you won't be able to recover it.")
                        .setPositiveButton(android.R.string.yes) {
                            this.findNavController().popBackStack()
                            viewModel.deleteNote(noteId)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
