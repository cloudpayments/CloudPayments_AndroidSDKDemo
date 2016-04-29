package ru.cloudpayments.sdk.demo.custom;

import java.lang.String; /**
 * Created by Nastya on 29.09.2014.
 */
public interface CardDataViewListener {

    void makeCharge(String cardNumber, String expDate, String cvv, String holderName, double amount, String currency, String desc);
    void makeAuth(String cardNumber, String expDate, String cvv, String holderName, double amount, String currency, String desc);

}
