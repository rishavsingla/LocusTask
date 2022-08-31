package com.example.locustask.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewHolder<B : ViewBinding>(protected val binding: B) :
    RecyclerView.ViewHolder(binding.root)