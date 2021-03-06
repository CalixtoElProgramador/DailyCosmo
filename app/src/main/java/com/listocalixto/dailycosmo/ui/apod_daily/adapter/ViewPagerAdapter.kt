package com.listocalixto.dailycosmo.ui.apod_daily.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.listocalixto.dailycosmo.core.BaseViewHolder
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.databinding.ItemApodDailyBinding
import com.listocalixto.dailycosmo.ui.apod.adapter.APODDiffUtil

class ViewPagerAdapter(
    private var apodList: List<APOD>,
    private val itemClickListener: OnImageAPODClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnImageAPODClickListener {
        fun onImageClick(apod: APOD, itemBinding: ItemApodDailyBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            ItemApodDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewPagerViewHolder(itemBinding, parent.context)

        itemBinding.imgApodPicture.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onImageClick(apodList[position], itemBinding)
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewPagerAdapter.ViewPagerViewHolder -> {
                holder.bind(apodList[position])
            }
        }
    }

    override fun getItemCount(): Int = apodList.size

    fun setData(newAPODList: List<APOD>) {
        val diffUtil = APODDiffUtil(apodList, newAPODList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        apodList = newAPODList
        diffResults.dispatchUpdatesTo(this)
    }

    private inner class ViewPagerViewHolder(
        val binding: ItemApodDailyBinding,
        val context: Context
    ) :
        BaseViewHolder<APOD>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun bind(item: APOD) {
            if (item.hdurl.isEmpty()) {
                Glide.with(context).load(item.url).into(binding.imgApodPicture)
            } else {
                Glide.with(context).load(item.hdurl).into(binding.imgApodPicture)
            }
            binding.textApodTitle.text = item.title
            binding.textApodDate.text = item.date
            if (item.explanation.isEmpty()) {
                binding.textApodExplanation.text = "No description"
            } else {
                binding.textApodExplanation.text = item.explanation
            }
            if (item.copyright.isEmpty()) {
                binding.textApodCopyright.visibility = View.GONE
            } else {
                binding.textApodCopyright.text = "Copyright: ${item.copyright}"
            }
        }

    }

}