package ru.cloudpayments.sdk.demo;

import ru.cloudpayments.sdk.demo.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.demo.business.domain.model.billing.CardsAuthConfirmResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Nastya
 * Date: 05.10.14
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
public interface ChargeTaskListener {

    public void error(BaseResponse response);

    public void success(CardsAuthConfirmResponse response);

}
