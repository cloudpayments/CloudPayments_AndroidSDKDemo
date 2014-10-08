package ru.cloudpayments.sdk.demo.business.domain.model.billing;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Nastya on 28.09.2014.
 */
public class CardAuth {

    @JsonProperty("TransactionId")
    public int transactionId;

    @JsonProperty("PaReq")
    public String paReq;

    @JsonProperty("AcsUrl")
    public String acsUrl;
}
