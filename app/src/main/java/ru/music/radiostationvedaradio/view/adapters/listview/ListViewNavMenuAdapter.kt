package ru.music.radiostationvedaradio.view.adapters.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.music.radiostationvedaradio.R

class ListViewNavMenuAdapter() : RecyclerView.Adapter<ListViewNavMenuAdapter.ItemHolder>() {

    val myList = arrayListOf<ListViewItemModel>()

    inner class ItemHolder(item: View): RecyclerView.ViewHolder(item) {
        private val text: TextView = item.findViewById(R.id.tv_list_view_item)
        private val image: ImageView = item.findViewById(R.id.iv_listview_item)


        fun bind(position: Int){
            text.text = "item test" //listItem.getTitle()

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_navmenu, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 3 //myList.size



}