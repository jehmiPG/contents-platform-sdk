using UnityEngine;
using UnityEngine.UI;

using Jehmi.Unity;
using EnumExtension;
using System;
using UnityEngine.SceneManagement;

public class Main : MonoBehaviour
{
    public InputField etApiKey;
    public InputField etAppKey;
    public InputField etHashData;
    public InputField etAuthToken;
    public InputField etTransactionId;
    public InputField etUserId;
    public InputField etAmount;
    public InputField etCountry;
    public InputField etLanguage;
    public InputField etCurrency;
    public InputField etTimeZone;
    public InputField etItemId;
    public InputField etItemName;
    public InputField etBackUrl;
    public Button btPreset;
    public Button btLogin;
    public Button btPayment;
    public Button btClearData;
    public Button btAppConfiguration;
    public Button btDownladApk;

    private void Awake()
    {
        Debug.Log("++ Awake");
        JCP.Debuggable(true);
        //JCP.Init();
    }

    // Update is called once per frame
    void Update()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            if (Input.GetKey(KeyCode.Escape))
            {
                Application.Quit();
            }
        }
    }

    public void Text_changed(string text)
    {
        if (etApiKey != null)
        {
            Debug.Log("etapikey text : " + etApiKey.text);
        }
    }

    public void OnClick(int id)
    {
        switch (id)
        {
            case 0: // Preset
                Debug.Log("++ Preset Clicked");
                setPreSet(PreSet.set_1);
                break;
            case 2: // Login
                Debug.Log("++ Login Clicked");
                new Login(onLoginComplete).request();
                break;
            case 3: // Payment
                Debug.Log("++ Payment Clicked");
                makePaymentParams();
                SceneManager.LoadScene("PaymentScene", LoadSceneMode.Additive);
                break;
            case 4: // Clear Datas
                Debug.Log("++ Clear Datas Clicked");
                clearAll();
                break;
            case 5: // get app configurations
                JCP.GetAppInfo(new ResultCallback<AppInfo>(onSuccess, onError));
                break;
            case 6: // download and install apk
                string title = "Unity Sample App";
                string description = "Update new Application";
                JCP.DownloadApk(title, description, false, new ErrorCallback(onErrorDelegate: onError));
                break;
        }
    }

    private void onSuccess(AppInfo appInfo)
    {
        JCP.ShowToast("++ onSuccess Get ApInfos");
        Debug.Log("++ OnSuccess : " + appInfo);
    }

    private void onError(ErrorResult error)
    {
        JCP.ShowToast("-- onError : " + error.Message());
    }

    private void onLoginComplete(User user, Exception e)
    {
        if (user != null)
        {
            Debug.Log("++ onLogin Success");
            PlayerPrefs.SetString(KeySet.userId, user.userId);
            etUserId.text = user.userId;
            JCP.RegisterUserId(user.userId, new LoginCallback(onSuccessRegisterUserId, onError));
        }

        if (e != null) {
            Debug.Log("++ onLogin Error");
            JCP.ShowToast("-- onError : " + e.Message);
        }
    }

    private void onSuccessRegisterUserId()
    {
        JCP.ShowToast("++ onSuccess RegisterUserId");
        Debug.Log("++ OnSuccess Register User id");
    }

    private void makePaymentParams()
    {
        PaymentParams.apiKey = etApiKey.text;
        PaymentParams.appKey = etAppKey.text;
        PaymentParams.userId = etUserId.text;
        PaymentParams.amount = etAmount.text;
        PaymentParams.country = etCountry.text;
        PaymentParams.language = etLanguage.text;
        PaymentParams.currency = etCurrency.text;
        PaymentParams.itemId = etItemId.text;
        PaymentParams.itemName = etItemName.text;
        PaymentParams.backUrl = etBackUrl.text;
    }

    private void clearAll()
    {
        PlayerPrefs.DeleteAll();

        etApiKey.text = String.Empty;
        etAppKey.text = String.Empty;
        etHashData.text = String.Empty;
        etAuthToken.text = String.Empty;
        etTransactionId.text = String.Empty;
        etAmount.text = String.Empty;
        etCountry.text = String.Empty;
        etLanguage.text = String.Empty;
        etCurrency.text = String.Empty;
        etTimeZone.text = String.Empty;
        etItemId.text = String.Empty;
        etItemName.text = String.Empty;
        etBackUrl.text = String.Empty;
    }

    private void setPreSet(PreSet preSet)
    {
        PreSetData data = preSet.getPreSet();
        etApiKey.text = data.apiKey;
        etAppKey.text = data.appKey;
        etAmount.text = data.amount;
        etCountry.text = data.country;
        etLanguage.text = data.language;
        etCurrency.text = data.currency;
        etTimeZone.text = data.timeZone;
        etItemId.text = data.itemId;
        etItemName.text = data.itemName;

        string userId = PlayerPrefs.GetString(KeySet.userId);
        if (userId != null)
        {
            etUserId.text = userId;
        }
    }
}
