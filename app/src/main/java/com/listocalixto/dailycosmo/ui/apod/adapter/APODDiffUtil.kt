package com.listocalixto.dailycosmo.ui.apod.adapter

import androidx.recyclerview.widget.DiffUtil
import com.listocalixto.dailycosmo.data.model.APOD

class APODDiffUtil(private val oldList: List<APOD>, private val newList: List<APOD>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].date == newList[newItemPosition].date

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].date != newList[newItemPosition].date -> false
            oldList[oldItemPosition].title != newList[newItemPosition].title -> false
            oldList[oldItemPosition].explanation != newList[newItemPosition].explanation -> false
            oldList[oldItemPosition].media_type != newList[newItemPosition].media_type -> false
            oldList[oldItemPosition].copyright != newList[newItemPosition].copyright -> false
            oldList[oldItemPosition].hdurl != newList[newItemPosition].hdurl -> false
            oldList[oldItemPosition].url != newList[newItemPosition].url -> false
            else -> true
        }
    }
}