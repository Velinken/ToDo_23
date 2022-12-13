/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.app4web.asdzendo.todo.launcher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Старт app на Android:
 * Введение
 * app лежат на "ssd" смартфона, иконка его торчит на рабочем столе,
 * манифест его лежит в правильном нужном надежном месте и все время виден Android
 * Когда тапаем на иконку (или стартуем с AS) то Android:
 * 1. берет манифест, читает его, хотя он и так его знает и делает то, что там написано
 * 2. а) там стартовать ToDoApplication.kt - что он и делает (значки, иконки и тема bar) (см.ниже)
 *    б) стартовать ToDoActivity.kt - находит, загружает, стартует и отдает ей управление
 *    в) применяет стили и названия указанные в манифесте
 *    г) разрешает доступы запрошенные в манифесте
 * 3. ToDoActivity.kt - начинает исполняться сверху вниз по строчкам как обычно и при этом:
 *    а) Создает свою ViewModel + repozitory + Dao + DataBase (или связывается с ними)
 *    б) Надувает главный экран из своего activity_to_do.xml и размещает на него:
 *      - верхний бар
 *      - меню три точки (и надувает его main.xml)
 *      - определяет обработку при тапах на менюшки
 *      - Бургер три полоски и связывает с навигацией
 *      - создает и надувает левую шторку заголовком nav_header_to_do.xml и
 *        содержанием activity_main_drawer.xml
 *      - в титл запихивает количество записей в базе
 *      все это рама для "картины"
 *      !!! Без нижней навигации !!!
 *      - и, наконец, определяет центральное место для загрузки фрагментов, указывая в нем
 *      механизм навигации NavHostFragment и файл навигации mobile_navigation.xml
 * 4. NavHostFragment считывает файл mobile_navigation.xml и определив оттуда, что стартовым будет
 *      ToDoFragment, зовет его на это место и отдает ему управление.
 *      (ToDoActivity отходит в стэк назад и работает оттуда)
 * 5a. ToDoFragment, получив управление начинает выполняться сверху вниз с целью разместить себя
 * в выделенном месте. Для этого:
 *    а) Создает свою ViewModel + repozitory + Dao + DataBase (или связывается с ними)
 *    б) Добавляет и обрабатывает меню три точки для этого фрагмента (додувая его to_do.xml)
 *       и определяет его обработку
 *    в) Вниз отведенного места надувает нижнее меню bottom_nav_menu.xml и определяет его обработку
 *       в ToDoViewModel
 *    г) Еще создает FAB и определяет ее обработку в ToDoViewModel
 *    д) Берет свой to_do_recycler_list.xml надувает его и размещает его в это место, в середину картины
 *    e) Передает to_do_recycler_list.xml ссылку на ToDoViewModel, (это технология Buinding), что бы
 *       xml общаясь с юзером мог взять что-то или передать или обратиться за обработкой из ViewModel
 *  5б. При размещении картины внутри этой картины (своей xml) натыкается на поле recycler view и
 *  ему надо его разместить, а поле recycler view для каждой своей строчки зовет ToDoPageAdapter.kt,
 *  Этот адаптер раздувает вид каждой строчки из to_do_recycler_item.xml
 *  !!! Если портрет, то строка надувается to_do_recycler_item.xml, если ландшафт, то
 *  строка надувается to_do_recycler_item.xml (lang)
 *  5в. ToDoPageAdapter.kt заполняет строчку/карточку нужными данными, теми, которые сейчас должны
 *  быть на экране. По правилам recycler view, т.е. при сдвижке данных их опять перезаполнит, заполняя
 *  хвост, стирая нос не держа лишних данных в памяти. Не храня ничего лишнего, кроме того, что видно на экране.
 *  5г. Данные, которые надо высвечивать recycler view ему поставляет библиотека Paging 3.0 кусками
 *  в потоке (Flow), для чего используется спецадаптер PagingDataAdapter recycler view
 *  для чего организуется специальный  Flow<PagingData<Fact>> куда Paging 3.0 считывает кусками через
 *  repozitory из Dao данные из базы своей спецкомандой потока Room. и все это делается в корутинах
 *  в отдельных потоках.
 *  5д. Еще этот фрагмент, как указано выше организовал и следит за нижним меню bottom, при тапе на
 *  меню буковку меняет ToDoViewModel. Фрагмент мониторит за изменением буковки и когда поменялась
 *  буковка, то он перечню пейджера меняет запрос.
 *  Тогда Paging 3.0 дает другой запрос к базе (DAO), база отвечает другой выборкой, тогда лист
 *  от пейджера заполняется ответом на новый запрос и recycler view хватая оттуда высвечивает ответ
 *  на другую буковку
 *  5е. Когда юзер тапнул на строчку/карточку recycler view то, recycler view сказал клик и ID номер
 *  этой строчки буюсит в ToDoViewModel.kt. Фрагмент мверху мониторит вот этот кликнутый номер, который
 *  нормально null:
 *   - как только он не null, то фрагмент вызывает FactDeiailFragment и передает ему этот ID строчки
 *   на которую нажали.
 *   5ж. Если нажали на FAB, то ToDoFragment то же зовет FactDeiailFragment, только передает ему
 *   буковку на которой стоим сейчас. На самом деле передается каждый раз и ID и буковка.
 *   5з. На самом деле, получив команду перейти на детали, фрагмент зовет NavHostFragment ему говорит
 *   action перехода на второй фрагмент (см. MobileNavigation) и передай ему два аргумента ID и буковку
 *   что NavHostFragment и делает
 *   6а. FactDeiailFragment будучи позванным и получив управление начинает выполнять себя сверху вниз,
 *   как обычно:
 *      - Получает свои два аргумента (ID и буковку)
 *      - Создает свою ViewModel + repozitory + Dao + DataBase (или связывается с ними)
 *      - Добавляет и обрабатывает меню три точки для этого фрагмента из detail.xml
 *      - Раздувает себя из своего макета fact_detail_fragment.xml и размещает на том же месте, где
 *      был recycler view
 *      - Передает ссылку на ViewModel в xml
 *      - Во ViewModel считывает строчку из базы, которую ему передали из recycler view
 *      - Во ViewModel организуется строчка одного факта со всеми ее полями (или новая если по FAB пришли)
 *   6б. fact_detail_fragment.xml связывается с полями факта, который лежит в FactDetailViewModel
 *   Работает двусторонний Buinding между fact_detail_fragment.xml и FactDetailViewModel
 *   7. Здесь не упомянуто о:
 *    - буковки PAEMI живут в ToDoCONSTANTS.kt enum - это спецкласс
 *    - ToDoInjectorUtils - утилиты для создания ViewModel+repo+dao
 *    - FactDataBase - создает или открывает базу данных по команде из Util
 *    - DatabaseConverters.kt - в SQL не может храниться дата и многое другое, при запихивании туда
 *      дату превращает в long, а при чтении оттуда long в дату.
 *    - Binding.kt - это конверторы UI xml хранит и правит string, а в факте есть long, data и т.п.
 *      приводит данные к экранному виду, экранный вид запихивает обратно в переменную. (там можно
 *      ставить проверки)
 *    - О сложностях по многопоточной работе программы см. по тексту
 *    - еще о чем-нибудь и тонкостях
 *    - здесь нет того, что напишем еще в будущем.
 *
 *
 */

