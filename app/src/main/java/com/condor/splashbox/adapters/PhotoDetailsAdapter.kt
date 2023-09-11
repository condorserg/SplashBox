package com.condor.splashbox.adapters

import androidx.recyclerview.widget.DiffUtil
import com.condor.splashbox.data.photo.Photo
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class PhotoDetailsAdapter(
    listener: OnItemClickListener
) : AsyncListDifferDelegationAdapter<Photo>(PhotoDiffUtilCallBack()) {

    init {
        delegatesManager.addDelegate(PhotoDetailsAdapterDelegate(listener))

    }

    class PhotoDiffUtilCallBack : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}