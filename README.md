CloudPayments Android SDK
=====================
### Использование

Приложение CloudPayments_AndroidSDKDemo демонстрирует работу SDK для платформы Android с платежным шлюзом CloudPayments
Полная информация об использовании на сайте http://cloudpayments.ru/docs/mobileSDK
Схемы проведения платежа http://cloudpayments.ru/Docs/Integration#schemes

### Требования
Для работы CloudPayments AndroidSDK необходим Android версии 4.3 и выше (API level 18).
### Инсталяция
Клонируйте проект на свой компьютер и откройте его в Android Studio:
git clone https://github.com/cloudpayments/CloudPayments_AndroidSDKDemo.git

Или подключите SDK к своему проекту, для этого скопируте папку libs в директорию своего проекта и добавьте в [_build.gradle_][build-config] вашего проекта следующее
```groovy
dependencies {
    compile(name: 'CloudPayments_AndroidSDK', ext: 'aar')
}

repositories {
    flatDir{
        dirs 'libs'
    }
}
```
### Описание работы приложения с SDK CloudPayments

SDK CloudPayments (CloudPaymentsAPI.framework) позволяет:
* Проводить проверку карточного номера на корректность  
```java  
ru.cloudpayments.sdk.ICard card = ru.cloudpayments.sdk.CardFactory.create(java.lang.String number);
boolean card.isValidNumber();
```
* Определять тип платежной системы
```java
ru.cloudpayments.sdk.ICard card = ru.cloudpayments.sdk.CardFactory.create(java.lang.String number);
java.lang.String card.getType();
```
* Шифровать карточные данные и создавать криптограмму для отправки на сервер
```java
ru.cloudpayments.sdk.ICard card = ru.cloudpayments.sdk.CardFactory.create(java.lang.String number);
java.lang.String card.cardCryptogram(java.lang.String publicId) throws 
                                        java.io.UnsupportedEncodingException, 
                                        javax.crypto.NoSuchPaddingException, 
                                        java.security.NoSuchAlgorithmException,                   
                                        ava.security.NoSuchProviderException, 
                                        javax.crypto.BadPaddingException, 
                                        javax.crypto.IllegalBlockSizeException,                     
                                        java.security.InvalidKeyException;
```
### Проведение оплаты

### Пример использования SDK и API CloudPayments 

В примере publicId это тестовые реквизиты для подключения, Вам нужно получить их в личном кабинете на сайте CloudPayments.

##### Двухстадийная оплата. Пример отправки запроса на списание средств с банковской карты через 3ds, через встроенную форму:
```java
        Intent intent = new Intent(Launcher.this, PaymentWidget.class);
        PaymentWidget.taskListener = paymentTaskListener;
        intent.putExtra(PaymentWidget.EXTRA_AMOUNT, amount); // Сумма оплаты
        intent.putExtra(PaymentWidget.EXTRA_DESCRIPTION, desc);
        intent.putExtra(PaymentWidget.EXTRA_CURRENCY, currency);
        intent.putExtra(PaymentWidget.EXTRA_PUBLIC_ID, Constants.publicId);
        intent.putExtra(PaymentWidget.EXTRA_INVOICE_ID, Constants.invoiceId);
        intent.putExtra(PaymentWidget.EXTRA_ACCOUNT_ID, Constants.accountId);
        startActivity(intent);
```

где
```java     
        private PaymentTaskListener paymentTaskListener = new PaymentTaskListener() {
                @Override
                public void success(BaseResponse baseResponse) {
                    // успешно 
                    // baseResponse instanceof CardsAuthConfirmResponse - оплата 3ds
                    // baseResponse instanceof CardsAuthResponse
                }
        
                @Override
                public void error(BaseResponse baseResponse) {
                    // ошибка
                }
        
                @Override
                public void cancel() {
                    // отменено пользователем
                }
            };
```

Описание успешного ответа
```java
        public class CardsAuthConfirmResponse extends BaseResponse {
            public CardTransaction transaction;
        }
        
        public class CardsAuthResponse extends BaseResponse {
            public CardAuth auth;
        }
        
        public class CardTransaction {
            public int transactionId;
            public String cardHolderMessage;
            public String accountId;
            public String token;
        }
        
        public class CardAuth extends CardTransaction {
            public String paReq;
            public String acsUrl;
        }
```
Подробнее см. ru.cloudpayments.sdk.demo.buildIn.BuildInActivity.java

### Пример отправки запроса на списание средств с банковской карты через 3ds, через свою форму:

**Одностадийная оплата**

```java
        ICard card = CardFactory.create(cardNumber, expDate, cvv);
        if (card.isValidNumber()) {
            IPayment paymentCharge = PaymentFactory.charge(CustomActivity.this,
                            Constants.publicId, "accId", "invId",
                            card.cardCryptogram(Constants.publicId),
                            holderName, amount, currency, desc,
                            "http://example.ru");
            paymentCharge.run(paymentTaskListener);
        } else {
            //CardNumber is not valid
        }
```

**Двухстадийная оплата**

```java
        ICard card = CardFactory.create(cardNumber, expDate, cvv);
        if (card.isValidNumber()) {
            IPayment paymentAuth = PaymentFactory.auth(CustomActivity.this,
                            Constants.publicId, "accId", "invId",
                            card.cardCryptogram(Constants.publicId),
                            holderName, amount, currency, desc,
                            "http://example.ru");
            paymentAuth.run(paymentTaskListener);
        } else {
            //CardNumber is not valid
        }
```
**Constants.publicId** - Ваш public ID, его необходимо получить в личном кабинете.
**accId** - ID покупателя в вашей системе.
**invId** - ID заказа в вашей системе.

Подробнее см. ru.cloudpayments.sdk.demo.custom.CustomActivity.java

### Тестирование
https://cloudpayments.ru/Docs/Test - данные банковских карт для тестирования.

### Ключевые моменты

В демо-проекте частично используется код из библиотеки https://github.com/LivotovLabs/3DSView. Все права на код этой библиотеки принадлежат авторам библиотеки.