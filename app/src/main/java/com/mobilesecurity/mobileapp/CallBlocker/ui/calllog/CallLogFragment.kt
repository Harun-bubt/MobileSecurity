package com.mobilesecurity.mobileapp.CallBlocker.ui.calllog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilesecurity.mobileapp.R
import com.mobilesecurity.mobileapp.databinding.FragmentCallLogBinding
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.CallLogModel
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModel
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModelType
import com.mobilesecurity.mobileapp.adapter.CallLogAdapter
import com.mobilesecurity.mobileapp.adapter.HandleCallLogItemClick
import com.mobilesecurity.mobileapp.CallBlocker.util.openAppSettings
import com.mobilesecurity.mobileapp.viewmodel.ContactsViewModel
import com.mobilesecurity.commons.SystemPermissionUtil
import com.mobilesecurity.commons.util.ResourceState
import com.mobilesecurity.commons.util.gone
import com.mobilesecurity.commons.util.stringRes
import com.mobilesecurity.commons.util.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallLogFragment : Fragment(),
    HandleCallLogItemClick {

    @Inject
    internal lateinit var systemPermissionUtil: SystemPermissionUtil
    private lateinit var viewModel: ContactsViewModel
    private lateinit var binding: FragmentCallLogBinding
    private lateinit var recyclerViewAdapter: CallLogAdapter
    var mInterstitial: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCallLogBinding.inflate(inflater, container, false)
        viewModel = requireActivity().run {
            ViewModelProvider(this)[ContactsViewModel::class.java]
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        attachClickListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        checkForPermission()
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
//                } else {
//                    showGrantPermissionSnackBar(
//                        requireActivity().findViewById(R.id.rootLayout),
//                        permissions.toList()
//                    )
                }
            }
        }
    }

    override fun handleActionImageClick(position: Int, callLogModel: CallLogModel) {
        viewModel.blockContact(
            ContactModel(
                name = callLogModel.contactName,
                phoneNumber = callLogModel.contactNumber,
                contactModelType = ContactModelType.BLOCKED_CONTACT
            ),
            viewModel.blockedContactLiveData
        )

        mInterstitial = InterstitialAd(requireActivity())
        mInterstitial!!.setAdUnitId(getString(R.string.interstitial_ad_unit))
        mInterstitial!!.loadAd(AdRequest.Builder().build())
        mInterstitial!!.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded()
                if (mInterstitial!!.isLoaded()) {
                    mInterstitial!!.show()
                }
            }
        })

        showContactBlockedSnackBar(requireActivity().findViewById(R.id.rootLayout))
    }

    private fun setupObservers() {
        viewModel.callLogList.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.emptyCallLogTextView.visible()
                binding.recyclerView.gone()
            } else {
                binding.emptyCallLogTextView.gone()
                binding.recyclerView.visible()
                binding.recyclerView.layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(
                        context,
                        R.anim.recyclerview_layout_anim
                    )
                recyclerViewAdapter.setCallLogList(it)
            }
        })

        viewModel.blockedContactLiveData.observe(viewLifecycleOwner, Observer {
            if (it is ResourceState.Success) {
                checkForPermission()
            }
        })
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerViewAdapter =
            CallLogAdapter(
                requireContext(),
                this
            )
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun attachClickListeners() {
        binding.permissionRequiredLayout.permissionRequiredViewGroup.setOnClickListener {
            obtainPermission()
        }
    }

    private fun checkForPermission() {
        systemPermissionUtil.checkPermissions(
            requireContext(),
            getListOfRequiredPermissions()
        ).also {
            if (systemPermissionUtil.getMissingPermissionsArray(it).isNotEmpty()) {
                showGrantPermissionLayout()
            } else {
                viewModel.getCallLog()
                binding.permissionRequiredLayout.permissionRequiredViewGroup.gone()
                binding.recyclerView.visible()
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
        return mutableListOf(Manifest.permission.READ_CALL_LOG)
    }

    private fun showGrantPermissionLayout() {
        binding.apply {
            permissionRequiredLayout.permissionRequiredViewGroup.visible()
        }
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