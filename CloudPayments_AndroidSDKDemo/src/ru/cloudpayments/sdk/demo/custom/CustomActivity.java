package ru.cloudpayments.sdk.demo.custom;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import ru.cloudpayments.sdk.CardFactory;
import ru.cloudpayments.sdk.ChargeFactory;
import ru.cloudpayments.sdk.ICard;
import ru.cloudpayments.sdk.ICharge;
import ru.cloudpayments.sdk.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.demo.Constants;
import ru.cloudpayments.sdk.demo.R;
import ru.cloudpayments.sdk.view.ChargeTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;

/**
 * Created by Nastya on 12.08.2015.
 */
public class CustomActivity extends Activity {

    private ChargeTaskListener chargeTaskListener = new ChargeTaskListener() {

        @Override
        public void success(BaseResponse response) {
            log("CustomActivity got success " + response);
            if (response instanceof CardsAuthConfirmResponse)
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.payment_finished) + " " +
                        ((CardsAuthConfirmResponse) response).transaction.cardHolderMessage + "\n");
            else if (response instanceof CardsAuthResponse) {
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.payment_finished) + " " +
                        ((CardsAuthResponse) response).auth.cardHolderMessage + "\n");
            } else {
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.payment_finished) + " " + response + "\n");
            }
        }

        @Override
        public void error(BaseResponse response) {
            log("CustomActivity got error " + response);
            if (response instanceof CardsAuthConfirmResponse)
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.payment_not_finished) + " " +
                        ((CardsAuthConfirmResponse) response).transaction.cardHolderMessage + "\n");
            if (response instanceof CardsAuthResponse)
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.payment_not_finished) + " " +
                        ((CardsAuthResponse) response).auth.cardHolderMessage + "\n");
            else
                ((TextView) findViewById(R.id.result)).setText(getString(R.string.error) + " " + response.message + "\n");
        }

        @Override
        public void cancel() {
            log("CustomActivity got cancel");
            ((TextView) findViewById(R.id.result)).setText(getString(R.string.operation_canceled) + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_activity);

        ((CardDataView) findViewById(R.id.cardDataView)).setCardDataViewListener(new CardDataViewListener() {
            @Override
            public void makePayment(String cardNumber, String expDate, String cvv, String holderName, double amount, String desc) {

                ICard card = CardFactory.create(cardNumber, expDate, cvv);
                if (card.isValidNumber()) {
                    ICharge charge = ChargeFactory.create(CustomActivity.this,
                            Constants.publicId, "accId", "invId",
                            card.cardCryptogram(Constants.publicId),
                            holderName, amount, "RUB", desc,
                            "http://example.ru");
                    charge.run(chargeTaskListener);
                } else {
                    ((TextView) findViewById(R.id.result)).setText("CardNumber is not valid");
                }
            }
        });
    }
}
