package com.example.locustask.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.locustask.R
import com.example.locustask.databinding.ItemViewLocusChoiceBinding
import com.example.locustask.databinding.ItemViewLocusCommentBinding
import com.example.locustask.databinding.ItemViewLocusPhotoBinding
import com.example.locustask.models.LocusResponse
import com.example.locustask.models.LocusType

class LocusAdapter(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = emptyList<LocusResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_view_locus_photo -> LocusPhotoVH(layoutInflater, parent)
            R.layout.item_view_locus_choice -> LocusChoiceVH(layoutInflater, parent)
            else -> LocusCommentVH(layoutInflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is LocusPhotoVH -> holder.bind(item)
            is LocusChoiceVH -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            LocusType.PHOTO.name -> R.layout.item_view_locus_photo
            LocusType.SINGLE_CHOICE.name -> R.layout.item_view_locus_choice
            else -> R.layout.item_view_locus_comment
        }
    }

    private fun getItem(position: Int): LocusResponse {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    fun submitList(it: List<LocusResponse>) {
        list = it
        notifyDataSetChanged()
    }

    inner class LocusPhotoVH(layoutInflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder<ItemViewLocusPhotoBinding>(
            ItemViewLocusPhotoBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        ) {

        init {
            binding.ivPhoto.setOnClickListener {
                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener
                val item = getItem(position)
                if (item.imagePath.isEmpty()) {
                    itemClickListener.onPhotoItemClick(position)
                } else {
                    itemClickListener.openPhoto(item.imagePath)
                }
            }

            binding.ivClose.setOnClickListener {
                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener
                itemClickListener.onRemovePhoto(position)
            }
        }

        fun bind(item: LocusResponse) {
            binding.tvTitle.text = item.title
            Glide.with(itemView.context).load(item.imagePath)
                .placeholder(R.drawable.ic_add_photo).into(binding.ivPhoto)
            binding.ivClose.isVisible = item.imagePath.isNotEmpty()
        }

    }

    inner class LocusCommentVH(layoutInflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder<ItemViewLocusCommentBinding>(
            ItemViewLocusCommentBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        ) {

        init {
            binding.switchComment.setOnCheckedChangeListener { _, isChecked ->
                binding.etComment.isVisible = isChecked
            }

            binding.etComment.addTextChangedListener {
                val enteredData = it ?: return@addTextChangedListener
                val enteredText = enteredData.toString().trim()

                val item = getItem(adapterPosition)
                item.title = enteredText
            }
        }

    }

    inner class LocusChoiceVH(layoutInflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder<ItemViewLocusChoiceBinding>(
            ItemViewLocusChoiceBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        ) {

        fun bind(item: LocusResponse) {
            binding.tvTitle.text = item.title
            val options = item.dataMap.list
            binding.radioGroup.removeAllViews()
            for (option in options) {
                val radioButton = RadioButton(itemView.context)
                val id = options.indexOf(option)
                radioButton.id = id
                radioButton.text = option.text
                radioButton.isChecked = option.isChecked
                binding.radioGroup.addView(radioButton)
                radioButton.setOnClickListener {
                    itemClickListener.onOptionChoice(id, adapterPosition)
                }
            }
        }

    }


    interface ItemClickListener {
        fun onPhotoItemClick(position: Int)
        fun onRemovePhoto(position: Int)
        fun openPhoto(path: String?)
        fun onOptionChoice(checkedId: Int, itemPosition: Int)
    }
}