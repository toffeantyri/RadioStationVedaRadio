package ru.music.radiostationvedaradio.view.adapters.expandableList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.data.model.menus.ExpandableChildItem
import ru.music.radiostationvedaradio.data.model.menus.ExpandableMenuItem

class ExpandableListAdapterForNavView(
    private val context: Context,
    private val listDataHeader: List<ExpandableMenuItem>,
    private val listDataChild: HashMap<ExpandableMenuItem, List<ExpandableChildItem>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = listDataHeader.size


    override fun getChildrenCount(groupPosition: Int): Int {
        var childCount = 0
        if (groupPosition != 2) {
            childCount = this.listDataChild[this.listDataHeader[groupPosition]]?.size ?: 0
        }
        return childCount
    }


    override fun getGroup(groupPosition: Int): ExpandableMenuItem = listDataHeader[groupPosition]


    override fun getChild(groupPosition: Int, childPosition: Int): String? {
        val key = listDataHeader[groupPosition]
        val child = listDataChild[key]
        return child?.get(childPosition)?.linkName?.let { context.getString(it) }
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()


    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()


    override fun hasStableIds(): Boolean = false


    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val headerTitle: ExpandableMenuItem = getGroup(groupPosition)
        var convertView0: View? = convertView
        if (convertView0 == null) {
            val inflater: LayoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView0 = inflater.inflate(R.layout.ex_list_item_header, null)
        }
        val headerText : TextView? = convertView0?.findViewById<TextView>(R.id.tv_listitemheader)
        val headerIcon : ImageView? = convertView0?.findViewById<ImageView>(R.id.iv_listitemheader)

        headerText?.apply {
            text = headerTitle.iconName
        }
        headerIcon?.setImageResource(headerTitle.iconImageId)
        return convertView0!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childText = getChild(groupPosition, childPosition)
        var convertView0 = convertView

        if(convertView0 == null){
            val inflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView0 = inflater.inflate(R.layout.ex_list_item_submenu, null)
        }

        val textChild : TextView? = convertView0?.findViewById(R.id.text_submenu)
        textChild?.text = childText
        return convertView0!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true



}
