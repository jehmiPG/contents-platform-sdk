//
// Created by Thomas Jeong on 2018. 4. 12..
//

#ifndef JEHMI_CPP_DEMO_ERRORRESULT_H
#define JEHMI_CPP_DEMO_ERRORRESULT_H

#include <string>
#include <memory>

class ErrorResult {
public:
    static std::shared_ptr<ErrorResult> create(int code, std::string message);

    int getCode();
    std::string getMessage();

private:
    ErrorResult();

    int code;
    std::string message;
};


#endif //JEHMI_CPP_DEMO_ERRORRESULT_H
