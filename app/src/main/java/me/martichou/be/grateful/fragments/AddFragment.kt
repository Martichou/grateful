package me.martichou.be.grateful.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.add_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.AddFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.currentTime
import me.martichou.be.grateful.utilities.randomNumber
import me.martichou.be.grateful.viewmodels.MainViewModel

class AddFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    val hasPhoto: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AddFragmentBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        // TODO Switch to AddFragment Viewmodel
        // Use InjectorUtils to inject the viewmodel
        val factory = InjectorUtils.provideMainViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        // Use this to bind onClick or other data binding from add_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
    }

    /**
     * Close this fragment and switch back to the previous one
     */
    fun btnCloseAction(v: View) {
        v.findNavController().popBackStack(R.id.main_fragment, false)
    }

    /**
     * Return the photo name if there is one
     * else, blank
     */
    fun photoOrNot(): String{
        return if(hasPhoto){
            randomNumber()
        } else {
            ""
        }
    }

    /**
     * Close this fragment and save info
     */
    fun btnSaveAction(v: View) {
        // TODO - REFER THE SAVE BUTTON and GET content of edit text
        val title: String = add_title_edit.text.toString()
        if (!title.isEmpty()) run {
            val n = Notes(title, add_content_edit.text.toString(), photoOrNot(), currentTime())
            viewModel.insertNote(n)
            v.findNavController().popBackStack(R.id.main_fragment, true)
        }
    }
}