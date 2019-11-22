package com.mishenka.notbasic.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.Button
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var url: String? = null
        var category: String? = null
        intent?.extras?.let { safeExtras ->
            url = safeExtras.getString(getString(R.string.intent_url_extra))
            category = safeExtras.getString(getString(R.string.intent_category_extra))
            obtainHomeVM().setCurrentSearchAndUrl(category, url)
        }

        setupUrlRelatedViews(url)

        setupStarButton(url, category)
    }


    private fun setupUrlRelatedViews(url: String?) {
        if (url == null) return
        Glide.with(detail_photo_iv.context)
            .load(url)
            .into(detail_photo_iv)

        detail_url_tv.text = url
        Linkify.addLinks(detail_url_tv, Linkify.WEB_URLS)
    }


    private fun setupStarButton(url: String?, category: String?) {
        if (url == null || category == null) return
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

}
