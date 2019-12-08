package com.mishenka.notbasic.detail

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {


    private var downloadState = 0

    private var url: String? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        Log.i("NYA", "Download state: $downloadState")

        intent?.extras?.let { safeExtras ->
            url = safeExtras.getString(getString(R.string.intent_url_extra))
            category = safeExtras.getString(getString(R.string.intent_category_extra))
            obtainHomeVM().setCurrentSearchAndUrl(category, url)
        }

        setupViews(url, category)
    }


    private fun setupViews(url: String?, category: String?) {
        if (url == null) return

        Glide.with(detail_photo_iv.context)
            .load(url)
            .into(detail_photo_iv)

        if (category == null) {
            detail_url_tv.text = getString(R.string.storage)
        } else {
            detail_url_tv.text = url
            Linkify.addLinks(detail_url_tv, Linkify.WEB_URLS)
        }

        setupDownloadButton(category)

        setupStarButton(url, category)
    }


    private fun setupDownloadButton(category: String?) {
        if (getExternalStoragePermission()) {
            detail_download_error_tv.visibility = View.INVISIBLE
            if (category == null) {
                detail_download_b.text = getString(R.string.detail_delete)
            } else {
                detail_download_b.text = getString(R.string.detail_download)
            }
            detail_download_b.setOnClickListener {
                switchDownload()
            }
        } else {
            detail_download_error_tv.text = getString(R.string.gallery_insufficient_permissions)
            detail_download_error_tv.visibility = View.VISIBLE
            detail_download_b.visibility = View.INVISIBLE
        }

    }


    private fun switchDownload() {
        detail_download_b.text = if (detail_download_b.text == getString(R.string.detail_delete)) {
            downloadState = -1
            getString(R.string.detail_download)
        } else {
            downloadState = 1
            getString(R.string.detail_delete)
        }
    }


    //TODO(This method should be written as an extension method just once, and not recopied 10 times)
    private fun getExternalStoragePermission() =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    //TODO("Star button can be implemented the same way as download button,
    // only doing its work after the activity is finished")
    private fun setupStarButton(url: String?, category: String?) {
        if (url == null || category == null) {
            detail_star_b.visibility = View.INVISIBLE
            return
        }
        val userId = obtainAuthVM().userId.value
        val homeVM = obtainHomeVM()
        if (userId == null) {
            with(detail_error_tv) {
                text = getString(R.string.star_auth_error)
                visibility = View.VISIBLE
            }
            detail_star_b.visibility = View.INVISIBLE
            homeVM.setCurrentFavAndCategoryId(null, null)
            return
        }

        detail_error_tv.visibility = View.INVISIBLE
        detail_star_b.visibility = View.VISIBLE

        //TODO("Change scope")
        GlobalScope.launch {
            with(homeVM) {
                setCurrentFavAndCategoryId(
                    getFavIdByUrl(url),
                    getFavSearchIdByCategory(category)
                )
            }
            val isAlreadyStarred = homeVM.isAlreadyStarred(userId)

            MainScope().launch {
                detail_star_b.text = if(isAlreadyStarred) {
                    getString(R.string.unstar)
                } else {
                    getString(R.string.star)
                }

                detail_star_b.setOnClickListener {
                    homeVM.toggleStar(userId, it as Button)
                }
            }
        }
    }


    override fun onPause() {
        if (downloadState == 1 && category != null && url != null) {
            obtainHomeVM().downloadPhoto(url!!)
        } else if (downloadState == -1 && category == null && url != null) {
            obtainHomeVM().deletePhoto(url!!)
        }

        super.onPause()
    }

}
