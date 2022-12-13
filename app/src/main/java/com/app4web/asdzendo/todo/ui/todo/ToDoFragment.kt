package com.app4web.asdzendo.todo.ui.todo

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app4web.asdzendo.todo.R
import com.app4web.asdzendo.todo.databinding.ToDoRecyclerListBinding
import com.app4web.asdzendo.todo.launcher.BASE_IN_MEMORY
import com.app4web.asdzendo.todo.launcher.COUNTSFact
import com.app4web.asdzendo.todo.launcher.PAEMI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A fragment representing a recycler list of Items.
 * Фрагмент, представляющий список элементов recycler.
 * Использует Paging 3.0 и HILT
 */
@AndroidEntryPoint
class ToDoFragment : Fragment(), MenuProvider {

    private val todoViewModel: ToDoViewModel by viewModels()
    // Создаем todoViewModel без параметров + репо + dao + database и связывается с ними
    // Предлагается передавать сюда состояние списка ????
    // private val args: ToDoFragmentArgs by navArgs()
    // Эксперимент используется - взят образец из устаревшей ViewModel для попыток отмены запроса
   // init {
        private var paemiJob: Job? = null
        private val toDoFragmentJob = Job()
        private val uiScope = CoroutineScope(Dispatchers.Main + toDoFragmentJob)
        // Coroutine that will be canceled when the ViewModel is cleared.

       // Timber.i("ToDoViewModel created PAEMI= ${paemi.value?.name}")
   // }

