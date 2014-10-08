package ru.cloudpayments.sdk.demo.business.connector;

/**
 * Created by Galina on 06.10.14.
 */
public class InitCloudPaymentsException extends Exception {

    public InitCloudPaymentsException() {
        super("For get started you need to init CloudPaymentsSession");
    }
}
