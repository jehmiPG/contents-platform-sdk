//
// Created by Thomas Jeong on 2018. 4. 12..
//

#ifndef JEHMI_CPP_DEMO_APPINFO_H
#define JEHMI_CPP_DEMO_APPINFO_H

#include <string>

class AppInfo {
public:
    static std::shared_ptr<AppInfo> create(std::string partnerAppId,
                                           std::string packageName,
                                           std::string appVersionName,
                                           int appVersionCode,
                                           int minAppVersionCode,
                                           std::string requirementOsVersion);

    std::string getPartnerAppId();

    std::string getPackageName();

    std::string getAppVersionName();

    int getAppVersionCode();

    int getMinAppVersionCode();

    std::string getRequirementOsVersion();

private:
    AppInfo();

    std::string partnerAppId;
    std::string packageName;
    std::string appVersionName;
    int appVersionCode;
    int minAppVersionCode;
    std::string requirementOsVersion;
};


#endif //JEHMI_CPP_DEMO_APPINFO_H
