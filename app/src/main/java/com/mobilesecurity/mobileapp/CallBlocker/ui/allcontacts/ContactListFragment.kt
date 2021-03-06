package com.mobilesecurity.mobileapp.CallBlocker.ui.allcontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilesecurity.mobileapp.R
import com.mobilesecurity.mobileapp.databinding.FragmentContactListBinding
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModel
import com.mobilesecurity.mobileapp.adapter.ContactListAdapter
import com.mobilesecurity.mobileapp.adapter.HandleItemClick
import com.mobilesecurity.mobileapp.CallBlocker.util.openAppSettings
import com.mobilesecurity.mobileapp.viewmodel.ContactsViewModel
import com.mobilesecurity.commons.SystemPermissionUtil
import com.mobilesecurity.commons.util.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val MINIMUM_NUMBER_OF_SEARCH_CHARS = 2


@AndroidEntryPoint
class ContactListFragment : Fragment(), HandleItemClick {

    @Inject
    internal lateinit var systemPermissionUtil: SystemPermissionUtil

    private lateinit var binding: FragmentContactListBinding
    private lateinit var viewModel: ContactsViewModel
    private lateinit var recyclerViewAdapter: ContactListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
        attachClickListeners()
        observeUnBlockedContactList()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        checkForPermission()
    }

    override fun handleActionImageClick(position: Int, contactModel: ContactModel) {
        viewModel.blockContact(contactModel, viewModel.allContactsToShowLiveData)
        showContactBlockedSnackBar(requireActivity().findViewById(R.id.rootLayout))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            val permission = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                val showRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                if (!showRationale) {
                    showOpenSettingsSnackBar(requireActivity().findViewById(R.id.rootLayout))
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerViewAdapter =
            ContactListAdapter(
                requireContext(),
                this
            )
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun initViewModel() {
        viewModel = requireActivity().run {
            ViewModelProvider(this)[ContactsViewModel::class.java]
        }
    }

    private fun attachClickListeners() {
        binding.permissionRequiredLayout.permissionRequiredViewGroup.setOnClickListener {
            Log.d("${this.javaClass.name}", "layout clicked")
            obtainPermission()
        }
    }

    private fun observeUnBlockedContactList() {
        viewModel.allContactsToShowLiveData.observe(
            viewLifecycleOwner,
            Observer {
                handleDataUpdate(it)
            })
    }

    private fun initSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.findViewById<EditText>(R.id.search_src_text).apply {
            setHintTextColor(requireContext().getThemeColor(R.attr.colorAccent))
            setTextColor(requireContext().getThemeColor(R.attr.colorAccent))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length != null && newText.length > MINIMUM_NUMBER_OF_SEARCH_CHARS) {
                    viewModel.searchAllContacts(newText)
                } else if (newText == null || newText.isEmpty()) {
                    viewModel.closeSearch()
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.length != null && query.isNotEmpty()) {
                    viewModel.searchAllContacts(query)
                } else if (query == null || query.isEmpty()) {
                    viewModel.closeSearch()
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            viewModel.closeSearch()
            true
        }
    }

    private fun checkForPermission() {
        Log.d("${this.javaClass.name}", "check permission")
        systemPermissionUtil.checkPermissions(
            requireContext(),
            getListOfRequiredPermissions()
        ).let {
            systemPermissionUtil.getMissingPermissionsArray(it)
        }.also {
            if (it.isNotEmpty()) {
                Log.d("${this.javaClass.name}", "the permission list is ${it[0]}")
                showGrantPermissionLayout()
            } else {
                loadUnblockedContacts()
            }
        }
    }

    private fun obtainPermission() {
        systemPermissionUtil.checkPermissions(
            requireContext(),
            getListOfRequiredPermissions()
        ).run {
            systemPermissionUtil.getMissingPermissionsArray(this)
                .apply {
                    if (isNotEmpty()) {
                        requestPermissions(
                            this,
                            12
                        )
                    }
                }
        }
    }

    private fun getListOfRequiredPermissions(): List<String> {
        return mutableListOf(Manifest.permission.READ_CONTACTS)
    }

    private fun showGrantPermissionLayout() {
        binding.apply {
            permissionRequiredLayout.permissionRequiredViewGroup.visible()
            lottieLoader.gone()
            recyclerView.gone()
            stateView.gone()
            searchCardView.gone()
        }
    }

    private fun loadUnblockedContacts() {
        viewModel.getAllSavedContacts()
    }

    private fun handleDataUpdate(result: ResourceState<List<ContactModel>>) {
        when (result) {
            is ResourceState.Loading -> handleLoadingState()
            is ResourceState.Success -> handleSuccessState(result.data)
            is ResourceState.Error -> handleErrorState()
        }
    }

    private fun handleLoadingState() {
        binding.stateView.gone()
        binding.lottieLoader.playAnimation()
        binding.lottieLoader.visible()
        binding.recyclerView.gone()
        binding.searchView.gone()
        binding.permissionRequiredLayout.permissionRequiredViewGroup.gone()
    }

    private fun handleSuccessState(result: List<ContactModel>) {
        binding.lottieLoader.pauseAnimation()
        binding.lottieLoader.gone()
        binding.stateView.gone()
        binding.recyclerView.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.recyclerview_layout_anim
            )
        binding.searchView.visible()
        binding.recyclerView.visible()
        if (result.isEmpty()) {
            showEmptyState()
        }
        recyclerViewAdapter.setContactsList(result)
    }

    private fun handleErrorState() {
        binding.recyclerView.gone()
        binding.searchView.gone()
        binding.stateView.text = stringRes(R.string.something_went_wrong)
        binding.stateView.visible()
    }

    private fun showEmptyState() {
        binding.permissionRequiredLayout.permissionRequiredViewGroup.gone()
        binding.searchView.visible()
        binding.stateView.text = stringRes(R.string.contacts_on_your_contact_list_will_show_up_here)
        binding.stateView.visible()
    }

    private fun showOpenSettingsSnackBar(coordinatorLayout: CoordinatorLayout) {
        Snackbar.make(
            coordinatorLayout,
            stringRes(R.string.accept_permission_from_settings),
            Snackbar.LENGTH_LONG
        ).setAction(
            stringRes(R.string.open_settings)
        ) {
            requireActivity().openAppSettings()
        }.show()
    }

    private fun showContactBlockedSnackBar(coordinatorLayout: CoordinatorLayout) {
        Snackbar.make(
            coordinatorLayout,
            stringRes(R.string.contact_blocked),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}