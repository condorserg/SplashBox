package com.condor.splashbox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.condor.splashbox.App
import com.condor.splashbox.R
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.databinding.ItemPhotoDetailsBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class PhotoDetailsAdapterDelegate(
    private val listener: OnItemClickListener
) :
    AbsListItemAdapterDelegate<Photo, Photo, PhotoDetailsAdapterDelegate.PhotoHolder>() {

    class PhotoHolder(
        val binding: ItemPhotoDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo, listener: OnItemClickListener) {
            with(binding) {
                locationButton.setOnClickListener { listener.onLocationButtonClick(photo) }
                likesImageView.setOnClickListener { listener.onLikeButtonClick(photo) }
            }
            loadPhotoInfo(binding, photo)
            //photo
            Glide.with(itemView)
                .load(photo.urls.regular)
                .placeholder(R.drawable.ic_photo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_photo)
                .into(binding.photoDetailsImageView)
            //user's avatar
            Glide.with(itemView)
                .load(photo.user?.profile_image?.medium)
                .placeholder(R.drawable.ic_photo)
                .circleCrop()
                .into(binding.userImageView)
        }

    }

    override fun isForViewType(item: Photo, items: MutableList<Photo>, position: Int): Boolean {
        return true
    }
    override fun onCreateViewHolder(parent: ViewGroup): PhotoHolder {
        return PhotoHolder(ItemPhotoDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(item: Photo, holder: PhotoHolder, payloads: MutableList<Any>) {

        holder.bind(item, listener)

    }
}
fun loadPhotoInfo(binding: ItemPhotoDetailsBinding, photo: Photo) {
    with(binding) {
        userNameTextView.text = photo.user?.name
        likesTextView.text = photo.likes.toString()
        viewsTextView.text = photo.views.toString()
        downloadsTextView.text = photo.downloads.toString()
        with(App.appContext) {
            cameraTextView.text = getString(R.string.camera)
            resolutionTextView.text = getString(R.string.resolution)
            isoTextView.text = getString(R.string.iso)
            apertureTextView.text = getString(R.string.aperture)
            exposureTimeTextView.text = getString(R.string.exposure)
            focalLengthTextView.text = getString(R.string.focal_lenght)
            descriptionTextView.text = getString(R.string.description)
        }
        cameraDetailsTextView.text = photo.exif?.model.toString()
        resolutionDetailsTextView.text =
            photo.height.toString() + "x" + photo.width.toString()
        isoDetailsTextView.text = photo.exif?.iso.toString()
        apertureDetailsTextView.text = photo.exif?.aperture.toString()
        exposureTimeDetailsTextView.text = photo.exif?.exposure_time.toString()
        focalLengthDetailsTextView.text = photo.exif?.focal_length.toString()
        descriptionDetailsTextView.text = photo.description ?: photo.alt_description
        likesImageView.setImageResource(
            if (photo.liked_by_user == false) R.drawable.ic_favorite_border_24
            else R.drawable.ic_favorite
        )
    }
}
interface OnItemClickListener {
    fun onLocationButtonClick(item: Photo)
    fun onLikeButtonClick(item: Photo)
}

