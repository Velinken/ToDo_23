package com.app4web.asdzendo.todo.launcher



import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.app4web.asdzendo.todo.R
import com.app4web.asdzendo.todo.databinding.ActivityToDoBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

// Создается новый класс ToDoActivity с родителем AppCompatActivity и внедрением зависимостей
// Теперь с Hilt для этого надо аннотировать ToDoApplication и ToDoActitityViewModel :
@AndroidEntryPoint
class ToDoActivity : AppCompatActivity(), MenuProvider {
    // Выделяется место под эти переменные в созданном классе
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainBinding: ActivityToDoBinding
    // Создается переменная mainViewModel и она инициализируется тем, что ей указано, указано в {}
    // По простому создает ViewModel используя HILT:
    private val mainViewModel: ToDoActitityViewModel by viewModels()
    // При создании ViewModel зовется создание репозитория, а при создании репозитория зовется
    // создание ДАО, а при создании ДАО зовется создание Базы данных
    // Если все это было создано раньше, то просто возвращаются ссылки на созданное, а не создается заново.
    // например при повороте смартфона

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityToDoBinding.inflate(layoutInflater)
        // Надувает главный экран из своего activity_to_do.xml и запоминает адрес в переменной
        setContentView(mainBinding.root)

        // Добавляет меню три точки для этого фрагмента на месте toolbar указанному ЛВ в xml
        setSupportActionBar(mainBinding.toolbar)  // Задать toolbar, что у него есть три точки через xml
        // надувание этого меню см ниже в onCreateOptionsMenu потом
        // обработка этого см. еще ниже onOptionsItemSelected

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Просим ссылку на загрузчик фрагментов и говорим ему, что грузить он будет в поле @+id/nav_host_fragment
        //val navController = findNavController(R.id.nav_host_fragment)
        // верхнему бару говорит,что у него будет вызывная шторка и она указана drawerLayout
        // а переходить надо будет контроллер навигации, а он знает куда
        appBarConfiguration = AppBarConfiguration(navController.graph, mainBinding.drawerLayout)
        // Полю @+id/toolbar пришивается setupWithNavController с конфигурацией построенной выше
        mainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
        // setupActionBarWithNavController(navController, appBarConfiguration) // Альтернатива todoapp
        // Полю @+id/nav_view ему звать navController, когда на него нажмут, что бы перейти куда нажали
        mainBinding.navView.setupWithNavController(navController)
        //mainBinding.bottomNavView.setupWithNavController(navController) перенесен в фрагмент

        // Вывести в заголовок количество записей в базе, в зависимости от того, что указано в ToDoCONSTANTS
        val bas = if (BASE_IN_MEMORY) "M" else "D"
        // Наблюдаю функцию count () во mainViewModel, репо, дао, FactDatabase и когда она изменяется, то
        // title activity присваиваю имя и количество записей в базе
        mainViewModel.count.observe(this) { count -> title = "ToDo$bas=$count" }

        // В надутый xml загоняю ссылку на mainViewModel в переменную viewmodel
        // после этого xml сама из своих дизайнерских полей может обращаться к классу mainViewModel
        // может брать оттуда значения, передавать туда значения и вызывать оттуда функции обработки
       // mainBinding.viewmodel = mainViewModel
        // Владельцем циклов жизни этого xml надутого буду Я - ToDoActivity.kt
        //mainBinding.lifecycleOwner = this

        // https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
        // Т.к. в onCreate мы сказали setSupportActionBar(mainBinding.toolbar), то
        // Андроид вызовет onCreateOptionsMenu, а мы его здесь переопределим. - Deprecate
        // Добавляет меню три точки для этого фрагмента
        addMenuProvider(this) //, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // отправляет в логкат ToDoMainActivity onCreate
        Timber.i("ToDoMainActivity onCreate ")
    // onCreate закончен, все сделали отдаем управление Андроиду и он отдает юзеру
    }
    // https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
    // Т.к. в onCreate мы сказали setSupportActionBar(mainBinding.toolbar), то
    // Андроид вызовет onCreateOptionsMenu, а мы его здесь переопределим. - Deprecate
    // Добавляет меню три точки для этого фрагмента
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Раздуйте меню; это добавит элементы в панель действий, если она присутствует.
        inflater.inflate(R.menu.main, menu)
    }
    // Обрабатывает меню три точки для этого фрагмента при нажатии на элемент меню
    override fun onMenuItemSelected(item: MenuItem): Boolean {
        // Меню MenuItem присылает имя поля из XML, которое нажали
        // По уму этот обработчик надо бы переместить в ToDoActivityViewModel, но технологии биндинг для меню не создано
        // Обещают сделать, тогда перемещу
        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
            //R.id.action_reboot -> startActivity(Intent(this, ToDoActivity::class.java))
            //R.id.main_action_base ->  BASE_IN_MEMORY = false
            R.id.main_action_base_in_memory -> BASE_IN_MEMORY = true
            R.id.main_action_base_on_disk -> BASE_IN_MEMORY = false

            R.id.main_action_constfact_1 -> COUNTSFact = 1
            R.id.main_action_constfact_10 -> COUNTSFact = 10
            R.id.main_action_constfact_100 -> COUNTSFact = 100
            R.id.main_action_constfact_1000 -> COUNTSFact = 1_000
            R.id.main_action_constfact_10_000 -> COUNTSFact = 10_000
            R.id.main_action_constfact_100_000 -> COUNTSFact = 100_000
            R.id.main_action_constfact_1000_000 -> COUNTSFact = 1_000_000
            else -> {
                //super.onOptionsItemSelected(item);
                return false
            }
        }
        Toast.makeText(applicationContext, "base in memory = $BASE_IN_MEMORY COUNTSFact = $COUNTSFact", Toast.LENGTH_LONG).show()
        return true
    }

    //codelab изучить и применять систему автоматического резервного копирования Android от 6.0
    //https://codelabs.developers.google.com/codelabs/android-backup-codelab/index.html#0
}

/* вроде нужен для возврата по стрелке но работает и без него т.к. есть app:defaultNavHost="true"
    override fun onSupportNavigateUp(): Boolean {
    //  = findNavController(R.id.nav_host_fragment).navigateUp()
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
/**
 * onCreate надувает setContentView (вот этот макет: R.layout.activity_to_do.xml)
 * По дороге он встречает View <fragment и он должен его надуть, а там есть менеджер фрагментов
 * android:name="androidx.navigation.fragment.NavHostFragment"
 * т.е. он должен надуть сюда что-то этим NavHostFragment, что он и делает вызывая его для надувания
 * и отдает ему mobile_navigation.xml специальным образом подготовленный файл навигации из которого
 * понятно, что надувать.
 *
 * NavHostFragment считывает mobile_navigation и запоминает навсегда.
 * (Мы потом много раз к нему будем обращаться, перейти куда-нибудь)
 * А сейчас он находит строчку app:startDestination="@id/todoFragment", т.е. ему сказано, что стартовым надувать
 * фрагмент с этим имемнем в этом файле.
 * Находит фрагмент с этим именем todoFragment и дает ему команду надуваться сюда:
 * - находит файл kt этого фрагмента ui.todo.ToDoFragment.kt
 * - стартует этот файл ToDoFragment.kt
 * - передает управление этому файлу, а я пошел и больше не нужен, но буду за спиной
 * А мы переходим к ToDoFragment.kt и начинаем с ним рисовать картину в уже подготовленной раме
 */