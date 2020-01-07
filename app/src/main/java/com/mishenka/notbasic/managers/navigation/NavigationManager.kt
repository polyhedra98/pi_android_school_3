package com.mishenka.notbasic.managers.navigation

import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.children
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.fragments.HomeFragment
import com.mishenka.notbasic.fragments.MapFragment
import com.mishenka.notbasic.fragments.SettingsFragment
import com.mishenka.notbasic.general.ExtendedActivity
import com.mishenka.notbasic.interfaces.IFragmentAdditionalExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import org.koin.dsl.module


val navigationModule = module {
    single { NavigationManager() }
}


//TODO("By switching to a different root, the previous root's data is cleared.
// I am not yet sure if I want to keep it that way, might have to keep separate stacks for
// each root to preserve the entire hierarchy. Potentially implement in the future?")
class NavigationManager {

    private val TAG = "NavigationManager"


    private val singleChildFrame = R.layout.fragment_single_child

    private val twoChildrenFrame = R.layout.fragment_two_children

    private val singleChildRoot = R.id.fragment_single_child_root

    private val primaryChildRoot = R.id.fragment_primary_child


    private val requestsStack = RequestsStack()

    private var host: ExtendedActivity? = null

    private var mainFrame: FrameLayout? = null

    private var isPopulated = false


    private val totalStackSize: Int
        get() = requestsStack.totalCount

    private val primaryStackSize: Int
        get() = requestsStack.primaryCount

    private val topChildrenStackSize: Int?
        get() = requestsStack.peek()?.children?.size


    fun requestAddition(request: IFragmentRequest, additionalExtras: IFragmentAdditionalExtras?) {
        Log.i("NYA_$TAG", "Addition of fragment ${request.fragmentTag} " +
                "requested. Current stack size: $totalStackSize.")
        conditionallyHideToolbar(request)

        val extras = FragmentExtras(System.currentTimeMillis(), additionalExtras)
        var shouldForceRemove = false
        if (request.isSecondary) {
            addChildElement(RequestItem(extras, request))
        } else {
            shouldForceRemove =
                detectRedundantChildren(requestsStack.peek()?.children.isNullOrEmpty())
            requestsStack.add(RequestsStackItem(RequestItem(extras, request)))
        }

        setupViews(shouldRepopulate = false, forceRemoval = shouldForceRemove)

        val frameId = getFrameIdForRequest(request)
        host!!.supportFragmentManager.beginTransaction().run {
            replace(frameId, request.instantiateFragment(host!!, extras))
            commit()
        }
        conditionallyChangeTitle(request)
        Log.i("NYA_$TAG", "Fragment ${request.fragmentTag} added. Current stack size: " +
                "$totalStackSize. Primary size: $primaryStackSize.")
    }


    fun backPressed(isFromSecondary: Boolean) {
        val logDesc: String
        var forceRepopulation = false

        if (isFromSecondary) {
            logDesc = "secondary"
            requestsStack.peek()?.children?.pop()
        }
        else {

            when (host!!.resources.configuration.orientation) {

                Configuration.ORIENTATION_PORTRAIT -> {
                    if (!requestsStack.peek()?.children.isNullOrEmpty()) {
                        logDesc = "secondary"
                        requestsStack.peek()?.children?.pop()
                        forceRepopulation = true
                    }
                    else {
                        logDesc = "primary"
                        requestsStack.pop()
                    }
                }

                Configuration.ORIENTATION_LANDSCAPE -> {
                    logDesc = "primary"
                    requestsStack.pop()
                    if (!requestsStack.peek()?.children.isNullOrEmpty()) {
                        forceRepopulation = true
                    }
                }

                else -> {
                    Log.i("NYA_$TAG", "Error processing back press. Unsupported orientation.")
                    throw Exception("Unsupported orientation.")
                }
            }
        }

        if (primaryStackSize == 0) {
            host!!.finish()
            return
        }

        setupViews(forceRepopulation = forceRepopulation)

        Log.i("NYA_$TAG", "Back press handled for a $logDesc fragment. " +
                "Current stack size: $totalStackSize. Primary stack size: $primaryStackSize. " +
                "Top children stack size: $topChildrenStackSize")
    }


    fun navigationItemSelected(@IdRes itemId: Int) {
        requestsStack.clear()

        when (itemId) {
            R.id.home_nav_menu_item -> requestAddition(HomeFragment.HomeRequest, null)
            R.id.map_nav_menu_item -> requestAddition(MapFragment.MapRequest, null)
            R.id.settings_nav_menu_item -> requestAddition(SettingsFragment.SettingsRequest, null)
            else -> {
                Log.i("NYA_$TAG", "Error processing navigation action. No such id.")
                throw Exception("Error processing navigation action. No such id.")
            }
        }
    }


    fun requestInitialPopulation(request: IFragmentRequest, additionalExtras: IFragmentAdditionalExtras?) {
        if (!isPopulated) {
            requestAddition(request, additionalExtras)
                .also { isPopulated = true }
        }
    }


    fun conditionallyInitializeHost(activity: ExtendedActivity) {
        if (host == null) {
            host = activity
            mainFrame = host!!.findViewById(host!!.mainFrameId)

            setupViews()
        }
    }


    fun clear() {
        requestsStack.clear()
        isPopulated = false
    }


