[![](https://jitpack.io/v/Payselection/Payselection-PayApp-SDK-Android.svg)](https://jitpack.io/#Payselection/Payselection-PayApp-SDK-Android)

## PaySelection SDK for Android

PaySelection SDK позволяет интегрировать прием платежей в мобильные приложение для платформы Android.

### Требования
Для работы PaySelection SDK необходим Android версии 5.0 или выше (API level 21)

### Подключение
В build.gradle уровня проекта добавить репозиторий Jitpack

```
repositories {
	maven { url 'https://jitpack.io' }
}
```
В build.gradle уровня приложения добавить зависимость:
```
implementation 'com.github.Payselection:Payselection-PayApp-SDK-Android:$version'
```


### Полезные ссылки

[Личный кабинет](https://merchant.payselection.com/login/)

[Разработчикам](https://api.payselection.com/#section/Request-signature)

### Структура проекта:

* **app** - Пример вызова методов с использованием SDK
* **sdk** - Исходный код SDK


### Возможности PaySelection SDK:

Вы можете с помощью SDK:

* создать заказ и совершить платеж картой
* получить детализацию по конкретной транзации

### Инициализация PaySelection SDK:

1. Создайте конфигурацию с данными из личного кабинета

```
val configuration = SdkConfiguration(
	"04a36ce5163f6120972a6bf46a76600953ce252e8d513e4eea1f097711747e84a2b7bf967a72cf064fedc171f5effda2b899e8c143f45303c9ee68f7f562951c88", // Публичный ключ
	"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUFxbk56eXlwR1R6cENZeDlzMnh6RQpaQ3B4eVkyL2YrbWljb0drWW1rV0M5Szl2SlIvcEtUL0VOUThCT1hIRkFBYW5Mb05PNzVLcmJUQ3Z5Y2pXbkJGCnhYQlJZY2NWeGsxaVB0c0VKbkNlcThmYXMwa2dYMFgzLzFnTFdvbkhheVdTUUl5emFTMXMrWUdsNEJyd2s5c08KTTQyZlk0dkM1WGptU1YxUDNlN2pvNUN1d2hxL2ljUkxZbTg1MXBYRTRiZ3FZYS96NEsrbXhUcWJvdC94b3lhTQpxSmlIOS9EUnQveTc0Z2t6Q0VIRThGQ0M4TkJlVXZUckRWbnlSQ0dtSlpVTDh0QnhPd1N3Ty94M1lzZi9CNU9vCkVjbllWdjVSQmF1MDl4VmFGTFN5QkZiRUsvZnRDUktFeUNQbnpYS2FnbTQ3T2dROEIvTkdhQ0cxRmdVOUhJb2gKd093TmsycWY1NTRPR21Oa0E3MnZCR1E0RTZ4TldUSnFJQWhOTUJQTjFMZGdRNXZTamszTUVJRHQ3Y3FEZzhFRwpCNU0vVS9VT2lVU2tXWmFtR3pXOVZFbkJhRFdWZFpxVVpTc0d0aCtJM093NGRPUUxiZG4rdzljYlpHLzR2VmwvCmFKdTdlQlZ2WVhEL0o0TnIzMk5RZ1o2YzlpMCtNU3RwWFUxMlJ4bzhJK1hCNVpZUTkzNE5iVXJoeDBuMlJhQk0KbGtlSTFtbE1ncWI3ME9BRk5zaDUyNUFIL3k5OVpJTzhsR0RqVEpSdDlKZzdGNVFmUEVWekRIbXdxdy9FaFFjQwpjVG5QaGRLOE53NDJ3QldIVDhXYXg4Y1NxYTdwRytTM2JOYkZvUVJlU1dvK2pzV0JNOU1NemJvckNqYWE1UzRNCnNCV0UyN2FRSElVMU5sTGNqK0laUldzQ0F3RUFBUT09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQo" // Публичный ключ RSA
	"20337" // Site ID
)
```

2. Создайте экземпляр PaySelectionPaymentsSdk для работы с API

```
val sdk = PayselectionPaymentsSdk.getInstance(configuration)
```

### Оплата с использованием PaySelection SDK:

1. Создайте объект PaymentData, содержащий информацию о транзакции и данные карты. При создании объекта используйте соответствующую функцию для выбора метода оплаты

```
val paymentData = PaymentData.createCrypto(
	transactionDetails = TransactionDetails(
		amount = "100",
		currency = "RUB"
	),
	cardDetails = CardDetails(
		cardholderName = "TEST CARD",
		cardNumber = "4111111111111111",
		cvc = "123",
		expMonth = "12",
		expYear = "24"
	)
)
```

2. Создайте объект CustomerInfo с информацией о покупателе

```
val customerInfo = CustomerInfo(
    email = "user@example.com",
    phone = "+19991231212",
    language = "en",
    address = "string",
    town = "string",
    zip = "string",
    country = "USA",
    ip = ""
 ),
```

3. Асинхронно (например, с использыванием coroutines) вызовите метод pay

```
viewModelScope.launch(handler) {
	sdk.pay(
		orderId = orderId, // ID заказа в вашей системе
		description = "", // Комментарий к оплате
		paymentData = paymentData // Данные, полученные из первого шага
		customerInfo = customerInfo // Данные, полученные из второго шага
	).proceedResult(
		success = {
			// Получение данных о созданной транзакции
            // в результате ответа приходит transactionId и transactionSecretKey, redirectUrl
            // "transactionSecretKey" служит параметром запроса получения статуса по transactionId
            // "redirectUrl" - ссылка на веб-интерфейс платежной системы		
        },
		error = {
			// Обработка ошибки оплаты
		}
	)
}
```

4. Отобразите WebView с полученной ссылкой на веб-интерфейс платежной системы (параметр "redirectUrl" из ответа сервера на метод "pay") с помощью
   ThreeDsDialogFragment, который находится в пакете ui. Используйте интерфейс ThreeDSDialogListener для прослушивания статуса транзакции.

### Другие методы PaySelection SDK:

1. Получение статуса одной транзакции

```
sdk.getTransaction(transactionSecretKey, transactionId)
```

### Поддержка

По возникающим вопросам техничечкого характера обращайтесь на support@payselection.com