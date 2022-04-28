package ru.music.radiostationvedaradio.view.adapters.listview

class ListViewItemModel {
    private var title = ""
    private var imageId = -1

    fun getTitle(): String = title

    fun getIconId(): Int = imageId

    fun setTitle(title: String){
        this.title = title
    }

    fun setIconId(imageResId: Int){
        this.imageId = imageResId
            }
}