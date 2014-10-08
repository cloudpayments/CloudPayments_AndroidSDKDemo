package ru.cloudpayments.sdk.demo.business.connector;

import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

import ru.cloudpayments.sdk.demo.business.Constants;
import ru.cloudpayments.sdk.demo.utils.Logger;

/**
 * Created by Nastya on 28.09.2014.
 */
public class CloudPaymentsSession {

    public DefaultHttpClient sHttpClient;

    private static CloudPaymentsSession ourInstance;

    public static CloudPaymentsSession getInstance() throws InitCloudPaymentsException {
        if (ourInstance == null) ourInstance = new CloudPaymentsSession();
        return ourInstance;
    }

    private CloudPaymentsSession() throws InitCloudPaymentsException {
        if (sHttpClient == null) {
            KeyStore trustStore;
            try {
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(new CPHostVerifier());
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));
                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                CredentialsProvider provider = new BasicCredentialsProvider();
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(Constants.publicId, Constants.passApi);
                provider.setCredentials(AuthScope.ANY, credentials);

                sHttpClient = new DefaultHttpClient(ccm, params);
                sHttpClient.setCredentialsProvider(provider);
            } catch (Exception e) {
                Logger.log(e);
            }
        }
    }
}
