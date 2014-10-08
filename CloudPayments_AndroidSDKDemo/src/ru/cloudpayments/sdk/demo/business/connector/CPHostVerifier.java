package ru.cloudpayments.sdk.demo.business.connector;

import org.apache.http.conn.ssl.AbstractVerifier;

import javax.net.ssl.SSLException;

public class CPHostVerifier extends AbstractVerifier {

    private String[] mAllowedHosts = new String[]{"api.cloudpayments.ru"};

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts)
            throws SSLException {
        for (int i = 0; i < mAllowedHosts.length; i++) {
            if (host == mAllowedHosts[i])
                return;
        }
        throw new SSLException("Invalid host!");
    }

}
