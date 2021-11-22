package com.yongchun.library.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Hani AlMomani on 23,April,2019
 */

@Parcelize
data class ImageItem(var imagePath: String, var source: ImageSource, var selected: Int) : Parcelable
