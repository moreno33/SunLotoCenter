package com.yongchun.library.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Hani AlMomani on 24,April,2019
 */
@Parcelize
internal data class AlbumItem(val name: String, val isAll: Boolean,val bucketId: String, var firstImagePath: String? = null) : Parcelable