/**
 * onCreate is called before the first screen is shown to the user.
 * onCreate Application вызывается до того, как пользователю будет показан первый экран.
 *
 * Use it to setup any background tasks, running expensive setup operations in a background
 * thread to avoid delaying app start.
 * Используйте его для настройки любых фоновых задач, выполняя дорогостоящие операции настройки
 * в фоновом режиме поток, чтобы избежать задержки запуска приложения.
 *
 * Этот Application вызывается сразу из манифеста, т.к. android:name=".launcher.ToDoApplication",
 * а после него вызывается android:name=".launcher.ToDoActivity"
 * А если в Application стартовать фоновый поток, то он начнет выполняться, но в фоне, т.е.
 * не дожадаясь его окончания стартует активити;
 * А можно много потоков стартовать, а можно службы и сервисы здесь стартовать, но в фоне
 * А тогда они будут работать параллельно с клиентской активити.
 *
 */

/**
* Стартует Androidом из манифеста, самый первый - на экране ничего не появляется
* Умирает она последней поэтому в ней что-нибудь размещать на всю программу
* # Если в ней стартовать потоки, то они будут жить параллельно с остальной программой
* Например птичку, бота, службу какую-то, навязчивую рекламу, которая все время всплывает.
* Архитектура рекомендует здесь размещать запланированные работы
*/
/**
 * @HiltAndroidApp запускает генерацию кода Hilt, включая базовый класс для вашего приложения, которое может использовать внедрение зависимостей.
 * Контейнер приложения является родительским контейнером приложения,
 * что означает, что другие контейнеры могут получить доступ к зависимостям, которые он предоставляет.
 * Зависимости берет из di/FactModule
 */
@HiltAndroidApp
class ToDoApplication : Application() { // родительский класс public class Application extends ContextWrapper implements ComponentCallbacks2

