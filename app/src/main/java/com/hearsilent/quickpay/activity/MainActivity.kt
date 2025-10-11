package com.hearsilent.quickpay.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.hearsilent.quickpay.R
import com.hearsilent.quickpay.callback.VersionCallback
import com.hearsilent.quickpay.databinding.ActivityMainBinding
import com.hearsilent.quickpay.libs.SelectableLinkMovementMethod
import com.hearsilent.quickpay.libs.SpanWithUnderline
import com.hearsilent.quickpay.libs.Utils
import com.hearsilent.quickpay.libs.helper.ApiHelper
import com.hearsilent.quickpay.models.VersionModel


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private var mHasNewVersion = false
    private var mLatestVersionModel: VersionModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpViews()
    }

    private fun setUpViews() {
        mBinding.button.setOnClickListener {
            if (!mHasNewVersion || mLatestVersionModel == null) {
                checkVersion()
            } else {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, mLatestVersionModel!!.downloadUrl.toUri())
                startActivity(browserIntent)
            }
        }

        checkVersion()
    }

    private fun checkVersion() {
        mBinding.groupContent.isInvisible = true
        mBinding.progressCircular.isVisible = true

        val version = Utils.getAppVersionName(this)
        ApiHelper.checkVersion(version, object : VersionCallback() {

            override fun onSuccess(current: VersionModel?, latest: VersionModel) {
                runOnUiThread {
                    if (isFinishing) return@runOnUiThread

                    mLatestVersionModel = latest

                    if (current == null) {
                        mBinding.textViewContent.text = getVersionSpannable(
                            R.string.current_version,
                            VersionModel("v$version", "", null)
                        )
                    } else {
                        mBinding.textViewContent.text =
                            getVersionSpannable(R.string.current_version, current)
                    }
                    mBinding.textViewContent.movementMethod = SelectableLinkMovementMethod.instance

                    if (latest.version == "v$version") {
                        mBinding.textViewDesc.setTextColor(getColor(R.color.textColorSecondary))
                        mBinding.textViewDesc.text = getString(R.string.no_update)

                        mBinding.button.setTextColor(getColor(R.color.textColorPrimary))
                        mBinding.button.setBackgroundColor(getColor(R.color.colorPrimary))
                        mBinding.button.text = getString(R.string.check_update)

                        mHasNewVersion = false
                    } else {
                        mBinding.textViewDesc.setTextColor(getColor(R.color.textColorPrimary))
                        mBinding.textViewDesc.text =
                            getVersionSpannable(R.string.latest_version, latest)
                        mBinding.textViewDesc.movementMethod = SelectableLinkMovementMethod.instance

                        mBinding.button.setTextColor(getColor(R.color.colorPrimary))
                        mBinding.button.setBackgroundColor(getColor(R.color.colorAccent))
                        mBinding.button.text = getString(R.string.update_now)

                        mHasNewVersion = true
                    }

                    mBinding.groupContent.isVisible = true
                    mBinding.progressCircular.isVisible = false
                }
            }

            override fun onFail() {
                runOnUiThread {
                    if (isFinishing) return@runOnUiThread

                    mLatestVersionModel = null

                    mBinding.textViewContent.text = getVersionSpannable(
                        R.string.current_version,
                        VersionModel("v$version", "", null)
                    )
                    mBinding.textViewContent.movementMethod = SelectableLinkMovementMethod.instance
                    mBinding.textViewDesc.text = getString(R.string.check_update_failed)

                    mBinding.button.setTextColor(getColor(R.color.textColorPrimary))
                    mBinding.button.setBackgroundColor(getColor(R.color.colorPrimary))
                    mBinding.button.text = getString(R.string.check_update)

                    mHasNewVersion = false

                    mBinding.groupContent.isVisible = true
                    mBinding.progressCircular.isVisible = false
                }
            }
        })
    }

    private fun getVersionSpannable(
        versionStringRes: Int,
        versionModel: VersionModel
    ): SpannableString {
        val spannable = SpannableString(
            getString(versionStringRes, versionModel.version) +
                    " (${getString(R.string.release_notes)})"
        )

        val releaseNotesSpan = object : SpanWithUnderline(getColor(R.color.colorAccent)) {
            override fun onClick(view: View) {
                AlertDialog.Builder(this@MainActivity, R.style.AlertDialogStyle)
                    .setTitle(R.string.release_notes).setMessage("${versionModel.releaseNotes}")
                    .setPositiveButton(R.string.got_it, null)
                    .show()
            }
        }
        spannable.setSpan(
            releaseNotesSpan,
            spannable.length - 5,
            spannable.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

}