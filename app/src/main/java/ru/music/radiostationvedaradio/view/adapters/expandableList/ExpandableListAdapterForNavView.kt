package ru.music.radiostationvedaradio.view.adapters.expandableList

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import ru.music.radiostationvedaradio.R

class ExpandableListAdapterForNavView() : BaseExpandableListAdapter() {

    lateinit var context0: Context
    lateinit var listDataHeader: ArrayList<ExpandedMenuModel>
    lateinit var listDataChild: HashMap<ExpandedMenuModel, List<String>>
    lateinit var expandList: ExpandableListView

    constructor(
        context: Context,
        listDataHeader: ArrayList<ExpandedMenuModel>,
        listDataChild: HashMap<ExpandedMenuModel, List<String>>,
        view: ExpandableListView
    ) : this() {
        this.context0 = context
        this.listDataHeader = listDataHeader
        this.listDataChild = listDataChild
        this.expandList = view
    }


    override fun getGroupCount(): Int = listDataHeader.size


    override fun getChildrenCount(groupPosition: Int): Int {
        var childCount = 0
        if (groupPosition != 2) {
            childCount = this.listDataChild[this.listDataHeader[groupPosition]]?.size ?: 0
        }
        return childCount
    }


    override fun getGroup(groupPosition: Int): ExpandedMenuModel = listDataHeader[groupPosition]


    override fun getChild(groupPosition: Int, childPosition: Int): String? {
        return this.listDataChild[this.listDataHeader[groupPosition]]?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()


    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()


    override fun hasStableIds(): Boolean = false


    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val headerTitle: ExpandedMenuModel = getGroup(groupPosition)
        var convertView0 : View? = convertView
        if (convertView0 == null) {
            val inflater: LayoutInflater =
                this.context0.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView0 = inflater.inflate(R.layout.list_item_header, null)
        }
        val headerText : TextView? = convertView0?.findViewById<TextView>(R.id.tv_listitemheader)
        val headerIcon : ImageView? = convertView0?.findViewById<ImageView>(R.id.iv_listitemheader)

        headerText?.setTypeface(null, Typeface.BOLD)
        headerText?.text = headerTitle.getIconName()
        //headerIcon?.setImageResource(headerTitle.getIconImage())
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
            val inflater = this.context0.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView0 = inflater.inflate(R.layout.list_item_submenu, null)
        }

        val textChild : TextView? = convertView0?.findViewById(R.id.text_submenu)
        textChild?.text = childText
        return convertView0!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true



}
