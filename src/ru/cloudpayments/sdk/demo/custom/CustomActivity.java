package ru.cloudpayments.sdk.demo.custom;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import ru.cloudpayments.sdk.CardFactory;
import ru.cloudpayments.sdk.ICard;
import ru.cloudpayments.sdk.IPayment;
import ru.cloudpayments.sdk.PaymentFactory;
import ru.cloudpayments.sdk.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.demo.Constants;
import ru.cloudpayments.sdk.demo.R;
import ru.cloudpayments.sdk.view.PaymentTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;

public class CustomActivity extends Activity {

    private PaymentTaskListener paymentTaskListener;

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_activity);

        paymentTaskListener = new PaymentTaskListener() {

            @Override
            public void success(BaseResponse response) {
                log("CustomActivity got success " + response);
                if (response instanceof CardsAuthConfirmResponse)
                    showResult(getString(R.string.payment_finished) + ((CardsAuthConfirmResponse) response).transaction.cardHolderMessage + "\n");
                else if (response instanceof CardsAuthResponse) {
                    showResult(getString(R.string.payment_finished) + ((CardsAuthResponse) response).auth.cardHolderMessage + "\n");
                } else {
                    showResult(getString(R.string.payment_finished) + response + "\n");
                }
            }

            @Override
            public void error(BaseResponse response) {
                log("CustomActivity got error " + response);
                if (response instanceof CardsAuthConfirmResponse)
                    showResult(getString(R.string.payment_not_finished) + ((CardsAuthConfirmResponse) response).transaction.cardHolderMessage + "\n");
                if (response instanceof CardsAuthResponse)
                    showResult(getString(R.string.payment_not_finished) + ((CardsAuthResponse) response).auth.cardHolderMessage + "\n");
                else
                    showResult(getString(R.string.error) + response.message + "\n");
            }

            @Override
            public void cancel() {
                log("CustomActivity got cancel");
                showResult(getString(R.string.operation_canceled) + "\n");
            }
        };

        resultView = (TextView) findViewById(R.id.result);
        ((CardDataView) findViewById(R.id.cardDataView)).setCardDataViewListener(new CardDataViewListener() {
            @Override
            public void makeCharge(String cardNumber, String expDate, String cvv, String holderName, double amount, String currency, String desc) {

                ICard card = CardFactory.create(cardNumber, expDate, cvv);
                try {
                    String cardCryptogram = card.cardCryptogram(Constants.publicId);
                    if (card.isValidNumber()) {
                        IPayment paymentCharge = PaymentFactory.charge(CustomActivity.this,
                                Constants.publicId,
                                "accId",
                                "invId",
                                cardCryptogram,
                                holderName,
                                amount,
                                currency,
                                desc,
                                "{\"age\":27,\"name\":\"Ivan\",\"phone\":\"+79998881122\"}");
                        paymentCharge.run(paymentTaskListener);
                    } else {
                        showResult("CardNumber is not valid");
                    }
                } catch (Exception e) {
                    showResult("Error get card cryptogram");
                }
            }

            @Override
            public void makeAuth(String cardNumber, String expDate, String cvv, String holderName, double amount, String currency, String desc) {

                ICard card = CardFactory.create(cardNumber, expDate, cvv);
                try {
                    String cardCryptogram = card.cardCryptogram(Constants.publicId);
                    if (card.isValidNumber()) {
                        IPayment paymentAuth = PaymentFactory.auth(CustomActivity.this,
                                Constants.publicId,
                                "accId",
                                "invId",
                                cardCryptogram,
                                holderName,
                                amount,
                                currency,
                                desc,
                                "{\"age\":27,\"name\":\"Ivan\",\"phone\":\"+79998881122\"}");
                        paymentAuth.run(paymentTaskListener);
                    } else {
                        showResult("CardNumber is not valid");
                    }
                } catch (Exception e) {
                    showResult("Error get card cryptogram");
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = new Bundle();
        outState.putString("result", ((TextView) findViewById(R.id.result)).getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("result"))
            ((TextView) findViewById(R.id.result)).setText(savedInstanceState.getString("result"));
    }

    public void showResult(String text){
        resultView.setText(text);
    }
}
