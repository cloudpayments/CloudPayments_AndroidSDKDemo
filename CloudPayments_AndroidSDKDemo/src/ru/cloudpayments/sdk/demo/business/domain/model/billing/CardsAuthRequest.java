package ru.cloudpayments.sdk.demo.business.domain.model.billing;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import ru.cloudpayments.sdk.demo.business.domain.model.BaseRequest;

/**
 * Created by Nastya on 28.09.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CardsAuthRequest extends BaseRequest {

    @JsonProperty("Amount")
    public double amount;

    @JsonProperty("Currency")
    public String currency;

    @JsonProperty("InvoiceId")
    public String invoiceId;

    @JsonProperty("Description")
    public String description;

    @JsonProperty("AccountId")
    public String accountId;

    @JsonProperty("Name")
    public String name;

    @JsonProperty("CardCryptogramPacket")
    public String cardCryptogramPacket;
}
