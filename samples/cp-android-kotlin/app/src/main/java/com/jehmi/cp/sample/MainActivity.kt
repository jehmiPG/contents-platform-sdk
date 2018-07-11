package com.jehmi.cp.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jehmi.cp.*
import com.jehmi.cp.constant.StringKeys
import com.jehmi.cp.model.AppInfo
import com.jehmi.cp.sample.dummy.ApiService
import com.jehmi.cp.sample.dummy.ApiService.DummyPaymentInfo
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RequestCode = 1222
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = getUserId()

        registerUserIdButton.setOnClickListener {
            val hud = showProgressHUD()
            JCP.registerUserId(userId, object : ILoginCallback {
                override fun onSuccess() {
                    printLog("$userId is registered.")
                    hud.dismiss()
                }

                override fun onError(error: ErrorResult?) {
                    showToast("register is error: $error")
                    hud.dismiss()
                }
            })
        }

        purchaseItemButton.setOnClickListener {
            val amount = "10000"
            val country = "THA"
            val language = "ENG"
            val currency = "THB"

            val hud = showProgressHUD()

            ApiService.get().dummyPaymentInfo(
                    mapOf(StringKeys.userId to userId,
                            StringKeys.amount to amount,
                            StringKeys.country to country,
                            StringKeys.language to language,
                            StringKeys.currency to currency))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ info ->
                        info?.let { requestPayment(hud, userId, it) } ?: hud.dismiss()
                    }, {
                        showToast(it?.localizedMessage ?: "")
                        hud.dismiss()
                    })
        }

        applicationInfoButton.setOnClickListener {
            val hud = showProgressHUD()
            JCP.getAppInfo(object : IResultCallback<AppInfo> {
                override fun onSuccess(appInfo: AppInfo?) {
                    printLog("getAppInfo is success: $appInfo")
                    hud.dismiss()
                }

                override fun onError(error: ErrorResult?) {
                    showToast("getAppInfo is error: $error")
                    hud.dismiss()
                }
            })
        }

        downloadApkButton.setOnClickListener {
            val apkTitle = "Sample"
            val apkDesc = "CP Application"

            JCP.downloadApk(apkTitle, apkDesc, false, object : IDownloadCallback {
                override fun onDownloadComplete(uri: String?) {
                    showToast("Apk download success: $uri")
                }

                override fun onError(error: ErrorResult?) {
                    showToast("downloadApk is error: $error")
                }
            })
        }

        checkWriteExternalStoragePermission()
    }

    private fun getUserId(): String {
        val pref = getSharedPreferences("UserPref", MODE_PRIVATE)
        var userId = pref.getString("userId", null)
        if (userId == null) {
            userId = UUID.randomUUID().toString()
            pref.edit().putString("userId", userId).apply()
        }

        return userId
    }

    private fun requestPayment(hud: KProgressHUD, userId: String, paymentInfo: DummyPaymentInfo) {
        val itemId = "Item001"
        val itemName = "100 Ruby"
        val backUrl = ""
        val testMode = true

        JCP.purchase(this,
                paymentInfo.hashData,
                paymentInfo.jehmiAuthToken,
                paymentInfo.transactionId,
                userId,
                paymentInfo.amount,
                paymentInfo.country,
                paymentInfo.language,
                paymentInfo.currency,
                paymentInfo.timeZone,
                itemId,
                itemName,
                backUrl,
                object : IPurchaseCallback {
                    override fun onSuccess() {
                        printLog("purchase is success.")
                        hud.dismiss()
                    }

                    override fun onPending() {
                        showToast("purchase is pending.")
                        hud.dismiss()
                    }

                    override fun onError(error: ErrorResult?) {
                        showToast("purchase is error: $error")
                        hud.dismiss()
                    }
                }, testMode)
    }

    private fun checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mainLayout, R.string.permission_external_storage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { requestWriteExternalStoragePermission() }
                    .show()
        } else {
            requestWriteExternalStoragePermission()
        }
    }

    private fun requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RequestCode && grantResults.count() > 0) {
            val grantedStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (!grantedStoragePermission) {
                showToast("Please grant external storage permission.")
                runOnUiThread {
                    finish()
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun printLog(message: String) {
        logTextView.text = message
    }

    private fun showProgressHUD(): KProgressHUD {
        return KProgressHUD.create(this).show()
    }

}
