package ru.music.radiostationvedaradio.ui.screens


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.FragmentMainBinding
import ru.music.radiostationvedaradio.ui.MainActivity
import ru.music.radiostationvedaradio.ui.viewmodel.ViewModelMainActivity
import ru.music.radiostationvedaradio.utils.TAG
import ru.music.radiostationvedaradio.utils.myLog

class MainFragment : Fragment() {

    private val mViewModel: ViewModelMainActivity by activityViewModels()
    lateinit var parentActivity: MainActivity
    lateinit var binding: FragmentMainBinding

    override fun onAttach(context: Context) {
        myLog("MainFragment onAttach")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        parentActivity = activity as MainActivity
        setUpOnClickStaticButton()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mViewModel.nounText.observe(this) {
            binding.tvTcitataDnya.text = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myLog("MainFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        myLog("MainFragment onResume")
        super.onResume()
        if (mViewModel.nounText.value.isNullOrEmpty()) loadNoun()
    }

    override fun onDestroy() {
        try {
            mViewModel.nounText.removeObservers(this)
        } catch (e: UninitializedPropertyAccessException) {
            Log.d(TAG, "${e.message}")
        }

        super.onDestroy()
    }


    private fun setUpOnClickStaticButton() {
        binding.btnRefreshTcitata.setOnClickListener {
            loadNoun()
        }
        binding.btnOpenHoro.setOnClickListener {
            findNavController().navigate(R.id.badAdviceFragment)
        }
    }

    private fun loadNoun() {
        setNounLoading(true)
        mViewModel.refreshTodayNoun() {
            setNounLoading(false)
        }
    }

    private fun setNounLoading(visible: Boolean) {
        val viewLoading = view?.findViewById<View>(R.id.noun_loading)
        when (visible) {
            true -> viewLoading?.visibility = View.VISIBLE
            false -> viewLoading?.visibility = View.GONE
        }
    }


}

