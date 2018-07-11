using System;
using Jehmi.Unity;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class Payment : MonoBehaviour {

    public Text description;
    public Button back;
	// Use this for initialization
	void Start () {
        drawDescription(null);
        AuthToken authToken = new AuthToken(PaymentParams.partnerAppId,
                                            PaymentParams.userId,
                                            PaymentParams.amount,
                                            PaymentParams.country,
                                            PaymentParams.language,
                                            PaymentParams.currency,
                                            new AuthToken.ResponseDelegate(onResponse));
        StartCoroutine(Api.Request(authToken));
	}
	
    private void onResponse(AuthResponse response, Exception e)
    {
        Debug.Log("++ onResponse() ");
        if (response != null)
        {
			Debug.Log("++ AuthResponse :  " + response);
            drawDescription(response);

            string userId = PaymentParams.userId;
            string itemId = PaymentParams.itemId;
            string itemName = PaymentParams.itemName;
            string backUrl = PaymentParams.backUrl;

            Debug.Log("++ request JCP.Purchase() ");
            JCP.Purchase(response.hashData, 
                         response.jehmiAuthToken, 
                         response.transactionId, 
                         userId, 
                         response.amount, 
                         response.country, 
                         response.currency, 
                         itemId,
                         itemName,
                         response.language,
                         response.timeZone,
                         backUrl,
                         new PurchaseCallback(onSuccess, onPending, onError),
                         PaymentParams.testMode);
        }
    }

    private void onSuccess()
    {
        Debug.Log("++ OnSuccess");
        JCP.ShowToast("++ onSuccess Purchase!");
        UnloadScene();
    }

    private void onPending()
    {
        Debug.Log("++ onPending");
        JCP.ShowToast("++ Purchase is get Pending status.");
        UnloadScene();
    }

    private void onError(ErrorResult error)
    {
        Debug.Log("++ onError : " + error.Message());
        JCP.ShowToast(error.Message());
        UnloadScene();
    }

    private void drawDescription(AuthResponse response) {
        string text = String.Format("PARTNER APP ID : {0}\n\nUSER ID : {1}\n\nAMOUNT : {2}\n\nCOUNTRY : {3}\n\nLANGUAGE : {4}\n\nCURRENCY : {5}\n\nITEM ID : {6}\n\nITEM NAME : {7}\n\nBACK URL : {8}\n\nTEST MODE : {9}", PaymentParams.partnerAppId, PaymentParams.userId, PaymentParams.amount, PaymentParams.country, PaymentParams.language, PaymentParams.currency, PaymentParams.itemId, PaymentParams.itemName, PaymentParams.backUrl, PaymentParams.testMode);

        if (response != null) {
            text += String.Format("\n\nHASHDATA : {0}\n\nJEHMI AUTH TOKEN : {1}\n\nTRANSACTION ID : {2}",
                                    response.hashData, response.jehmiAuthToken, response.transactionId);
        } else {
            text += "\n\n\nLoading...";
        }

        Debug.Log(text);
        description.text = text;
    }

    public void onClick(int id)
    {
        Debug.Log("++ onResponse() ");
        switch(id) {
            case 0 : // back
                UnloadScene();
                break;
        }
    }

    private void UnloadScene() {
        SceneManager.UnloadSceneAsync("PaymentScene");
    }
}
