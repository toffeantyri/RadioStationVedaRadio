package ru.music.radiostationvedaradio.ui.adapters.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.data.model.menus.SimpleMenuItem

class ListViewAdapter(list: List<SimpleMenuItem>) : BaseAdapter() {

    private var mListItems = list


    override fun getCount(): Int = mListItems.size

    override fun getItem(position: Int): Any = mListItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView0 = convertView
        if (convertView0 == null) {
            val inflater =
                parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView0 = inflater.inflate(R.layout.listview_item_navmenu, null)
        }
        val text = convertView0?.findViewById<TextView>(R.id.tv_list_view_item)
        val image = convertView0?.findViewById<ImageView>(R.id.iv_listview_item)

        text?.text = mListItems[position].title
        image?.setImageResource(mListItems[position].imageId)

        return convertView0!!


    }



}