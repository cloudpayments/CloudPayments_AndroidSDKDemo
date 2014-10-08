package ru.cloudpayments.sdk.demo;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 *
 * Created by Nastya on 28.09.2014.
 */
public class CardDataView extends LinearLayout {

    private CardDataViewListener cardDataViewListener;

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

    private void setupView() {
        findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
                if (edtCardNumber.getText().length() < 16) {
                    edtCardNumber.setError(getContext().getString(R.string.enter_card_number));
                    return;
                }
                EditText edtCardExp = (EditText) findViewById(R.id.edtCardExp);
                if (edtCardExp.getText().length() < 4) {
                    edtCardExp.setError(getContext().getString(R.string.enter_exp));
                    return;
                }
                EditText edtCardCvv = (EditText) findViewById(R.id.edtCardCvv);
                if (edtCardCvv.getText().length() < 3) {
                    edtCardCvv.setError(getContext().getString(R.string.enter_cvv));
                    return;
                }
                EditText edtCardHolderName = (EditText) findViewById(R.id.edtCardHolderName);
                if (edtCardHolderName.getText().length() == 0) {
                    edtCardHolderName.setError(getContext().getString(R.string.enter_card_holder_name));
                    return;
                }

                EditText edtAmount = (EditText) findViewById(R.id.edtAmount);
                if (edtAmount.getText().length() == 0) {
                    edtAmount.setError(getContext().getString(R.string.enter_amount));
                    return;
                }

                EditText edtDesc = (EditText) findViewById(R.id.edtDesc);
                if (edtDesc.getText().length() == 0) {
                    edtDesc.setError(getContext().getString(R.string.enter_desc));
                    return;
                }

                if (cardDataViewListener != null) {
                    cardDataViewListener.makePayment(
                            edtCardNumber.getText().toString(),
                            edtCardExp.getText().toString(),
                            edtCardCvv.getText().toString(),
                            edtCardHolderName.getText().toString(),
                            Double.valueOf(edtAmount.getText().toString()),
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
