package ru.cloudpayments.sdk.demo.business.domain.model.billing;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Nastya on 28.09.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CardTransaction {

    @JsonProperty("PublicId")
    public String publicId;

    @JsonProperty("TransactionId")
    public int transactionId;

    @JsonProperty("Amount")
    public Double amount;

    @JsonProperty("Currency")
    public String currency;

    @JsonProperty("CurrencyCode")
    public String currencyCode;

    @JsonProperty("PaymentAmount")
    public Double paymentAmount;

    @JsonProperty("PaymentCurrency")
    public String paymentCurrency;

    @JsonProperty("PaymentCurrencyCode")
    public int paymentCurrencyCode;

    @JsonProperty("InvoiceId")
    public String invoiceId;

    @JsonProperty("AccountId")
    public String accountId;

    @JsonProperty("Description")
    public String description;

    @JsonProperty("CardHolderMessage")
    public String cardHolderMessage;

    @Override
    public String toString() {
        return "CardTransaction{" +
                "publicId='" + publicId + '\'' +
                ", transactionId=" + transactionId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", paymentCurrency='" + paymentCurrency + '\'' +
                ", paymentCurrencyCode=" + paymentCurrencyCode +
                ", invoiceId='" + invoiceId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", description='" + description + '\'' +
                ", cardHolderMessage='" + cardHolderMessage + '\'' +
                '}';
    }
}
