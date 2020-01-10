package com.mishenka.notbasic.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.ContentType
import com.mishenka.notbasic.data.content.GalleryContentExtras
import com.mishenka.notbasic.data.content.GalleryContentResponse
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.content.ContentManager
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.android.ext.android.get


//TODO("Observe downloads to dynamically change master / detail if needed.")
class GalleryFragment : Fragment() {

    private val TAG = "GalleryFragment"


    private val EXT_STORAGE_PERM_RC = 1


    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }



    private fun setupViews() {

        if (!getExternalStoragePermissionState()) {
            //TODO("Implement 'Request permission.'")
            gallery_content_l.visibility = View.INVISIBLE
            gallery_error_l.visibility = View.VISIBLE
        }
        else if (getExternalStoragePermissionState()) {
            gallery_error_tv.visibility = View.INVISIBLE
            gallery_content_l.visibility = View.VISIBLE

            //initResultsFragment()

            fetchGallery()
        }

    }


    private fun initResultsFragment() {
        childFragmentManager.beginTransaction().run {
            replace(R.id.gallery_results_content_frame, ResultsFragment())
            commit()
        }
    }


    //TODO("Should be cached.")
    private fun fetchGallery(argPage: Int? = null) {
        val page = argPage ?: 1

        val observable = contentManager.requestContent(
            ContentType.GAL_TYPE,
            GalleryContentExtras(
                context!!,
                page
            )
        )

        //TODO("Ok, I've just realized that I have to remove observer, once data is fetched. Memory leak!")
        observable.observe(this, Observer {
            (it as? GalleryContentResponse?)?.let { response ->

                Log.i("NYA_$TAG", "Gallery response. List: ${response.galleryItemsList}, " +
                        "Total: ${response.totalPages}")

            }
        })
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



    object GalleryRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "GAL_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_gallery_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = GalleryFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }

}