    // Стандартный вызов :Fragment из Android
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Сообщает меню ... из ToDoActivity что надо будет Добавлять и обрабатывать доп меню три точки для этого фрагмента
        //setHasOptionsMenu(true)
        Timber.i("ToDo Recycler Fragment onCreate")
    }
    // Стандартный вызов Fragment из Android для надувания макета и др.
    override fun onCreateView(
            inflater: LayoutInflater,               // раздуваетль фрагмента
            container: ViewGroup?,                  // поле фрагмент в котором надо раздуваться
            savedInstanceState: Bundle?             // сохраненные параметры устаревшие из Kitty
    ): View {    // возвращает раздутый и настроенный View для высветки его родительским фрагментом в указанном поле
        Timber.i("ToDo Recycler Fragment onCreateView")
        /**
         * Get a reference to the binding object and inflate the fragment views.
         * Получить ссылку на объект привязки и раздуть представления фрагментов.
         * Раздувается databinding из layout\to_do_recycler_list.xml по методу для фрагмента
         * Не указан xml который раздувать ToDoRecyclerListBinding из этого названия он знает
         */
        val binding = ToDoRecyclerListBinding.inflate(inflater, container, false)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        // Чтобы использовать модель представления с привязкой данных, вы должны явно
        // дать объекту привязки ссылку на него.
        // ссылка на todoViewModel засовывается в viewmodel to_do_recycler_list.xml
        // теперь to_do_recycler_list.xml по этой ссылке может брать из todoViewModel напрямую
        binding.viewmodel = todoViewModel
        binding.lifecycleOwner = viewLifecycleOwner              // владелец цикла жизни этого фрагмента Я

        // Полю recyclerList из to_do_recycler_list.xml определяется адаптер как положено для RecyclerView
        // Но вместо обычного адаптера наследуется от спец адаптер из Paging 3.0  PagingDataAdapter<Fact, FactViewHolder>(diffCallback)
        // Где Fact - класс строчки данных из ROOM, FactViewHolder - стандарт , diffCallback - стандартный из RecyclerView но здесь обязателен
        // кроме этого ему передается FactListener { factID -> onFactClicked(factID) }  для CallBack от клика на строчке
        binding.recyclerList.adapter = todoViewModel.adapterPageTable

        // Наблюдатель меняющий буковку фильтра от выбора нижнего меню фрагмента
        todoViewModel.paemi.observe(viewLifecycleOwner) {
            // Попытки отмены ненужного уже запроса при выборе новой буковки внизу
            //toDoFragmentJob.cancel()      // стартует НЕ показывает не отменяет
            paemiJob?.cancel()          // стартует показывает не отменяет
            paemiJob = uiScope.launch {   // стартует показывает не отменяет
           // paemiJob = viewLifecycleOwner.lifecycleScope.launch {   // стартует показывает не отменяет
           // ***** Это основной здесь - Заполнение адаптером строк RecyclerView из потока factsPage: Flow<PagingData<Fact>>***
                todoViewModel.factsPageChangeTable(it)
           // Заполнение адаптером строк RecyclerView осуществляется из потока Paging 3.0 который на фильтре
            }
        }

        // в отличии от активити фрагмент требует вернуть ему ссылку на корень раздутого макета, что бы он его высветил
        return binding.root
    }
    /**
     * Вызывается сразу после {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * вернулся, но до того, как любое сохраненное состояние было восстановлено в представлении.
     * Это дает подклассам возможность инициализировать себя один раз
     * они знают, что их иерархия взглядов была полностью создана. Фрагмент
     * однако иерархия представлений на данном этапе не привязана к своему родителю.
     * @param view представление, возвращаемое {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState если значение не равно null, то этот фрагмент создается заново
     * из предыдущего сохраненного состояния, как указано здесь.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("ToDo Recycler Fragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // Он наблюдает когда поступит команда перехода на форму detail (тап в строчку или fab)
        todoViewModel.navigateToFactDetail.observe(viewLifecycleOwner) { factID ->
            // поле LiveData очень часто(почти всегда) null - и это нам не интересно - пропускаем
            factID?.let {
                /**
                 * Получив команду перейти мы зовем NavController это то же самое, что NavHostFragment
                 * Мы ему говорим пошли, навигируй нас в actionTodoFragmentToFactDetailFragment
                 * Он ищет это в mobile_navigation, который он тогда еще считал и помнит
                 * Он находит: <action
                 *            android:id="@+id/action_todoFragment_to_factDetailFragment"
                 *            app:destination="@id/factDetailFragment" />
                 * (подчерки и малые и большие буквы, это правило обращения ToDoFragmentDirections -
                 * пакета безопасной навигации из архитектуры)
                 * Указано куда переходить к фрагменту с именем factDetailFragment в файле mobile_navigation
                 * А там <fragment
                 *        android:id="@+id/factDetailFragment"
                 *        android:name="com.app4web.asdzendo.todo.ui.detail.FactDetailFragment"
                 * соответственно зовет FactDetailFragment.kt из указанного каталога
                 * и говорит ему ты сюда давай размещайся и отдает ему управление
                 * но еще здесь он ему посылает два аргумента factID и paemi (как указано в mobile_navigation)
                 */
                this.findNavController().navigate(
                    ToDoFragmentDirections.actionTodoFragmentToFactDetailFragment(factID,todoViewModel.paemi.value?: PAEMI.N))
                todoViewModel.navigateToFactDetailNavigated()
            }
        }
    }

    // Добавляет в меню три точки пункты для этого фрагмента из menu\to_do.xml
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.to_do, menu)
    }

    //  обрабатывает в меню три точки свои добавленные для этого фрагмента пункты меню
    // хорошо бы научиться вызывать из XML прямо ViewModel, но пока не сделали - обещают
    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fact_base_creating -> {  // "Добавить пачку"
                todoViewModel.addFactDatabase(COUNTSFact) // Дозаполнить заново базу данных
                Toast.makeText(activity,"База добавлено  $COUNTSFact * 7 = ${COUNTSFact * 7} записей ", Toast.LENGTH_SHORT).show()
            }
            R.id.fact_base_clearing -> {  // "Очисить базу"
                todoViewModel.clear()
                Toast.makeText(activity,"База очищена ", Toast.LENGTH_SHORT).show()
                Timber.i("ToDoFactRepository fact_base_clearing База очищена  ")
            }
            R.id.isCancel -> {              // "Отменить поиск"
                todoViewModel.isCancelFlow = true
                Toast.makeText(activity,"Отмена ", Toast.LENGTH_SHORT).show()
            }
            else -> return false
        }
        return true
    } // , viewLifecycleOwner, Lifecycle.State.RESUMED)
}