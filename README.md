# RadioStationVedaRadio
My pet Project. Veda Radio app

Можно скачать по ссылке:
https://play.google.com/store/apps/details?id=ru.music.radiostationvedaradio

Приложение предназначено для онлайн прослушивания интернет трансляции Веда радио, просмотра сайтов соответсвующих тематике, просмотру гороскопа "Вредные советы" на каждый день, и просмотру случайной цитаты из Бхагавад-гиты.

В приложении использовались:
Для прослушивания Аудио : Foreground Service с media button Notification. (С Activity связь реализована через Binder и BroadcastReceiver).
Для запросов в сеть использовался Retrofit с Intercentor okHttp. С конвертерами SimpleXml и Scalars(для сырого html)
Используется Room для сохранения данных из сети , и последующего их отображения в случае отсутствия интернета.
В основе архитектуры лежит подход MVVM, с запросами в сеть, приёмом данных в dataEmitter в репозитории типа BehaviorSubject(RxJava) и последующей трансляции получателям в ViewModel и соответственно подпиской view на данные из ViewModel
Используется подход SingleActivity (кроме Splash Screen - он реализован как отдельное активити), при помощи Android Navigation Component.
К проекту подключена Firebase Crachlitics для отслеживания крашей и просмотра логов.
А так же в приложении добавлена реклама от Yandex РСЯ - баннер единый для всех экранов.

В будущем планируются обновления. 

