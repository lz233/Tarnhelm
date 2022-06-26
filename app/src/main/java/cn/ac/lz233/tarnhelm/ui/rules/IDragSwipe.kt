package cn.ac.lz233.tarnhelm.ui.rules

interface IDragSwipe {
    fun onItemSwapped(fromPosition: Int, toPosition: Int)

    fun onItemDeleted(position: Int)

    fun onItemCopy(position: Int)
}