package ru.music.radiostationvedaradio.view.adapters.badadvice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroItemHolder

class AntiHoroAdapter : RecyclerView.Adapter<AntiHoroAdapter.HoroViewHolder>() {

    private var itemList : List<HoroItemHolder> = listOf()
    private val imageList : List<Int> = listOf() //todo добавить ссылка на картнки

    inner class HoroViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val itemName : MaterialTextView = view.findViewById(R.id.tv_name_zodiac)
        val itemImage : ImageView = view.findViewById(R.id.iv_logo_zodiac)
        val itemDescr : MaterialTextView = view.findViewById(R.id.tv_descr_zodiac)

        fun bindView(pos : Int){
            //itemImage.setImageResource(imageList[pos])
            itemName.text = itemList[pos].name
            itemDescr.text = itemList[pos].description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.horo_rv_item, parent, false)
        return HoroViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: HoroViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun fillListAdapter(list : List<HoroItemHolder>){
        itemList = list
        notifyDataSetChanged()
    }


}