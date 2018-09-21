package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.martichou.be.grateful.R
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideMainViewModelFactory(requireContext().applicationContext)).get(MainViewModel::class.java)
        binding = MainFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@MainFragment)
            this.notesList.adapter = viewModel.adapter
            this.hdl = this@MainFragment
            subscribeUi(viewModel.adapter)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main, menu)
    }

    private fun subscribeUi(adapter: NotesAdapter) {
        viewModel.notesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null) adapter.submitList(notes)
        })
    }

    /**
     * Open the bottomsheet
     */
    fun btnNewAction(v: View) {
        val bottomsheetFragment = BottomsheetFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }
}