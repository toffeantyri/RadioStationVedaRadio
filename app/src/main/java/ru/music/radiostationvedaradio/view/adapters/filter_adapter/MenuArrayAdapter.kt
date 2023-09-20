package ru.music.radiostationvedaradio.view.adapters.filter_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.ChooseQualityItemBinding
import ru.music.radiostationvedaradio.utils.hide
import ru.music.radiostationvedaradio.utils.show
import ru.music.radiostationvedaradio.view.adapters.OnFilterClickListener


class MenuArrayAdapter(
    context: Context,
    private val list: MutableList<String>,
    onFilterClickListener: OnFilterClickListener,
    private val itemLayout: Int = R.layout.choose_quality_item,
) : ArrayAdapter<String>(context, itemLayout, list) {

    companion object {
        const val START_CHECKED_POS = 0
    }

    private var headerViewVisible = true
    private var arrowVisibility = true

    var checkedPosition = START_CHECKED_POS
    private val userFilterClickListener = OnItemClickAndTouchListener(onFilterClickListener)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getHeaderView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getItemView(position, convertView, parent)
    }

    private fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ChooseQualityItemBinding.bind(
            convertView ?: LayoutInflater.from(context)
                .inflate(itemLayout, parent, false)
        )

        with(binding) {
            root.isVisible = headerViewVisible
            arrowUp.isVisible = arrowVisibility
            arrowUp.rotation = 0f
            arrowUp.show()
            qualityName.text = list[position]
        }
        return binding.root
    }

    private fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ChooseQualityItemBinding.bind(
            convertView ?: LayoutInflater.from(context)
                .inflate(itemLayout, parent, false)
        )
        with(binding) {
            qualityName.text = list[position]
            if (position == checkedPosition) {
                qualityName.setTextColor(context.getColor(R.color.green_200))
            } else {
                qualityName.setTextColor(context.getColor(R.color.black))
            }
            if (position == 0) arrowUp.show() else arrowUp.hide()
            arrowUp.isVisible = arrowVisibility
        }
        return binding.root
    }

    fun getUserSelectionClickListener() = userFilterClickListener

    fun setHeaderViewVisibility(state: Boolean) {
        headerViewVisible = state
    }

    fun setArrowViewVisibility(state: Boolean) {
        arrowVisibility = state
    }
}
