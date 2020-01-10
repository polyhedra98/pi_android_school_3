package com.mishenka.notbasic.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.fragment.DetailFragmentData
import com.mishenka.notbasic.data.fragment.additional.DetailAdditionalExtras
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : Fragment() {

    private val TAG = "DetailFragment"


    private val EXT_STORAGE_PERM_RC = 1


    private val eventVM by sharedViewModel<EventVM>()

    private val prefVM by sharedViewModel<PrefVM>()

    private val preservationManager = get<PreservationManager>()


    private var fragmentId: Long? = null

    private var restoredData: DetailFragmentData? = null

    private var categoryToPreserve: String? = null

    private var urlToPreserve: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        categoryToPreserve = arguments?.getString(getString(R.string.bundle_category_key))
        urlToPreserve = arguments?.getString(getString(R.string.bundle_url_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? DetailFragmentData?)

        setupViews()
    }


    override fun onDestroyView() {
        preservationManager.preserveFragmentData(fragmentId!!,
            DetailFragmentData(
                category = categoryToPreserve,
                url = urlToPreserve
            )
        )

        super.onDestroyView()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            EXT_STORAGE_PERM_RC -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("NYA", "Permission has been denied")
                } else {
                    Log.i("NYA", "Permission has been accepted")
                }

                val url = arguments?.getString(getString(R.string.bundle_url_key))
                if (url == null) {
                    Log.i("NYA_$TAG", "Url is null. Can't set up button.")
                } else {
                    setupDownloadButton(url, true)
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }



    private fun setupViews() {

        var localCategory: String? = null
        var localUrl: String? = null

        (restoredData?.category ?: categoryToPreserve)?.let { category ->
            localCategory = category
            setupCategoryRelatedViews(category)
        }

        (restoredData?.url ?: urlToPreserve)?.let { url ->
            localUrl = url
            setupUrlRelatedViews(url)
        }

        if (localCategory != null && localUrl != null) {
            setupCategoryAndUrlRelatedViews(localCategory!!, localUrl!!)
        }

    }


    private fun setupCategoryRelatedViews(category: String) {
        detail_category_tv.text = getString(R.string.category_ui, category)
    }


    private fun setupUrlRelatedViews(url: String) {
        detail_url_tv.text = getString(R.string.url_ui, url)
        Linkify.addLinks(detail_url_tv, Linkify.WEB_URLS)

        Glide.with(detail_photo_iv)
            .load(url)
            .fitCenter()
            .into(detail_photo_iv)
    }


    private fun setupCategoryAndUrlRelatedViews(category: String, url: String) {

        prefVM.userId.observe(this, Observer {
            if (it == null) {
                detail_star_b.visibility = View.INVISIBLE
                detail_star_error_tv.visibility = View.VISIBLE
            } else {
                detail_star_b.visibility = View.VISIBLE
                detail_star_error_tv.visibility = View.INVISIBLE
                setupStarButton(it, category, url)
            }
        })

        setupDownloadButton(url)

    }


    //TODO("I feel like these results should be cashed, but I haven't experienced any performance issues.")
    private fun setupStarButton(userId: Long, category: String, url: String) {

        prefVM.isAlreadyStarred(userId, category, url).observe(this, Observer {
            if (it == false) {
                detail_star_b.text = getString(R.string.star_button_text)
                detail_star_b.setOnClickListener { view ->
                    prefVM.toggleStar(it, userId, category, url, {
                        (view as Button?)?.run {
                            isEnabled = false
                            isClickable = false
                        }
                    }, {
                        (view as Button?)?.run {
                            isEnabled = true
                            isClickable = true
                            setupStarButton(userId, category, url)
                        }
                    })
                }
            } else if (it == true) {
                detail_star_b.text = getString(R.string.unstar_button_text)
                detail_star_b.setOnClickListener { view ->
                    prefVM.toggleStar(it, userId, category, url, {
                        (view as Button?)?.run {
                            isEnabled = false
                            isClickable = false
                        }
                    }, {
                        (view as Button?)?.run {
                            isEnabled = true
                            isClickable = true
                            setupStarButton(userId, category, url)
                        }
                    })
                }
            }
        })

    }


    private fun setupDownloadButton(url: String, afterRequest: Boolean = false) {

        if (!getExternalStoragePermissionState() && afterRequest) {
            detail_download_b.visibility = View.INVISIBLE
            detail_download_error_tv.visibility = View.VISIBLE
        }
        else if (getExternalStoragePermissionState() && afterRequest) {
            prefVM.downloadPhoto(context!!, url, {
                (detail_download_b)?.run {
                    isEnabled = false
                    isClickable = false
                }
            }, {
                (detail_download_b)?.run {
                    isEnabled = true
                    isClickable = true
                }
            })
        }

        detail_download_b.setOnClickListener { view ->

            if (!getExternalStoragePermissionState()) {
                requestExternalStoragePermission()
            }
            else {
                detail_download_error_tv.visibility = View.INVISIBLE
                detail_download_b.visibility = View.VISIBLE

                prefVM.downloadPhoto(context!!, url, {
                    (view as Button?)?.run {
                        isEnabled = false
                        isClickable = false
                    }
                }, {
                    (view as Button?)?.run {
                        isEnabled = true
                        isClickable = true
                    }
                })
            }

        }

    }


    private fun getExternalStoragePermissionState() =
        ContextCompat.checkSelfPermission(
            context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestExternalStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            EXT_STORAGE_PERM_RC
        )
    }



    object DetailRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "DETAIL_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_detail_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = true

        override val shouldHideToolbar: Boolean
            get() = true

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = DetailFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                    (extras.additionalExtras as? DetailAdditionalExtras)?.run {
                        putString(context.getString(R.string.bundle_category_key), category)
                        putString(context.getString(R.string.bundle_url_key), url)
                    }
                }
            }

    }

}