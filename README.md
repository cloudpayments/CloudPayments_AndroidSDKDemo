#Использование

Приложение CloudPayments_AndroidSDKDemo демонстрирует работу SDK для платформы Android с платежным шлюзом CloudPayments
Полная информация об использовании на сайте http://cloudpayments.ru/docs/mobileSDK

##Установка

git clone https://github.com/cloudpayments/CloudPayments_AndroidSDKDemo.git

##Описание работы приложения с SDK CloudPayments

SDK CloudPayments (CloudPaymentsAPI.framework) позволяет:
* проводить проверку карточного номера на корректность  
```    
boolean ru.cloudpayments.sdk.Card.isValidNumber(java.lang.String number);
```
* определять тип платежной системы
```
java.lang.String ru.cloudpayments.sdk.Card.getType(java.lang.String number);
```
* шифровать карточные данные и создавать криптограмму для отправки на сервер
```
java.lang.String ru.cloudpayments.sdk.Card.cardCryptogram(java.lang.String publicId) throws                                                      java.io.UnsupportedEncodingException, javax.crypto.NoSuchPaddingException, java.security.NoSuchAlgorithmException,                   java.security.NoSuchProviderException, javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException,                     java.security.InvalidKeyException;
```
##Проведение оплаты

###Пример использования SDK и API CloudPayments 

В примере publicId и passApi это тестовые реквизиты для подключения, Вам нужно получить их в личном кабинете на сайте CloudPayments.

Пример отправки запроса на списание средств с банковской карты через 3ds:
```
new ChargeTask(android.support.v4.app.FragmentActivity activity, 
            java.lang.String accountId, 
            java.lang.String invoiceId, 
            java.lang.String cardCryptogram, 
            java.lang.String cardHolderName, 
            double amount, 
            java.lang.String currency, 
            java.lang.String desc, 
            java.lang.String termUrl, ru.cloudpayments.demo.ChargeTaskListener chargeTaskListener).execute();                
```
##Ключевые моменты

В демо-проекте частично используется код из библиотеки https://github.com/LivotovLabs/3DSView. Все права на код этой библиотеки принадлежат авторам библиотеки.
