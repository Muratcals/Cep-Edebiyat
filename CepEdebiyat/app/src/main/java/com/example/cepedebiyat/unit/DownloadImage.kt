package com.example.cepedebiyat.unit

import android.media.Image
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


fun CircleImageView.DownloadUrl(url:String){
    Picasso.get().load(url).into(this)
}