    /**
    * Андроид диктаторская система и он и только он маин программа по умолчанию и навсегда.
    * Поэтому Андроид просматривает манифест: видит, что надо стартовать application
    * Андроид загружает в память класс ToDoApplication и создает его экземпляр
    * Потом он распределяет там все переменные
    * Андроид вызывает метод- функцию OnCreate
    * (для application, activity, fragment)
    * А для других родительских класс могут вызываться другие override как задумано в родительском классе
    * Функция получает управление и начинает себя выполнять сверху вниз в порядке своего жизненного цикла
    */
    override fun onCreate() {
        // Вызов родительского класса - спецстандартная запись
        // Мы его вызываем что бы Андроид в родительском классе сделал нужные необходимые ему действия
        // this. - это этот класс - можно не указывать это по умолчанию, super. - это родительский класс,
        super.onCreate()
        // стартуем корутину с вызовом из нее функции приостановки (Suspend)
        // Как только видишь Coroutine- launch-, Asynk-, Wait? join и еще 20 ключевых слов, то ДО звать
        CoroutineScope(Dispatchers.Default).launch {
            timberInit()  // Инициализировать Timber не блокирует основной поток:+ две задачи
        }
    }
}
    // Когда выполнится эта функция до конца, то она вернет управление Андроиду (не считая корутин)

    private fun timberInit() { // Инициализация Timber
            Timber.plant(Timber.DebugTree())
            Timber.i("ToDoApplication timber READY ")

        // Пример параллельных отменяемых корутин
        /*val cor1 = CoroutineScope(Dispatchers.Default).launch {
            Timber.i("ToDoApplication timber Init START_1")
            for (i in 0..10) {
                delay(7000L)
                Timber.i("ToDoApplication Работа 1 шаг $i")
            }
            Timber.i("ToDoApplication timber Init FINISH_1")
        }

        CoroutineScope(Dispatchers.Default).launch {
            Timber.i("ToDoApplication timber Init START_2")
            for (i in 0..10) {
                delay(3000L)
                Timber.i("ToDoApplication Работа 2 шаг $i")
            }
            cor1.cancel()
            Timber.i("ToDoApplication timber Init FINISH_2")
        }*/
    }

    /**
     * Override application to setup background work via WorkManager
     * Переопределение приложения для настройки фоновой работы через Диспетчер работ
     */
    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     * Настройка работы менеджер фоновых заданий, чтобы "взять" новую сеть данных ежедневно.
     */
// Важно отметить, что WorkManager.initialize должен вызываться изнутри onCreate без использования фонового потока,
// чтобы избежать проблем, возникающих при инициализации после использования WorkManager.
    // Шаг 1: Настройка повторяющейся работы
// Сделайте запрос PeriodWorkRequest: Это должно бежать один раз каждый день.
  /*  private fun setupRecurringWork() {
        // Определите ограничения: чтобы предотвратить выполнение работы, когда нет доступа к сети или устройство разряжено.
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)  // Unlimited
                .setRequiresBatteryNotLow(true)                 // Battery не разряжена
                .setRequiresCharging(true)                      // Стоит на зарядке
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setRequiresDeviceIdle(true)             // Устройство не используется
                    }
                }.build()  // Используйте build()метод для генерации ограничений из компоновщика.
            // переменную, которая использует PeriodicWorkRequestBuilder для создания PeriodicWorkRequest для вас RefreshDataWorker.
        /*val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints) //  Добавьте ограничения
                .build()
         */
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        // val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(16, TimeUnit.MINUTES)
        //       .build()

        //  Запланируйте работу как уникальную:
        // Получить экземпляр WorkManager и позвонить, enqueueUniquePeriodicWork чтобы запланировать работу.
        Timber.d("Periodic Work request for sync is scheduled")
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest)
    }*/
    // Если существует ожидающая (незавершенная) работа с тем же именем,
    // ExistingPeriodicWorkPolicy.KEEP параметр заставляет WorkManager сохранить предыдущую периодическую работу
    // и отклонить новый запрос на работу.




// В приложении для Android Application класс является базовым классом,
// который содержит все другие компоненты, такие как действия и службы.
// Когда создается процесс для вашего приложения или пакета,
// создается экземпляр Application класса (или любого подкласса Application) перед любым другим классом.
// Класс является хорошим местом , чтобы запланировать WorkManager.

/*
Шаг 2: Запланируйте WorkRequest с WorkManager
После того, как вы определите свой WorkRequest, вы можете запланировать это с WorkManager помощью enqueueUniquePeriodicWork()метода.
Этот метод позволяет добавить уникальное имя PeriodicWorkRequest в очередь,
где одновременно PeriodicWorkRequest может быть активным только одно из определенных имен.

Например, вы можете захотеть, чтобы была активна только одна операция синхронизации.
Если выполняется одна операция синхронизации, вы можете разрешить ее выполнение или заменить ее новой работой,
используя ExistingPeriodicWorkPolicy .

Чтобы узнать больше о способах планирования WorkRequest, см. WorkManagerДокументацию.
 */

/*
Лучшая практика:
onCreate()метод работает в основном потоке.
Выполнение длительной операции в onCreate() может заблокировать поток пользовательского интерфейса
и вызвать задержку загрузки приложения.
Чтобы избежать этой проблемы, запустите такие задачи, как инициализация Timber и планирование WorkManager
из основного потока внутри сопрограммы.
 */
/**
 * Ликбез Kotlin - Android:
 * this - это этот класс
 * super - это родительский класс
 * class - это класс
 * fun F () - это функция
 * val/var - это объявление переменной
 * А=Б+С - это оператор, Б+С складывается и заносится в А
 *
 * F () - это вызов функции
 * fun F (): View - это объявление функции, которая должна вернуть View
 *
 */
