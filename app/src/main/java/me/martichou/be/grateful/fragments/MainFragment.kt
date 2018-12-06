package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var notesList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideMainViewModelFactory(requireContext().applicationContext)).get(MainViewModel::class.java)
        binding = MainFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@MainFragment)
        }

        postponeEnterTransition()

        binding.hdl = this

        notesList = binding.notesList

        notesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        notesList.addItemDecoration(DividerRV(40))
        notesList.setHasFixedSize(true)
        notesList.adapter = viewModel.adapter

        subscribeUi(viewModel.adapter)

        notesList.doOnLayout {
            startPostponedEnterTransition()
        }

        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.sharedimage_exit)

        return binding.root
    }

    private fun subscribeUi(adapter: NotesAdapter) {
        viewModel.notesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null){
                adapter.submitList(notes)
            }
        })
    }

    /**
     * Open the bottomsheet
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = BottomsheetFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }
}