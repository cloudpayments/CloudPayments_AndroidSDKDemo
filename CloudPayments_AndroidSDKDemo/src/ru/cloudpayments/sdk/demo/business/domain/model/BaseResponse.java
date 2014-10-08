package ru.cloudpayments.sdk.demo.business.domain.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseResponse {

    @JsonProperty("InnerResult")
    public String innerResult;

    @JsonProperty("Success")
    public Boolean success;

    @JsonProperty("Message")
    public String message;

    public BaseResponse() {
    }

    public BaseResponse(Boolean success) {
        this.success = success;
    }

    public BaseResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "innerResult='" + innerResult + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}

