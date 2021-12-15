package com.yongchun.library.adapter

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.yongchun.library.model.AlbumItem
import com.yongchun.library.model.ImageItem
import com.yongchun.library.model.ImageSource
import com.yongchun.library.utils.*

/**
 * Created by Hani AlMomani on 24,September,2019
 */


internal class ImagesDataSource(private val contentResolver: ContentResolver){

    fun loadAlbums(): ArrayList<AlbumItem> {
        val albumCursor = contentResolver.query(
            cursorUri,
            arrayOf(DISPLAY_NAME_COLUMN,MediaStore.Images.ImageColumns.BUCKET_ID),
            null,
            null,
            ORDER_BY
        )
        val list = arrayListOf<AlbumItem>()
        try {
            list.add(AlbumItem("All", true,"0"))
            if (albumCursor == null) {
                return list
            }
            albumCursor.doWhile {
                val bucketId = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))
                val name = albumCursor.getString(albumCursor.getColumnIndex(DISPLAY_NAME_COLUMN)) ?: bucketId
                var albumItem = AlbumItem(name, false, bucketId)
                if (!list.contains(albumItem)) {
                    list.add(albumItem)
                }
            }
        } finally {
            if (albumCursor != null && !albumCursor.isClosed) {
                albumCursor.close()
            }
        }
        return list
    }

    fun loadAlbumImages(
        albumItem: AlbumItem?,
        page: Int,
        pageSize: Int= PAGE_SIZE
    ): ArrayList<ImageItem> {
        val offset = page * PAGE_SIZE
        val list: ArrayList<ImageItem> = arrayListOf()
        var photoCursor: Cursor? = null
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val bundle = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                    putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, ORDER_BY)
                    putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                }

                if (albumItem == null || albumItem.isAll) {
                    photoCursor = contentResolver.query(
                        cursorUri,
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        bundle,
                        null
                    )
                } else {

                    val bundle = Bundle().apply {
                        putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                        putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                        putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, ORDER_BY)
                        putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                        putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.ImageColumns.BUCKET_ID} =?")
                        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(albumItem.bucketId))
                    }

                    photoCursor = contentResolver.query(
                        cursorUri,
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        bundle,
                        null
                    )
                }
            }else{
                if (albumItem == null || albumItem.isAll) {
                    photoCursor = contentResolver.query(
                        cursorUri,
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        null,
                        null,
                        null
                    )
                } else {
                    photoCursor = contentResolver.query(
                        cursorUri,
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        "${MediaStore.Images.ImageColumns.BUCKET_ID} =?",
                        arrayOf(albumItem.bucketId),
                        "$ORDER_BY LIMIT $pageSize OFFSET $offset"
                    )
                }
            }



            photoCursor?.isAfterLast ?: return list
            photoCursor.doWhile {
                val image = photoCursor.getString((photoCursor.getColumnIndex(PATH_COLUMN)))
                list.add(ImageItem(image, ImageSource.GALLERY, 0))
            }
        } finally {
            if (photoCursor != null && !photoCursor.isClosed()) {
                photoCursor.close()
            }
        }
        return list
    }
}