package com.app4web.asdzendo.todo.ui.detail

import androidx.lifecycle.*
import com.app4web.asdzendo.todo.database.Fact
import com.app4web.asdzendo.todo.database.FactRepository
import com.app4web.asdzendo.todo.launcher.PAEMI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// Создается фабрикой из FactDetailFragment.kt + репо+дао+database
// ссылка на него поступает layout\fact_detail_fragment.xml
// Заметьте, что это не активити, не фрагмент, а родительский класс ViewModel
// Здесь не надо ждать onCreate и всякие от активити, это другой специальный класс для архитектуры,
// что бы жить вместе с активити/фрагментом, отвечает за все за это библиотека Lifecycle
@HiltViewModel
class FactDetailViewModel @Inject constructor(
        private val factRepository: FactRepository,

        /**
         * Marks a parameter in a androidx.hilt.lifecycle.ViewModelInject-annotated constructor.
         * Отмечает параметр в жизненном цикле androidx.hilt.ViewModel Inject-аннотированный конструктор.
         * Deprecated
         * Use Assisted
         */
        private val savedStateHandle: SavedStateHandle
     //   @Assisted factID: Int = 0,
     //   @Assisted paemi: PAEMI = PAEMI.N,
): ViewModel(), LifecycleObserver  {
    // Assume we're making the intent data a lifedata.
    //private val intentDataStoreAsLiveData
    //        = savedStateHandle.getLiveData<String>(KEY)
    // Or we can just extract the original form of the data
    //private val inteData = savedStateHandle.get<String>(KEY)

    // Пристроил временно пока не знаю как передавать параметры в ViewModel c Hilt
    companion object { // Это статика, если говорить по java
        var factID: Int = 0
        var paemi: PAEMI = PAEMI.N
        fun start(
                factID: Int = 0,
                paemi: PAEMI = PAEMI.N,
        ) {
            this.factID = factID
            this.paemi = paemi
        }
    }

    init { Timber.i("TODO FactDetailViewModel created $factID")}

    /**
     * Эта FactDetailViewModel считывает из базы данных через репозиторий factRepository запись с номером factID
     * Считывает fact в LiveData через MediatorLiveData
     * (это изврат, по другому не нашел) надо бы напрямую прямо в LiveData, когда стану шибко умным
     * После этого она ничего не делает, а просто висит рядом со своим раздутым xml и в ней этот факт
     * Надутый layout\fact_detail_fragment.xml лазеет в эту ViewModel и вынимает из нее значения полей
     * Надутый layout\fact_detail_fragment.xml запихивает в эту ViewModel измененнные юзером значения полей
     * Это реализуется двухсторонним DataBinding (используется адаптеры из Binding.kt)
     */
    /**
     * Hold a reference to FactDatabase via its FactDatabaseDao.
     * Держите ссылку на базу данных Fact через ее Fact DatabaseDao.
     * Теперь ссылка factRepository на FactRepository
     */

    /** Coroutine setup variables Переменные настройки сопрограммы
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     * задание viewModel позволяет нам отменить все сопрограммы, запущенные этой ViewModel.
     */
   // private val viewModelJob = Job()
    // Создаем переменную для хранения одной строки базы данных (одного факта)
    //private val fact = LiveData<Fact>()
    private val fact = MediatorLiveData<Fact>()
    fun getFact() = fact

    // Выполняется при создании class FactDetailViewModel
    init {
        viewModelScope.launch {
            // Временную переменную для хранения одной строчки базы и
            val fact0L: LiveData<Fact> = if (factID == 0)
                // если factID == 0, то пришли с fab создаем пустой факт по умолчанию (не считываем с базы)
                MutableLiveData(Fact(paemi = paemi, nameShort = "новый Факт", name = "Факт полностью: новый"))
            else
                // если factID не ноль, то счтать строчку с номером factID из базы
                    // через factRepository, через Dao считается LiveData этой строчки
                factRepository.getFactWithId(factID)
            // Особенность MediatorLiveData ему надо это addSource
            fact.addSource(fact0L, fact::setValue)
            Timber.i("ToDo FactDetailViewModel $factID")
        }
        // Когда сработала эта Корутина, то заполнился fact чем-то или пустой или из базы
    }

    // Добавляет и обрабатывает меню три точки для этого фрагмента
    // все три функции обработка выбора пунктов из меню трех точек (вызывается из фрагмента)
     fun update() {
        viewModelScope.launch {
         fact.value?.rezult = " Изм ${fact.value?.factId} " + fact.value?.rezult
         factRepository.update(fact.value)
        }
         Timber.i("ToDo Detail ViewModel update ${fact.value?.factId} ")
         backupTrue() // Дать пожелание покинуть этот фрагмент и вернуться в таблицу
     }

     fun insert()  {
         viewModelScope.launch {    // coroutine стоит в репозитории, наверно здесь не надо???
             //   withContext(Dispatchers.IO) {
         fact.value?.rezult = " Доб " + fact.value?.rezult
         factRepository.insert(fact.value)
             //   }
         }

        Timber.i("ToDo Detail ViewModel insert ${fact.value?.rezult}")
         backupTrue()
     }

     fun delete() {
         viewModelScope.launch {
      //  withContext(Dispatchers.IO) {
         factRepository.delete(fact.value)
        }
         Timber.i("ToDo Detail ViewModel delete ${fact.value?.factId}")
         backupTrue()
     }

    // Объявляю живой флажок, пора ли возвращаться в таблицу, (по умолчанию нет - null)
    private val _backup: MutableLiveData<Boolean?> = MutableLiveData<Boolean?>(null)
    val backup: LiveData<Boolean?>
        get() = _backup

    private fun backupTrue() {
        _backup.value = true
    }
    fun backupNull() {
        _backup.value = null
    }
}