package ru.music.radiostationvedaradio.view.adapters.filter_adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import ru.music.radiostationvedaradio.view.adapters.OnFilterClickListener

class OnItemClickAndTouchListener(private val onFilterClickListener: OnFilterClickListener) :
    AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private var isUserClick = false

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("MyLog", "LISTENER: ITEM SELECTED $position")
        if (isUserClick) {
            Log.d("MyLog", "LISTENER: ITEM SELECTED USER $position")
            onFilterClickListener.onItemFilterClick(position)
            isUserClick = false
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        isUserClick = true
        return false
    }
}