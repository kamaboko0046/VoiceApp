package com.kamaboko.voiceapp.manege

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kamaboko.voiceapp.R
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.database.VoiceDatabase
import com.kamaboko.voiceapp.databinding.FragmentInputBinding
import com.kamaboko.voiceapp.databinding.FragmentManegeBinding
import com.kamaboko.voiceapp.input.InputFragmentDirections
import kotlinx.coroutines.launch

class ManageFragment: Fragment() {

    // Voiceデータ
    lateinit var voiceData: LiveData<List<Voice>>

    // DB
    lateinit var db: VoiceDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DBデータ設定
        val application = requireNotNull(this.activity).application
        db  = VoiceDatabase.getInstance(application)
        voiceData = db.voiceDao.getAll()

        // ViewModel設定
        val viewModelFactory = ManageViewModelFactory(db.voiceDao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory).get(ManageViewModel::class.java)

        // データバインド
        val binding: FragmentManegeBinding = FragmentManegeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = ManageVoiceAdapter {
            viewModel.delete(it)
            val mySnackbar = Snackbar.make(binding.root,
                "データを削除しました", Snackbar.LENGTH_SHORT)
            mySnackbar.show()
        }
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.VoiceList.addItemDecoration(itemDecoration)
        binding.VoiceList.adapter = adapter
        binding.VoiceList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // Menu設定
        setHasOptionsMenu(true)

        voiceData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        return binding.root
    }

    // Menu設定
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "キオク"
    }

    // Menuリスナー
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val action =
                    ManageFragmentDirections.actionManageFragmentToOutputFragment()
                findNavController().navigate(action)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}