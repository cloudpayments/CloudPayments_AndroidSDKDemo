package ru.cloudpayments.sdk.demo.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import ru.cloudpayments.sdk.demo.R;

/**
 * Created by Nastya on 28.09.2014.
 */
public class CardDataView extends LinearLayout {

    private CardDataViewListener cardDataViewListener;
    private EditText edtCardNumber;
    private EditText edtCardExp;
    private EditText edtCardCvv;
    private EditText edtCardHolderName;
    private EditText edtAmount;
    private EditText edtCurrency;
    private EditText edtDesc;

    public CardDataView(Context context) {
        super(context);
    }

    public CardDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.card_data_view, this);

        setupView();
    }

    private boolean validate() {
        if (edtCardNumber.getText().length() < 16) {
            edtCardNumber.setError(getContext().getString(R.string.enter_card_number));
            return false;
        }
        if (edtCardExp.getText().length() < 4) {
            edtCardExp.setError(getContext().getString(R.string.enter_exp));
            return false;
        }
        if (edtCardCvv.getText().length() < 3) {
            edtCardCvv.setError(getContext().getString(R.string.enter_cvv));
            return false;
        }
        if (edtCardHolderName.getText().length() == 0) {
            edtCardHolderName.setError(getContext().getString(R.string.enter_card_holder_name));
            return false;
        }

        if (edtAmount.getText().length() == 0) {
            edtAmount.setError(getContext().getString(R.string.enter_amount));
            return false;
        }

        if (edtCurrency.getText().length() == 0) {
            edtCurrency.setError(getContext().getString(R.string.enter_currency));
            return false;
        }

        if (edtDesc.getText().length() == 0) {
            edtDesc.setError(getContext().getString(R.string.enter_desc));
            return false;
        }

        return true;
    }

    private void setupView() {
        edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
        edtCardExp = (EditText) findViewById(R.id.edtCardExp);
        edtCardCvv = (EditText) findViewById(R.id.edtCardCvv);
        edtCardHolderName = (EditText) findViewById(R.id.edtCardHolderName);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtCurrency = (EditText) findViewById(R.id.edtCurrency);

        findViewById(R.id.btnCharge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validate()) return;

                if (cardDataViewListener != null) {
                    cardDataViewListener.makeCharge(
                            edtCardNumber.getText().toString(),
                            edtCardExp.getText().toString(),
                            edtCardCvv.getText().toString(),
                            edtCardHolderName.getText().toString(),
                            Double.valueOf(edtAmount.getText().toString()),
                            edtCurrency.getText().toString(),
                            edtDesc.getText().toString()
                    );
                }
            }
        });
        findViewById(R.id.btnAuth).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validate()) return;

                if (cardDataViewListener != null) {
                    cardDataViewListener.makeAuth(
                            edtCardNumber.getText().toString(),
                            edtCardExp.getText().toString(),
                            edtCardCvv.getText().toString(),
                            edtCardHolderName.getText().toString(),
                            Double.valueOf(edtAmount.getText().toString()),
                            edtCurrency.getText().toString(),
                            edtDesc.getText().toString()
                    );
                }
            }
        });
    }

    public void setCardDataViewListener(CardDataViewListener cardDataViewListener) {
        this.cardDataViewListener = cardDataViewListener;
    }
}
