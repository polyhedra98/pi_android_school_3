package com.mishenka.notbasic.utils.recycler

import android.util.Log
import androidx.recyclerview.widget.RecyclerView


abstract class ResponsiveAdapter <T : RecyclerView.ViewHolder> (
    argItems: List<PagerElement>
) : ResponsiveHeaderlessAdapter<T>(argItems) {


    open fun replaceHeader(newHeader: PagerElement) {
        Log.i("NYA_$TAG", "Replacing header with $newHeader")
    }


}