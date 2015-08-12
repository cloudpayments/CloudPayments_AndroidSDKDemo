package ru.cloudpayments.sdk.demo.buildIn;

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
import ru.cloudpayments.sdk.demo.Constants;
import ru.cloudpayments.sdk.demo.R;
import ru.cloudpayments.sdk.view.ChargeTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;

/**
 * Created by Nastya on 12.08.2015.
 */
public class BuildInActivity extends Activity {

    private ChargeTaskListener chargeTaskListener = new ChargeTaskListener() {

        @Override
        public void success(BaseResponse response) {
            log("BuildInActivity got success " + response);
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
            log("BuildInActivity got error " + response);
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
            log("BuildInActivity got cancel");
            ((TextView) findViewById(R.id.result)).setText(getString(R.string.operation_canceled) + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.build_in_form);
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

        Intent intent = new Intent(BuildInActivity.this, PaymentWidget.class);
        intent.putExtra("amount", amount);
        intent.putExtra("desc", desc);
        intent.putExtra("currency", Constants.currency);
        intent.putExtra("publicId", Constants.publicId);
        intent.putExtra("invoiceId", Constants.invoiceId);
        intent.putExtra("accountId", Constants.accountId);

        PaymentWidget.listener = chargeTaskListener;

        startActivity(intent);
    }
}
