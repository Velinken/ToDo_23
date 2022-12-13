package com.app4web.asdzendo.todo.ui.todo


import android.view.MenuItem
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app4web.asdzendo.todo.database.FactRepository
import com.app4web.asdzendo.todo.database.FactTable
import com.app4web.asdzendo.todo.launcher.PAEMI
import com.app4web.asdzendo.todo.launcher.PAEMI.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

// Стандартный класс ViewModel для фрагмента ToDoFragment через Hilt
@HiltViewModel
class ToDoViewModel @Inject internal constructor(
    private val factRepository: FactRepository
) : ViewModel() {

    // Переменная в которой сидит нажатая буква от нажатого сейчас пункта Bottom_view
    // Изменяется функцией внизу nClickBottomNavView вызываемой из xml
    // Наблюдается сверху фрагментом для переключения потока на этот SQL фильтр вызовом factsPageChange()
    val paemi: MutableLiveData<PAEMI> = MutableLiveData<PAEMI>(R)

    // из меню ... попытка выдать команду на отмену запроса
    var isCancelFlow = false

    // Наблюдается (т.к. это LifeData) из ToDoActivity
    val count:LiveData<Int> = factRepository.count()
    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     * задание viewModel позволяет нам отменить все сопрограммы, запущенные этой ViewModel.
     * Любая сопрограмма, запущенная в этой области, автоматически отменяется, если ViewModel очищается
     */
    // init {
    private var viewModelJob = Job()
    private var toDoViewModelJob: Job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    // Coroutine that will be canceled when the ViewModel is cleared.
    //}

    // *********** ОСНОВНОЙ СПИСОК от Paging 3.0 откуда берет строчки RecyclerView  ***************
     private val factsPageTable: Flow<PagingData<FactTable>>
        get() = factRepository.getAllPageTableCollect(paemi.value ?: N).flow.cachedIn(viewModelScope)
    // из Репо берется "нужная" функция чтения Paging 3.0; из нее организуется поток (а это корутина Котлин)
    // еще она кешируется но зачем и что дает пока не видно - нужны тесты-исследования

    // объявляется аадптер: он назначен Recycler в фрагменте и  передает ему строчки ниже: adapterPage.submitData(it)
    // Но вместо обычного адаптера наследуется от спец адаптер из Paging 3.0  PagingDataAdapter<Fact, FactViewHolder>(diffCallback)
    // Где Fact - класс строчки данных из ROOM, FactViewHolder - стандарт , diffCallback - стандартный из RecyclerView но здесь обязателен
    // кроме этого в Репо ему передается FactListener для CallBack от клика на строчке -
    // вызыватся прямо из XML android:onClick="@{() -> clickListener.onClick(fact)}"
    val adapterPageTable = ToDoPageAdapterTable(this)

    // Подпишите адаптер на ViewModel, чтобы элементы в адаптере обновлялись
    // когда список меняется; Cancel не работает
    //@OptIn(ExperimentalCoroutinesApi::class)

    @SuppressWarnings("unused")
    fun factsPageChangeTable(paemi: PAEMI) {
        // viewModelJob.cancel()    // НЕ показывает
        // viewModelScope.launch {  // viewModelJob = Не транслируется as CompletableJob но работает
        viewModelJob.cancelChildren()    // Показывает  не отменяет
        toDoViewModelJob.cancelChildren()   // Показывает  не отменяет
        toDoViewModelJob = ioScope.launch {  // Показывает не отменяет

            // Для каждого элемента ОСНОВНОго СПИСКа от Paging 3.0 откуда берет строчки RecyclerView
            // применяется collectLatest из Paging 3.0, которая состоит почти из строчек фактов
            // adapterPage - адаптер RecyclerView передает RecyclerView каждый факт
            // эта декларация и передает он их по требованию RecyclerView
            factsPageTable.cancellable().collectLatest {
                adapterPageTable.submitData(it)
            }
        } //as CompletableJob
    }

    //07.4.5 Задача: обрабатывать щелчки элементов
    // Переменная которая говорит, что надо переходить к фрагменту деталей
    // Если она null не надо переходить, если =0 или номер записи то переходим к деталям
    // SingleLiveEvent<Any>()
    private val _navigateToFactDetail = MutableLiveData<Int?>()
    val navigateToFactDetail
        get() = _navigateToFactDetail

    // Шаг 1: навигация по клику
    // функцию обработчика щелчков вызывается из xml через FactListener переданный адаптеру
    // Когда юзер нажал по строчке, то идет вызов сюда, и флагу перехода присваивается номер строчки
   fun onFactClicked(factid: Int) {
        _navigateToFactDetail.value = factid
    }

    // Шаг 2: Определите метод для вызова после завершения навигации приложения
    // Сброс флажка перехода после осуществления перехода (техническая необходимость)
    fun navigateToFactDetailNavigated() {
        _navigateToFactDetail.value = null
    }

    // подключено  вызов из XML нажание на FAB - зовет фрагмент detail c ID 0 и текущим paemi
    fun fabClick() {
        _navigateToFactDetail.value = 0
        Timber.i("ToDotimber ToDoFragment Recycler ViewModel fabClick() SnackbarTrue() ${paemi.value?.name}")
    }

    // подключено  вызов из XML нажатие на нижнее меню
    fun onClickBottomNavView(clickpaemi: MenuItem): Boolean {
        val oldPaemi = paemi.value
        paemi.value = PAEMI.valueOf(clickpaemi.title?.first().toString())

        if (oldPaemi == paemi.value) paemi.value = N
        Timber.i("ToDoViewModel onClickBottomNavView ${paemi.value?.name}}")
        return true
    }

    // Добавляет и обрабатывает пункт меню три точки  "Очисить базу" для этого фрагмента
    fun clear() = viewModelScope.launch { factRepository.clear() }
    //  обрабатывает пункт меню три точки "Добавить пачку" для этого фрагмента
    fun addFactDatabase(COUNTSFact: Int) = viewModelScope.launch { factRepository.addFactDatabase(COUNTSFact) }

    /**
     *
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     * Выполняется при нажатии кнопки Очистить.Вызывается при демонтаже ViewModel.
     * На этом этапе мы хотим отменить все сопрограммы;
     * в противном случае мы имеем дело с процессами, которым некуда возвращаться
     * использование памяти и ресурсов.
     */
// Вроде не нужен при использовании последних версий библиотек ViewModel - сама почистит - проверить
     override fun onCleared() {
                 super.onCleared()
                 viewModelJob.cancel()
     }
}
