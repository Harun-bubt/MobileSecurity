package com.mobilesecurity.mobileapp.CallBlocker.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.mobilesecurity.mobileapp.CallBlocker.service.ForegroundKeepAppAliveService
import com.mobilesecurity.mobileapp.CallBlocker.ui.allcontacts.ContactListFragment
import com.mobilesecurity.mobileapp.CallBlocker.ui.blockedcontacts.BlockedContactsFragment
import com.mobilesecurity.mobileapp.CallBlocker.ui.calllog.CallLogFragment
import com.mobilesecurity.mobileapp.Constant.UpgradePro
import com.mobilesecurity.mobileapp.Internetconnection
import com.mobilesecurity.mobileapp.R
import com.mobilesecurity.mobileapp.base.BaseActivity
import com.mobilesecurity.mobileapp.databinding.ActivityCallblockerBinding

import com.iammert.library.readablebottombar.ReadableBottomBar
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.shashank.sony.fancydialoglib.Animation
import com.shashank.sony.fancydialoglib.FancyAlertDialog
import com.shashank.sony.fancydialoglib.Icon
import com.ypyproductions.utils.ApplicationUtils
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityCallblockerBinding
    private var mAdMobAdView: AdView? = null
    private val ads1: LinearLayout? = null
    var mInterstitial: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallblockerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActionBar()?.setDisplayHomeAsUpEnabled(true)
        startKeepAppAliveService()
        decorateViewPager()
        initBottomBarListener()
        checkshowint()

        var strPref = false
        val shf = getSharedPreferences("config", MODE_PRIVATE)
        strPref = shf.getBoolean(UpgradePro, false)

        if (strPref) {
            val ads1 = findViewById<View>(R.id.ads1) as LinearLayout
            ads1.visibility = View.GONE
        } else {
            if (Internetconnection.checkConnection(this)) {
                mAdMobAdView = findViewById<View>(R.id.admob_adview) as AdView
                val adRequest: AdRequest = AdRequest.Builder()
                    .build()
                mAdMobAdView!!.loadAd(adRequest)
                if (ApplicationUtils.isOnline(this)) {
                    val b = true
                    if (b) {

                    }
                }
            } else {
                val ads1 = findViewById<View>(R.id.ads1) as LinearLayout
                ads1.visibility = View.GONE
            }
        }
    }


    fun startKeepAppAliveService() {
        Intent(this, ForegroundKeepAppAliveService::class.java).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, it);
            } else {
                startService(it)
            }
        }
    }

    private fun decorateViewPager() {
        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add(R.string.blocked_contacts_fragment_name, BlockedContactsFragment::class.java)
                .add(
                    R.string.call_log_fragment_name,
                    CallLogFragment::class.java
                )
                .add(
                    R.string.unblocked_contacts_fragment_name,
                    ContactListFragment::class.java
                )
                .create()
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.bottomBar.selectItem(position)
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBottomBarListener() {
        binding.bottomBar.setOnItemSelectListener(object : ReadableBottomBar.ItemSelectListener {
            override fun onItemSelected(index: Int) {
                binding.viewPager.setCurrentItem(index, true)
            }
        })
    }

    fun checkshowint() {
        var strPref = false
        val shf = getSharedPreferences("config", MODE_PRIVATE)
        strPref = shf.getBoolean("callblockerint", false)
        if (strPref) {
        } else {
            showint()
        }
    }

    fun showint() {
        FancyAlertDialog.Builder(this)
            .setTitle("Call Blocker")
            .setBackgroundColor(Color.parseColor("#4076b2")) //Don't pass R.color.colorvalue
            .setMessage(applicationContext.resources.getString(R.string.callblockerint))
            .setPositiveBtnBackground(Color.parseColor("#FF4081")) //Don't pass R.color.colorvalue
            .setPositiveBtnText("Ok")
            .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8")) //Don't pass R.color.colorvalue
            .setNegativeBtnText("Cancel")
            .setAnimation(Animation.POP)
            .isCancellable(false)
            .setIcon(R.drawable.callblockericon, Icon.Visible)
            .OnPositiveClicked {
                val saveint = getSharedPreferences("config", MODE_PRIVATE)
                saveint.edit().putBoolean("callblockerint", true).apply()
            }
            .OnNegativeClicked {
                val saveint = getSharedPreferences("config", MODE_PRIVATE)
                saveint.edit().putBoolean("callblockerint", true).apply()
            }
            .build()
    }


}