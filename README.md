#Использование

Приложение CloudPayments_AndroidSDKDemo демонстрирует работу SDK для платформы Android с платежным шлюзом CloudPayments
Полная информация об использовании на сайте http://cloudpayments.ru/docs/mobileSDK

##Установка

git clone https://github.com/cloudpayments/CloudPayments_AndroidSDKDemo.git

##Описание работы приложения с SDK CloudPayments

SDK CloudPayments (CloudPaymentsAPI.framework) позволяет:
* проводить проверку карточного номера на корректность  
```    
boolean ru.cloudpayments.sdk.Card. isValidNumber(java.lang.String number);
```
* определять тип платежной системы
```
int ru.cloudpayments.sdk.CardType.getType(java.lang.String creditCardNumberPart);
```
* шифрует карточные данные, создает криптограмму и отправляет запрос на сервер 
```
new ru.cloudpayments.sdk.ChargeTask(android.support.v4.app.FragmentActivity activity, 
            java.lang.String accountId, 
            java.lang.String invoiceId, 
            java.lang.String cardNumber, 
            java.lang.String cardExp, 
            java.lang.String cardCvv, 
            java.lang.String cardHolderName, 
            double amount, 
            java.lang.String currency, 
            java.lang.String desc, 
            java.lang.String termUrl, ru.cloudpayments.demo.ChargeTaskListener chargeTaskListener).execute();
```
##Проведение оплаты

###Пример использования SDK и API CloudPayments 

Для начала нужно инициализировать сессию при помощи вашего publicId и passApi из личного кабинета CloudPayments:
```
CloudPaymentsSession.init("pk_348c635ba69b355d6f4dc75a4a205", "02a16349d37b79838a1d0310e21bd369");
```
Пример отправки запроса на списание средств с банковской карты через 3ds:
```
new ChargeTask(android.support.v4.app.FragmentActivity activity, 
            java.lang.String accountId, 
            java.lang.String invoiceId, 
            java.lang.String cardNumber, 
            java.lang.String cardExp, 
            java.lang.String cardCvv, 
            java.lang.String cardHolderName, 
            double amount, 
            java.lang.String currency, 
            java.lang.String desc, 
            java.lang.String termUrl, ru.cloudpayments.demo.ChargeTaskListener chargeTaskListener).execute();                
```
##Ключевые моменты

В демо-проекте частично используется код из библиотеки https://github.com/LivotovLabs/3DSView. Все права на код этой библиотеки принадлежат авторам библиотеки.
