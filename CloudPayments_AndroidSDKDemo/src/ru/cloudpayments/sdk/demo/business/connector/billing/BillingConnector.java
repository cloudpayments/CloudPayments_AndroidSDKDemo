package ru.cloudpayments.sdk.demo.business.connector.billing;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import ru.cloudpayments.sdk.demo.business.Constants;
import ru.cloudpayments.sdk.demo.business.connector.CloudPaymentsSession;
import ru.cloudpayments.sdk.demo.business.connector.InitCloudPaymentsException;
import ru.cloudpayments.sdk.demo.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthConfirmRequest;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthRequest;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsChargeResponse;
import ru.cloudpayments.sdk.demo.utils.Logger;

public class BillingConnector {

    public CardsChargeResponse charge(String accountId, String invoiceId,
                                      String cardCryptogram, String cardHolderName,
                                      double amount, String currency, String desc) throws InitCloudPaymentsException {
        return (CardsChargeResponse) pay(accountId, invoiceId, cardCryptogram, cardHolderName,
                amount, currency, desc, "payments/cards/charge", CardsChargeResponse.class);
    }

    public CardsAuthResponse auth(String accountId, String invoiceId,
                                  String cardCryptogram, String cardHolderName,
                                  double amount, String currency, String desc) throws InitCloudPaymentsException {
        return (CardsAuthResponse) pay(accountId, invoiceId, cardCryptogram, cardHolderName,
                amount, currency, desc, "payments/cards/auth", CardsAuthResponse.class);
    }

    private BaseResponse pay(String accountId, String invoiceId,
                             String cardCryptogram, String cardHolderName,
                             double amount, String currency, String desc,
                             String method, Class<? extends BaseResponse> responseClass) {

        try {
            CardsAuthRequest cardsAuthRequest = new CardsAuthRequest();
            cardsAuthRequest.accountId = accountId;
            cardsAuthRequest.amount = amount;
            cardsAuthRequest.description = desc;
            cardsAuthRequest.currency = currency;
            cardsAuthRequest.invoiceId = invoiceId;
            cardsAuthRequest.name = cardHolderName;
            cardsAuthRequest.cardCryptogramPacket = cardCryptogram;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String requestJson = null;
            requestJson = objectMapper.writeValueAsString(cardsAuthRequest);
            Logger.log("JsonRequest: " + requestJson);

            HttpPost httpPost = new HttpPost(Constants.BASE_URL + method);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept-Charset", "UTF-8");

            httpPost.setEntity(new StringEntity(requestJson, "UTF-8"));
            HttpResponse response = CloudPaymentsSession.getInstance().sHttpClient.execute(httpPost);
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            Logger.log("ServerResponse: " + res);

            return objectMapper.readValue(res, responseClass);
        } catch (Exception e) {
            Logger.log(e);
        }

        return null;
    }

    /**
     * @param md
     * @param paRes
     * @return
     */
    public CardsAuthConfirmResponse payConfirm(String md, String paRes) {
        try {
            CardsAuthConfirmRequest cardsAuthConfirmRequest = new CardsAuthConfirmRequest();
            cardsAuthConfirmRequest.transactionId = Integer.valueOf(md);
            cardsAuthConfirmRequest.paRes = paRes;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String requestJson = null;
            requestJson = objectMapper.writeValueAsString(cardsAuthConfirmRequest);
            Logger.log("JsonRequest: " + requestJson);

            HttpPost httpPost = new HttpPost(Constants.BASE_URL + "payments/post3ds");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept-Charset", "UTF-8");

            httpPost.setEntity(new StringEntity(requestJson, "UTF-8"));
            HttpResponse response = CloudPaymentsSession.getInstance().sHttpClient.execute(httpPost);
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            Logger.log("ServerResponse: " + res);

            return objectMapper.readValue(res, CardsAuthConfirmResponse.class);

        } catch (Exception e) {
            Logger.log(e);
        }

        return null;
    }
}
