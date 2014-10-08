package ru.cloudpayments.sdk.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Override;import java.lang.Runnable;import java.lang.String;import java.lang.Throwable;import java.lang.Void;import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.cloudpayments.sdk.demo.business.connector.InitCloudPaymentsException;
import ru.cloudpayments.sdk.demo.business.connector.billing.BillingConnector;
import ru.cloudpayments.sdk.demo.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.demo.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Nastya
 * Date: 05.10.14
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public class ChargeTask extends AsyncTask<String, Void, BaseResponse> {

    private ChargeTaskListener chargeTaskListener;

    private FragmentActivity activity;
    private String accountId;
    private String invoiceId;
    private String cardCryptogram;
    private String cardHolderName;
    private double amount;
    private String currency;
    private String desc;
    private String termUrl;

    private PrDialog prDialog;

    private ChargeTask() {
    }


    @Override
    protected void onPreExecute() {
        if (prDialog == null) prDialog = PrDialog.newInstance();

        prDialog.showDialog(activity);
    }

    public ChargeTask(FragmentActivity activity, String accountId, String invoiceId,
                      String cardCryptogram, String cardHolderName,
                      double amount, String currency, String desc, String termUrl,
                      ChargeTaskListener chargeTaskListener) {
        this.activity = activity;
        this.accountId = accountId;
        this.invoiceId = invoiceId;
        this.cardCryptogram = cardCryptogram;
        this.cardHolderName = cardHolderName;
        this.amount = amount;
        this.currency = currency;
        this.desc = desc;
        this.termUrl = termUrl;
        this.chargeTaskListener = chargeTaskListener;
    }

    @Override
    protected BaseResponse doInBackground(String... params) {
        try {
            return new BillingConnector().auth(accountId, invoiceId,
                    cardCryptogram, cardHolderName, amount, currency, desc);
        } catch (InitCloudPaymentsException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(BaseResponse result) {
        if (prDialog != null) prDialog.hideDialog();

        if (result != null && result.message == null) {
            if (result instanceof CardsAuthResponse) {
                CardsAuthResponse cardsAuthResponse = (CardsAuthResponse) result;
                if (cardsAuthResponse.auth != null) {
                    // Открываем форму 3ds
                    D3SDialog.newInstance(cardsAuthResponse.auth.acsUrl,
                            String.valueOf(cardsAuthResponse.auth.transactionId),
                            cardsAuthResponse.auth.paReq,
                            termUrl,
                            new D3SDialog.D3SDialogListener() {
                                @Override
                                public void onAuthorizationCompleted(String md, String paRes) {
                                    // Подтверждение транзакции
                                    new HttpPayConfirmTask().execute(md, paRes);
                                }

                                @Override
                                public void onAuthorizationFailed(int code, String message, String failedUrl) {
                                    // Транзакция отклонена
                                    if (chargeTaskListener != null)
                                        chargeTaskListener.error(new BaseResponse(false, "AuthorizationFailed: " + message));
                                }
                            }
                    ).showDialogAndAuthenticate(activity);
                }
            }
        } else {
            if (chargeTaskListener != null)
                chargeTaskListener.error(new BaseResponse(false, result != null ? result.message : "result is null"));

        }
    }

    private class HttpPayConfirmTask extends AsyncTask<String, Void, BaseResponse> {

        @Override
        protected void onPreExecute() {
            if (prDialog == null) prDialog = PrDialog.newInstance();

            prDialog.showDialog(activity);
        }

        @Override
        protected BaseResponse doInBackground(String... params) {
            return new BillingConnector().payConfirm(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(BaseResponse result) {
            if (prDialog != null) prDialog.hideDialog();

            if (result.success) {
                if (chargeTaskListener != null)
                    chargeTaskListener.success((CardsAuthConfirmResponse) result);
            } else {
                if (chargeTaskListener != null) chargeTaskListener.error(result);
            }
        }
    }

    private static class PrDialog extends DialogFragment {

        private ProgressBar progressBar;

        private Handler handler;

        public static PrDialog newInstance() {
            return new PrDialog();
        }

        public void showDialog(FragmentActivity activity) {
            if (activity.getCurrentFocus() != null) {
                activity.getCurrentFocus().clearFocus();
            }

            try {
                activity.getSupportFragmentManager().executePendingTransactions();
            } catch (Throwable err) {
                err.printStackTrace();
            }

            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.add(this, "prdialog");
            ft.commitAllowingStateLoss();
        }

        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(null);

            FrameLayout.LayoutParams paramsCenter = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            FrameLayout fl = new FrameLayout(getActivity());
            fl.setLayoutParams(paramsCenter);
            paramsCenter.gravity = Gravity.CENTER;
            progressBar = new ProgressBar(getActivity());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(params);
            fl.addView(progressBar);
            params.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(true);

            handler = new Handler();

            return fl;
        }

        public void hideDialog() {
            handler.post(new Runnable() {
                public void run() {
                    dismiss();
                }
            });
        }
    }

    private static class D3SDialog extends DialogFragment implements D3SViewAuthorizationListener {

        private D3SView authenticator;
        private ProgressBar progressBar;
        private String acs, md, pareq, postback;

        private D3SDialogListener authorizationListener;

        private Handler handler;

        public static D3SDialog newInstance(final String acsUrl, final String md, final String paReq, final String postbackUrl, D3SDialogListener listener) {
            D3SDialog dialog = new D3SDialog();

            dialog.acs = acsUrl;
            dialog.md = md;
            dialog.pareq = paReq;
            dialog.postback = postbackUrl;
            dialog.authorizationListener = listener;

            return dialog;
        }

        public void showDialogAndAuthenticate(FragmentActivity activity) {
            if (activity.getCurrentFocus() != null) {
                activity.getCurrentFocus().clearFocus();
            }

            try {
                activity.getSupportFragmentManager().executePendingTransactions();
            } catch (Throwable err) {
                err.printStackTrace();
            }

            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.add(this, "d3sdialog");
            ft.commitAllowingStateLoss();
        }

        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(null);

            FrameLayout.LayoutParams paramsCenter = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            FrameLayout fl = new FrameLayout(getActivity());
            fl.setLayoutParams(paramsCenter);
            paramsCenter.gravity = Gravity.CENTER;

            authenticator = new D3SView(getActivity());
            fl.addView(authenticator);
            progressBar = new ProgressBar(getActivity());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(params);
            fl.addView(progressBar);
            params.gravity = Gravity.CENTER;
            authenticator.setAuthorizationListener(this);

            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(true);

            handler = new Handler();

            return fl;
        }

        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (TextUtils.isEmpty(postback)) {
                authenticator.authorize(acs, md, pareq);
            } else {
                authenticator.authorize(acs, md, pareq, postback);
            }
        }

        public void onAuthorizationCompleted(final String md, final String paRes) {
            handler.post(new Runnable() {
                public void run() {
                    dismiss();
                    if (authorizationListener != null) {
                        authorizationListener.onAuthorizationCompleted(md, paRes);
                    }
                }
            });
        }

        public void onAuthorizationStarted(final D3SView view) {
            handler.post(new Runnable() {
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

        }

        public void onAuthorizationWebPageLoadingProgressChanged(final int progress) {
            handler.post(new Runnable() {
                public void run() {
                    progressBar.setVisibility(progress > 0 && progress < 100 ? View.VISIBLE : View.GONE);
                }
            });

        }

        public void onAuthorizationWebPageLoadingError(final int errorCode, final String description, final String failingUrl) {
            handler.post(new Runnable() {
                public void run() {
                    dismiss();
                    if (authorizationListener != null) {
                        authorizationListener.onAuthorizationFailed(errorCode, description, failingUrl);
                    }
                }
            });
        }


        interface D3SDialogListener {

            void onAuthorizationCompleted(final String md, final String paRes);

            void onAuthorizationFailed(final int code, final String message, final String failedUrl);
        }
    }

    private static class D3SView extends WebView {

        private static String JavaScriptNS = "D3SJS";

        private static Pattern mdFinder = Pattern.compile(".*?(<input[^<>]* name=\\\"MD\\\"[^<>]*>).*?", 32);

        private static Pattern paresFinder = Pattern.compile(".*?(<input[^<>]* name=\\\"PaRes\\\"[^<>]*>).*?", 32);

        private static Pattern valuePattern = Pattern.compile(".*? value=\\\"(.*?)\\\"", 32);

        private boolean urlReturned = false;

        private String postbackUrl = "https://www.google.com";

        private boolean postbackHandled = false;

        private D3SViewAuthorizationListener authorizationListener = null;


        public D3SView(final Context context) {
            super(context);
            initUI();
        }

        public D3SView(final Context context, final AttributeSet attrs) {
            super(context, attrs);
            initUI();
        }

        public D3SView(final Context context, final AttributeSet attrs, final int defStyle) {
            super(context, attrs, defStyle);
            initUI();
        }

        public D3SView(final Context context, final AttributeSet attrs, final int defStyle, final boolean privateBrowsing) {
            super(context, attrs, defStyle);
            initUI();
        }

        private void initUI() {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setBuiltInZoomControls(true);
            getSettings().setSupportZoom(true);
            addJavascriptInterface(new D3SJSInterface(), JavaScriptNS);

            setWebViewClient(new WebViewClient() {

                public void onPageStarted(WebView view, String url, Bitmap icon) {
                    if (!urlReturned && !postbackHandled) {
                        if (url.toLowerCase().contains(postbackUrl.toLowerCase())) {
                            postbackHandled = true;
                            view.loadUrl(String.format("javascript:window.%s.processHTML(document.getElementsByTagName('html')[0].innerHTML);", JavaScriptNS));
                            urlReturned = true;
                        } else {
                            super.onPageStarted(view, url, icon);
                        }
                    }
                }

                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    if (!postbackHandled && url.toLowerCase().contains(postbackUrl.toLowerCase())) {
                        postbackHandled = true;
                        view.loadUrl(String.format("javascript:window.%s.processHTML(document.getElementsByTagName('html')[0].innerHTML);", JavaScriptNS));
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (!failingUrl.startsWith(postbackUrl)) {
                        authorizationListener.onAuthorizationWebPageLoadingError(errorCode, description, failingUrl);
                    }
                }

                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                }
            });

            setWebChromeClient(new WebChromeClient() {

                public void onProgressChanged(WebView view, int newProgress) {
                    if (authorizationListener != null) {
                        authorizationListener.onAuthorizationWebPageLoadingProgressChanged(newProgress);
                    }
                }
            });
        }

        private void completeAuthorization(String html) {
            String md = "";
            String pares = "";

            Matcher localMatcher1 = mdFinder.matcher(html);
            Matcher localMatcher2 = paresFinder.matcher(html);

            if (localMatcher1.find()) {
                md = localMatcher1.group(1);
            }

            if (localMatcher2.find()) {
                pares = localMatcher2.group(1);
            }

            if (!TextUtils.isEmpty(md)) {
                Matcher valueMatcher = valuePattern.matcher(md);
                if (valueMatcher.find()) {
                    md = valueMatcher.group(1);
                }
            }

            if (!TextUtils.isEmpty(pares)) {
                Matcher valueMatcher = valuePattern.matcher(pares);
                if (valueMatcher.find()) {
                    pares = valueMatcher.group(1);
                }
            }

            if (authorizationListener != null) {
                authorizationListener.onAuthorizationCompleted(md, pares);
            }
        }

        /**
         * Sets the callback to receive auhtorization events
         *
         * @param authorizationListener
         */
        public void setAuthorizationListener(final D3SViewAuthorizationListener authorizationListener) {
            this.authorizationListener = authorizationListener;
        }

        /**
         * Starts 3DS authorization
         *
         * @param acsUrl ACS server url, returned by the credit card processing gateway
         * @param md     MD parameter, returned by the credit card processing gateway
         * @param paReq  PaReq parameter, returned by the credit card processing gateway
         */
        public void authorize(final String acsUrl, final String md, final String paReq) {
            authorize(acsUrl, md, paReq, null);
        }

        /**
         * Starts 3DS authorization
         *
         * @param acsUrl      ACS server url, returned by the credit card processing gateway
         * @param md          MD parameter, returned by the credit card processing gateway
         * @param paReq       PaReq parameter, returned by the credit card processing gateway
         * @param postbackUrl custom postback url for intercepting ACS server result posting. You may use any url you like
         *                    here, if you need, even non existing ones.
         */
        public void authorize(final String acsUrl, final String md, final String paReq, final String postbackUrl) {
            if (authorizationListener != null) {
                authorizationListener.onAuthorizationStarted(D3SView.this);
            }

            if (!TextUtils.isEmpty(postbackUrl)) {
                this.postbackUrl = postbackUrl;
            }

            urlReturned = false;

            List<NameValuePair> params = new LinkedList<NameValuePair>();

            params.add(new BasicNameValuePair("MD", md));
            params.add(new BasicNameValuePair("TermUrl", this.postbackUrl));
            params.add(new BasicNameValuePair("PaReq", paReq));

            Logger.log(params.toString());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                new UrlEncodedFormEntity(params, HTTP.UTF_8).writeTo(bos);
            } catch (IOException e) {
            }

            postUrl(acsUrl, bos.toByteArray());
        }


        class D3SJSInterface {

            D3SJSInterface() {
            }

            @android.webkit.JavascriptInterface
            public void processHTML(final String paramString) {
                completeAuthorization(paramString);
            }
        }
    }

    private interface D3SViewAuthorizationListener {

        /**
         * Called when remote banking ACS server finishes 3DS authorization. Now you may pass the returned
         * MD and PaRes parameters to your credit card processing gateway for finalizing the transaction.
         *
         * @param md    MD parameter, sent by ACS server
         * @param paRes paRes parameter, sent by ACS server
         */
        void onAuthorizationCompleted(final String md, final String paRes);

        /**
         * Called when authorization process is started and web page from ACS server is being loaded.
         * For isntace, you may display progress now, etc...
         *
         * @param view reference for the DDDSView instance
         */
        void onAuthorizationStarted(D3SView view);

        /**
         * Called to update the ACS web page loading progress.
         *
         * @param progress current loading progress from 0 to 100.
         */
        void onAuthorizationWebPageLoadingProgressChanged(int progress);

        /**
         * Called if a loading error occurs
         *
         * @param errorCode
         * @param description
         * @param failingUrl
         */
        void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl);
    }
}
