package ru.music.radiostationvedaradio.view.adapters.expandableList

class ExpandedMenuModel {
    private var iconName = ""
    private var iconImage = -1

    fun getIconName() = iconName

    fun setIconName(string: String) {
        this.iconName = string
    }

    fun getIconImage() = iconImage

    fun setIconImage(resourceId: Int){
        this.iconImage = resourceId
    }

}