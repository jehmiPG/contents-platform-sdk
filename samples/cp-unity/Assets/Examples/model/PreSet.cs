using System;
using Jehmi.Unity.Setting;

namespace EnumExtension
{
    public class PreSetData
    {
        public string apiKey;
        public string appKey;
        public string amount;
        public string country;
        public string language;
        public string currency;
        public string timeZone;
        public string itemId;
        public string itemName;

		public PreSetData(string apiKey, 
                          string appKey, 
                          string amount, 
                          string country, 
                          string language, 
                          string currency, 
                          string timeZone, 
                          string itemId, 
		                  string itemName)
        {
            this.apiKey = apiKey;
            this.appKey = appKey;
            this.amount = amount;
            this.country = country;
            this.language = language;
            this.currency = currency;
            this.timeZone = timeZone;
            this.itemId = itemId;
            this.itemName = itemName;
        }
    }

    public static class Extensions
    {
        public static PreSetData getPreSet(this PreSet value) {
            PreSetData result = null;
            switch (value) {
                case PreSet.set_1:
                    result = new PreSetData(JehmiSetting.ApiKey, JehmiSetting.AppKey, "100000", "THA", "ENG", "THB", "UTC", "UnityItem1", "100 Unit");
                    break;
                default :
                    result = new PreSetData(JehmiSetting.ApiKey, JehmiSetting.AppKey, "100000", "THA", "ENG", "THB", "UTC", "UnityItem1", "100 Unit");
                    break;
            }
            return result;
        }
    }

    public enum PreSet { set_1 = 1 }
}
