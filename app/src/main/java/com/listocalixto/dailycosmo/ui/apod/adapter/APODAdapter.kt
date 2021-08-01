package com.listocalixto.dailycosmo.ui.apod.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.listocalixto.dailycosmo.core.BaseViewHolder
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.databinding.ItemApodBinding

class APODAdapter(
    private var apodList: List<APOD>,
    private val itemClickListener: OnAPODClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnAPODClickListener {
        fun onAPODClick(apod: APOD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            ItemApodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = APODViewHolder(itemBinding, parent.context)

        itemBinding.root.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onAPODClick(apodList[position])
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is APODViewHolder -> holder.bind(apodList[position])
        }
    }

    override fun getItemCount(): Int = apodList.size

    fun setData(newAPODList: List<APOD>) {
        val diffUtil = APODDiffUtil(apodList, newAPODList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        apodList = newAPODList
        diffResults.dispatchUpdatesTo(this)
    }

    private inner class APODViewHolder(val binding: ItemApodBinding, val context: Context) :
        BaseViewHolder<APOD>(binding.root) {
        override fun bind(item: APOD) {
            Glide.with(context).load(item.hdurl).centerCrop().into(binding.imageItemAPOD)
            binding.apply {
                titleItemAPOD.text = item.title
                dateItemAPOD.text = item.date
            }
        }
    }

}