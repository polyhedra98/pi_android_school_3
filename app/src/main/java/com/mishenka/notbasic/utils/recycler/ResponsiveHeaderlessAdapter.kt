package com.mishenka.notbasic.utils.recycler

import android.util.Log
import androidx.recyclerview.widget.RecyclerView


abstract class ResponsiveHeaderlessAdapter <T : RecyclerView.ViewHolder> (
    argItems: List<PagerElement>
) : RecyclerView.Adapter<T>() {

    abstract val TAG: String


    protected var items = argItems.toMutableList()


    open fun replaceItems(newItems: List<PagerElement>) {
        Log.i("NYA_$TAG", "Replacing items with $newItems.")
    }


    open fun removeItem(position: Int) {
        Log.i("NYA_$TAG", "Removing value $position.")
    }


    open fun addItem(newItem: PagerElement) {
        Log.i("NYA_$TAG", "Adding value $newItem")
    }


}