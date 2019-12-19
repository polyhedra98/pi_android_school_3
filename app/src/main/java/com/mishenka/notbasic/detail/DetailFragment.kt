package com.mishenka.notbasic.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var downloadState = 0

    private var url: String? = null
    private var category: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail,
            container, false).also {
            arguments?.let { safeArguments ->
                url = safeArguments.getString(getString(R.string.intent_url_extra))
                category = safeArguments.getString(getString(R.string.intent_category_extra))
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).obtainHomeVM()
            .setCurrentSearchAndUrl(category, url)

        setupViews(url, category)
    }


    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onResume()
    }


    override fun onStop() {
        (activity as AppCompatActivity).supportActionBar?.show()
        super.onStop()
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
            detail_category_tv.text = category
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
            context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    //TODO("Star button can be implemented the same way as download button,
    // only doing its work after the activity is finished")
    private fun setupStarButton(url: String?, category: String?) {
        if (url == null || category == null) {
            detail_star_b.visibility = View.INVISIBLE
            return
        }
        val userId = (activity as AppCompatActivity).obtainAuthVM().userId.value
        val homeVM = (activity as AppCompatActivity).obtainHomeVM()
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
            (activity as AppCompatActivity).obtainHomeVM().downloadPhoto(url!!)
        } else if (downloadState == -1 && category == null && url != null) {
            (activity as AppCompatActivity).obtainHomeVM().deletePhoto(url!!)
        }

        super.onPause()
    }


    companion object {

        fun newInstance(context: Context? = null,
                        url: String? = null,
                        category: String? = null)
                = DetailFragment().apply {
            context?.let { safeContext ->
                arguments = Bundle().apply {
                    putString(safeContext.getString(R.string.intent_url_extra), url)
                    putString(safeContext.getString(R.string.intent_category_extra), category)
                }
            }
        }

    }

}