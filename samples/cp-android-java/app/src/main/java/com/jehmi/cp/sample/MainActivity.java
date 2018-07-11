package com.jehmi.cp.sample;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jehmi.cp.ErrorResult;
import com.jehmi.cp.IDownloadCallback;
import com.jehmi.cp.IErrorCallback;
import com.jehmi.cp.ILoginCallback;
import com.jehmi.cp.IPurchaseCallback;
import com.jehmi.cp.IResultCallback;
import com.jehmi.cp.JCP;
import com.jehmi.cp.constant.StringKeys;
import com.jehmi.cp.model.AppInfo;
import com.jehmi.cp.sample.dummy.DummyApi;
import com.jehmi.cp.sample.dummy.DummyPaymentInfo;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int RequestCode = 2222;

    private TextView logTextView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.logTextView);
        userId = getUserId();

        findViewById(R.id.registerUserIdButton).setOnClickListener(this);
        findViewById(R.id.purchaseItemButton).setOnClickListener(this);
        findViewById(R.id.applicationInfoButton).setOnClickListener(this);
        findViewById(R.id.downloadApkButton).setOnClickListener(this);

        checkWriteExternalStoragePermission();
    }

    private String getUserId() {
        SharedPreferences pref = getSharedPreferences("UserPref", MODE_PRIVATE);
        String userId = pref.getString("userId", null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            pref.edit().putString("userId", userId).apply();
        }

        return userId;
    }

    private void checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(R.id.mainLayout), R.string.permission_external_storage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestWriteExternalStoragePermission();
                        }
                    })
                    .show();
        } else {
            requestWriteExternalStoragePermission();
        }
    }

    private void requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RequestCode && grantResults.length > 0) {
            boolean grantedStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (!grantedStoragePermission) {
                showToast("Please grant external storage permission.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void printLog(String message) {
        logTextView.setText(message);
    }

    private KProgressHUD showProgressHUD() {
        return KProgressHUD.create(this).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerUserIdButton: {
                final KProgressHUD hud = showProgressHUD();
                JCP.registerUserId(userId, new ILoginCallback() {
                    @Override
                    public void onSuccess() {
                        printLog(userId + " is registered.");
                        hud.dismiss();
                    }

                    @Override
                    public void onError(ErrorResult errorResult) {
                        showToast("register is error: " + errorResult);
                        hud.dismiss();
                    }
                });
                break;
            }

            case R.id.purchaseItemButton: {
                final String amount = "10000";
                final String country = "THA";
                final String language = "ENG";
                final String currency = "THB";

                final KProgressHUD hud = showProgressHUD();
                final Map<String, String> map = new HashMap<>();

                map.put(StringKeys.userId, userId);
                map.put(StringKeys.amount, amount);
                map.put(StringKeys.country, country);
                map.put(StringKeys.language, language);
                map.put(StringKeys.currency, currency);

                DummyApi.get().dummyPaymentInfo(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<DummyPaymentInfo>() {
                            @Override
                            public void accept(DummyPaymentInfo dummyPaymentInfo) {
                                requestPayment(hud, dummyPaymentInfo);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                showToast(throwable.getLocalizedMessage());
                                hud.dismiss();
                            }
                        });
                break;
            }

            case R.id.applicationInfoButton: {
                final KProgressHUD hud = showProgressHUD();
                JCP.getAppInfo(new IResultCallback<AppInfo>() {
                    @Override
                    public void onSuccess(AppInfo appInfo) {
                        printLog("getAppInfo is success: " + appInfo);
                        hud.dismiss();
                    }

                    @Override
                    public void onError(ErrorResult errorResult) {
                        showToast("getAppInfo is error: " + errorResult);
                        hud.dismiss();
                    }
                });
                break;
            }

            case R.id.downloadApkButton: {
                final String apkTitle = "Sample";
                final String apkDesc = "CP Application";

                JCP.downloadApk(apkTitle, apkDesc, false, new IDownloadCallback() {
                    @Override
                    public void onDownloadComplete(String uri) {
                        showToast("Apk download success: " + uri);
                    }

                    @Override
                    public void onError(ErrorResult errorResult) {
                        showToast("downloadApk is error: " + errorResult);
                    }
                });
                break;
            }
        }
    }

    private void requestPayment(final KProgressHUD hud, final DummyPaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            hud.dismiss();
            return;
        }

        final String itemId = "Item001";
        final String itemName = "100 Ruby";
        final String backUrl = "";
        final Boolean testMode = true;

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
                new IPurchaseCallback() {
                    @Override
                    public void onSuccess() {
                        printLog("purchase is success.");
                        hud.dismiss();
                    }

                    @Override
                    public void onError(ErrorResult error) {
                        showToast("purchase is error: " + error);
                        hud.dismiss();
                    }

                    @Override
                    public void onPending() {
                        showToast("purchase is pending.");
                        hud.dismiss();
                    }
                }, testMode);
    }
}
