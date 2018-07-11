using System;
using Jehmi.Unity;

namespace EnumExtension
{
    public class PreSetData
    {
        public string partnerAppId;
        public string amount;
        public string country;
        public string language;
        public string currency;
        public string timeZone;
        public string itemId;
        public string itemName;

		public PreSetData(string partnerAppId,
                          string amount, 
                          string country, 
                          string language, 
                          string currency, 
                          string timeZone, 
                          string itemId, 
		                  string itemName)
        {
            this.partnerAppId = partnerAppId;
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
                    result = new PreSetData(JCP.PartnerAppId, "100000", "THA", "ENG", "THB", "UTC", "UnityItem1", "100 Unit");
                    break;
                default :
                    result = new PreSetData(JCP.PartnerAppId, "100000", "THA", "ENG", "THB", "UTC", "UnityItem1", "100 Unit");
                    break;
            }
            return result;
        }
    }

    public enum PreSet { set_1 = 1 }
}
