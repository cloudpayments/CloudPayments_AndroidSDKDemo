package ru.cloudpayments.sdk.demo.business.domain.model.billing;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import ru.cloudpayments.sdk.demo.business.domain.model.BaseResponse;

/**
 * Created by Nastya on 28.09.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CardsChargeResponse extends BaseResponse {

    @JsonProperty("Model")
    public CardTransaction transaction;

    @Override
    public String toString() {
        return super.toString() + " CardsChargeResponse{" +
                "transaction=" + transaction +
                '}';
    }
}
