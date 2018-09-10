package me.martichou.be.grateful.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = MainFragmentBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        // Use InjectorUtils to inject the viewmodel
        val factory = InjectorUtils.provideMainViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        // Fill in the recyclerview using our custom adapter
        val adapter = NotesAdapter()
        binding.notesList.adapter = adapter
        subscribeUi(adapter)

        // Use this to bind onClick or other data binding from main_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
    }

    private fun subscribeUi(adapter: NotesAdapter) {
        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null) adapter.submitList(notes)
        })
    }

    /**
     * Execute the insertion of
     * Hello into the db for testing purpose.
     */
    fun btnNewAction(v: View) {
        val 
        // TODO
        Log.i("btnNewAction", "Clicked")
    }

}
