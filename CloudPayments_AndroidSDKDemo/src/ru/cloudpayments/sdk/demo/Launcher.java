package ru.cloudpayments.sdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.cloudpayments.sdk.PaymentWidget;
import ru.cloudpayments.sdk.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsChargeResponse;
import ru.cloudpayments.sdk.view.ChargeTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;


public class Launcher extends Activity {

    /**
     * Тестовый publicId для подключения, его вам нужно получить в личном кабинете на сайте cloudpayments.ru
     */
    public static final String publicId = "pk_348c635ba69b355d6f4dc75a4a205";

    public static final String currency = "RUB";
    public static final String invoiceId = "testInvoiceId";
    public static final String accountId = "test_acc@mail.ru";

    private ChargeTaskListener chargeTaskListener = new ChargeTaskListener() {

        @Override
        public void success(BaseResponse response) {
            log("Launcher got success " + response);
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
            log("Launcher got error " + response);
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
            log("Launcher got cancel");
            ((TextView) findViewById(R.id.result)).setText("[Операция отменена]" + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
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

    public void onPay(View view) {
        Double amount = 0.;
        try {
            amount = Double.parseDouble(((EditText) findViewById(R.id.edtAmount)).getText().toString());
        } catch (Exception e) {
        }

        String desc = ((EditText) findViewById(R.id.edtDesc)).getText().toString();

        Intent intent = new Intent(Launcher.this, PaymentWidget.class);
        intent.putExtra("amount", amount);
        intent.putExtra("desc", desc);
        intent.putExtra("currency", currency);
        intent.putExtra("publicId", publicId);
        intent.putExtra("invoiceId", invoiceId);
        intent.putExtra("accountId", accountId);

        PaymentWidget.listener = chargeTaskListener;

        startActivity(intent);
    }
}
