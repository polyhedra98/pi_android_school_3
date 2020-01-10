package com.mishenka.notbasic.data.content

import android.content.Context
import com.mishenka.notbasic.interfaces.IContentExtras


data class GalleryContentExtras(
    val context: Context,
    val page: Int
) : IContentExtras