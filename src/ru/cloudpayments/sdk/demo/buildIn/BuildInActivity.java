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
import ru.cloudpayments.sdk.view.PaymentTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;

/**
 * Created by Nastya on 12.08.2015.
 */
public class BuildInActivity extends Activity {

    private PaymentTaskListener paymentTaskListener = new PaymentTaskListener() {

        @Override
        public void success(BaseResponse response) {
            log("BuildInActivity got success " + response);
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
            log("BuildInActivity got error " + response);
            if (response instanceof CardsAuthConfirmResponse)
                showResult(getString(R.string.payment_not_finished) + ((CardsAuthConfirmResponse) response).transaction.cardHolderMessage + "\n");
            if (response instanceof CardsAuthResponse)
                showResult(getString(R.string.payment_not_finished) + ((CardsAuthResponse) response).auth.cardHolderMessage + "\n");
            else
                showResult(getString(R.string.error) + response.message + "\n");
        }

        @Override
        public void cancel() {
            log("BuildInActivity got cancel");
            showResult(getString(R.string.operation_canceled) + "\n");
        }
    };

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.build_in_form);
        resultView = (TextView) findViewById(R.id.result);
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
            showResult(savedInstanceState.getString("result"));
    }

    public void onPay(View view) {
        Double amount = 0.;
        try {
            amount = Double.parseDouble(((EditText) findViewById(R.id.edtAmount)).getText().toString());
        } catch (Exception e) {

            return;
        }

        String desc = ((EditText) findViewById(R.id.edtDesc)).getText().toString();
        String currency = ((EditText) findViewById(R.id.edtCurrency)).getText().toString();

        Intent intent = new Intent(BuildInActivity.this, PaymentWidget.class);
        intent.putExtra(PaymentWidget.EXTRA_AMOUNT, amount); // Сумма оплаты
        intent.putExtra(PaymentWidget.EXTRA_DESCRIPTION, desc); // Описание
        intent.putExtra(PaymentWidget.EXTRA_CURRENCY, currency); // Код валюты
        intent.putExtra(PaymentWidget.EXTRA_PUBLIC_ID, Constants.publicId); // Ваш public ID
        intent.putExtra(PaymentWidget.EXTRA_INVOICE_ID, Constants.invoiceId); // ID заказа в вашей системе
        intent.putExtra(PaymentWidget.EXTRA_ACCOUNT_ID, Constants.accountId); // ID покупателя в вашей системе
        intent.putExtra(PaymentWidget.EXTRA_DATA, "{\"age\":27,\"name\":\"Ivan\",\"phone\":\"+79998881122\"}"); // Произвольный набор параметров
        intent.putExtra(PaymentWidget.EXTRA_TYPE, PaymentWidget.TYPE_AUTH); // Тип платежа: TYPE_CHARGE (одностадийный) или TYPE_AUTH (двухстадийный)

        PaymentWidget.taskListener = paymentTaskListener;

        startActivity(intent);
    }

    public void showResult(String text){
        resultView.setText(text);
    }

}
