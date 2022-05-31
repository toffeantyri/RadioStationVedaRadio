package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import ru.music.radiostationvedaradio.viewmodel.BadAdviceViewModel
import ru.music.radiostationvedaradio.R


class BadAdviceFragment : Fragment() {

    companion object {
        fun newInstance() = BadAdviceFragment()
    }

    private lateinit var viewModel: BadAdviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.bad_advice_fragment, container, false)
        viewModel = ViewModelProvider(this).get(BadAdviceViewModel::class.java)
        return view0
    }


}
