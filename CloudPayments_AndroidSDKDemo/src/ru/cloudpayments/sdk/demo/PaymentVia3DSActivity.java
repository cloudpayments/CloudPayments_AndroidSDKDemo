package ru.cloudpayments.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ru.cloudpayments.sdk.Card;
import ru.cloudpayments.sdk.demo.business.Constants;
import ru.cloudpayments.sdk.demo.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthConfirmResponse;

/**
 * Created by Nastya on 29.09.2014.
 */
public class PaymentVia3DSActivity extends FragmentActivity implements ChargeTaskListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.payment);

        ((CardDataView) findViewById(R.id.cardDataView)).setCardDataViewListener(new CardDataViewListener() {
            @Override
            public void makePayment(String cardNumber, String expDate, String cvv, String holderName, double amount, String desc) {

                Card card = new Card(cardNumber, expDate, cvv);
                if (card.isValidNumber()) {
                    try {
                        new ChargeTask(PaymentVia3DSActivity.this, "accId", "invId",
                                card.cardCryptogram(Constants.publicId), holderName, amount, "RUB", desc,
                                "http://example.ru", PaymentVia3DSActivity.this).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PaymentVia3DSActivity.this, "CardNumber is not valid", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void error(BaseResponse response) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PaymentVia3DSActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.error));
        alertDialogBuilder
                .setMessage(response.message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void success(CardsAuthConfirmResponse response) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PaymentVia3DSActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.success));
        alertDialogBuilder
                .setMessage(response.toString())
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