    fun removeHost() {
        host = null
        mainFrame = null
    }


    private fun setupViews(shouldRepopulate: Boolean = true,
                           forceRepopulation: Boolean = false,
                           forceRemoval: Boolean = false) {
        if (!validateHost()) {
            return
        }
        val lastItem = requestsStack.peek()
        if (lastItem == null) {
            Log.i("NYA_$TAG", "Requests stack is empty.")
            return
        }

        if (forceRemoval) {
            mainFrame!!.removeAllViews()
        }

        val replaced = if (lastItem.primaryItem.request.shouldBeDisplayedAlone) {
            conditionallyReplaceFrame(singleChildFrame, singleChildRoot)
        } else {
            conditionallyReplaceFrame(twoChildrenFrame, primaryChildRoot)
        }

        if ((replaced && shouldRepopulate) || forceRepopulation) {
            repopulate()
        }
    }


    private fun detectRedundantChildren(isChildrenStackEmpty: Boolean): Boolean {
        if (isChildrenStackEmpty) return false

        return host!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    private fun conditionallyHideToolbar(request: IFragmentRequest) {
        if (request.shouldBeDisplayedAlone && request.shouldHideToolbar) {
            host?.supportActionBar?.hide()
        } else {
            host?.supportActionBar?.show()
        }
    }


    private fun conditionallyChangeTitle(request: IFragmentRequest,
                                         secondaryRequest: IFragmentRequest? = null) {
        when (host!!.resources.configuration.orientation) {

            Configuration.ORIENTATION_PORTRAIT -> {
                if (secondaryRequest?.navigationTitleId != null) {
                    changeTitle(secondaryRequest.navigationTitleId)
                }
                else {
                    changeTitle(request.navigationTitleId)
                }
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                if (!request.isSecondary) {
                    changeTitle(request.navigationTitleId)
                }
            }

            else -> {
                Log.i("NYA_$TAG", "Error setting up title. Unsupported orientation.")
                throw Exception("Unsupported orientation.")
            }

        }
    }


    private fun changeTitle(@StringRes titleId: Int) {
        host?.supportActionBar?.setTitle(titleId)
    }


    private fun repopulate() {
        val lastPrimaryItem = requestsStack.peek()?.primaryItem
        if (lastPrimaryItem == null) {
            Log.i("NYA_$TAG", "Error repopulating. Last primary item is null")
            return
        }
        val lastPrimaryRequest = lastPrimaryItem.request
        val lastPrimaryExtras = lastPrimaryItem.extras

        val lastChildItem = requestsStack.peek()?.children?.peek()

        with(host!!.supportFragmentManager) {

            beginTransaction().run {
                replace(getFrameIdForRequest(lastPrimaryRequest),
                    lastPrimaryRequest.instantiateFragment(host!!, lastPrimaryExtras))
                commit()
            }

            if (lastChildItem == null) {
                Log.i("NYA_$TAG", "No child to repopulate.")
            } else {

                val lastChildRequest = lastChildItem.request
                val lastChildExtras = lastChildItem.extras

                beginTransaction().run {
                    replace(getFrameIdForRequest(lastChildRequest),
                        lastChildRequest.instantiateFragment(host!!, lastChildExtras))
                    commit()
                }
            }

        }
        conditionallyChangeTitle(lastPrimaryRequest, lastChildItem?.request)
    }


    private fun getFrameIdForRequest(request: IFragmentRequest): Int {
        if (request.shouldBeDisplayedAlone) {
            return mainFrame!!.children.first().id
        } else {

            when (host!!.resources.configuration.orientation) {

                Configuration.ORIENTATION_PORTRAIT -> {
                    return mainFrame!!.children.first().id
                }

                Configuration.ORIENTATION_LANDSCAPE -> {
                    return if (request.isSecondary) {
                        (mainFrame!!.children.first() as ViewGroup)
                            .children.last().id
                    } else{
                        (mainFrame!!.children.first() as ViewGroup)
                            .children.first().id
                    }
                }

                else -> {
                    Log.i("NYA_$TAG", "Error getting frame for request ${request.fragmentTag}. " +
                            "Unsupported orientation.")
                    throw Exception("Unsupported orientation.")
                }

            }
        }
    }


    private fun validateHost(): Boolean {

        if (!isHostInstantiated()) {
            Log.i("NYA_$TAG", "Error setting up views. Host is not instantiated.")
            return false
        }

        if (mainFrame == null) {
            Log.i("NYA_$TAG", "Error setting up views. Main content frame is null.")
            return false
        }

        return true
    }


    private fun isHostInstantiated(): Boolean {
        return if (host != null) {
            true
        } else {
            Log.i("NYA_$TAG", "Host is not instantiated")
            false
        }
    }


    private fun addChildElement(requestItem: RequestItem) {
        if (requestsStack.lastElement().children == null) {
            requestsStack.lastElement().children = ChildrenStack()
        }
        requestsStack.lastElement().children!!.add(requestItem)
    }


    private fun conditionallyReplaceFrame(@LayoutRes layoutId: Int, @IdRes rootId: Int): Boolean {
        if (mainFrame!!.findViewById<FrameLayout>(rootId) == null) {
            mainFrame!!.removeAllViews()
            View.inflate(host, layoutId, mainFrame)
            return true
        }
        return false
    }

}