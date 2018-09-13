package me.martichou.be.grateful.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val noteId = ShowFragmentArgs.fromBundle(arguments).noteId
        Log.i("ID:", noteId.toString())

        // Use InjectorUtils to inject the viewmodel
        val factory = InjectorUtils.provideShowViewModelFactory(requireActivity(), noteId)
        val showViewModel = ViewModelProviders.of(this, factory).get(ShowViewModel::class.java)
        val binding = DataBindingUtil.inflate<ShowFragmentBinding>(inflater, R.layout.show_fragment, container, false).apply {
            viewModel = showViewModel
            setLifecycleOwner(this@ShowFragment)
        }

        // Use this to bind onClick or other data binding from main_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
    }

    /**
     * Execute the insertion of
     * Hello into the db for testing purpose.
     */
    fun btnEditAction(v: View) {
        // TODO
    }
}