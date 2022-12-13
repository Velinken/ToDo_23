package com.app4web.asdzendo.todo.ui.detail


import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app4web.asdzendo.todo.databinding.FactDetailFragmentBinding
import timber.log.Timber
import com.app4web.asdzendo.todo.R
import dagger.hilt.android.AndroidEntryPoint

// Вызывается NavHostFragment-ом  из ToDoFragment.kt при нажатии на строку или на fab
// сюда передано управление, что бы разместить этот FactDetailFragment в отведенном ToDoActivity месте под фрагмент
@AndroidEntryPoint
class FactDetailFragment : Fragment(), MenuProvider {

    // Kotlin Android идиома, что бы поймать два аргумента: ID и букву.
    // Если ID не ноль, то буква нас не интерессует
    // Если ID ноль, т.е. по fab пришли, то создать новый факт с переданной буквой
    private val args: FactDetailFragmentArgs by navArgs()

    private val factDetailViewModel: FactDetailViewModel by viewModels()
    // Создаем factDetailViewModel + репо + dao + database и связывается с ними
/*    private val factDetailViewModel: FactDetailViewModel by viewModels {
        ToDoInjectorUtils.provideFactDetailViewModelFactory(requireContext(), args.factID, args.paemi)
    }*/

    // private val factDetailViewModel by navGraphViewModels<FactDetailViewModel>(R.id.nav_host_fragment) {
    //     defaultViewModelProviderFactory  // необязательно но сбоит пока Вообще вылет
    // }

    // Андроид вызовет как обычно onCreate, но во фрагменте он ничего не раздувает.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Сообщает, что надо добавить в меню три точки для этого фрагмента

        //setHasOptionsMenu(true) //deprecate перенесено см onViewCreated
        Timber.i("ToDo FactDetailFragment onCreate ")

        // Пристроил временно пока не знаю как передавать параметры в ViewModel c Hilt
        //factDetailViewModel.start(args.factID, args.paemi)
        FactDetailViewModel.start(args.factID, args.paemi)
    }

    // Для :Fragment() Андроид вызовет onCreateView и он будет надувать макет
    override fun onCreateView(
        // Андроид передает сюда стандартные параметры
        inflater: LayoutInflater, // раздуваетль фрагмента
        container: ViewGroup?,    // поле фрагмент в котором надо раздуваться
        savedInstanceState: Bundle?, // сохраненные параметры устаревшие из Kitty
    ): View {                            // возвращает раздутый и настроенный View
        Timber.i("ToDoFactDetailFragment onCreateView ")

        // Get a reference to the binding object and inflate the fragment views.
        // Получить ссылку на объект привязки и раздуть представления фрагментов.
        // Используется databinding - из-за него без подчерков и с большой буквы
        // Не указан xml который раздувать FactDetailFragmentBinding из этого названия он знает,
        // что надо раздувать layout\fact_detail_fragment.xml
        val binding = FactDetailFragmentBinding.inflate(inflater, container, false)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        // Чтобы использовать модель представления с привязкой данных, вы должны явно
        // дать объекту привязки ссылку на него.
        // ссылка на factDetailViewModel засовывается в viewmodel fact_detail_fragment.xml
        // теперь act_detail_fragment.xml по этой ссылке может брать из factDetailViewModel напрямую
        binding.viewmodel = factDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner //this // владелец цикла этого фрагмента Я

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
        Timber.i("ToDo FactDetailFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Для возврата в таблицу по нажатию любой кнопки
        // Наблюдатель /ожидатель надо ли возвращаться назад (добавить / обновить / удалить, а стрелочки шли мимо)
        factDetailViewModel.backup.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true. Наблюдаемое состояние истинно.
                // Получив команду перейти мы зовем NavController это то же самое, что NavHostFragment
                // Мы ему говорим пошли, навигируй нас в назад Up
                // Он знает откуда пришел и грузит фрагмент который был до этого, откуда пришли
                // соответственно зовет ToDoFragment.kt из указанного каталога
                // и говорит ему ты сюда давай размещайся и отдает ему управление
                this.findNavController().navigateUp()
                factDetailViewModel.backupNull()
            }
        }
    }
//    https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
    // Добавляет в меню еще пункты
    //override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail, menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }
    // Обрабатывает нажатия на добавленные пункты меню
    //override fun onOptionsItemSelected(item: MenuItem): Boolean {
    override fun onMenuItemSelected(item: MenuItem): Boolean {
        // Зовет функции из factDetailViewModel
        when (item.itemId) {
            R.id.Add_fact ->
                factDetailViewModel.insert()
            R.id.Update_fact->
                factDetailViewModel.update()
            R.id.Delete_fact->
                factDetailViewModel.delete()
            else -> return false
        }
        return true
        // По уму, когда нибудь будет команда в xml прямо вызвать эти функции обработки меню
    }  //, viewLifecycleOwner, Lifecycle.State.RESUMED)
}
/**
 * Все основное только начинается, здесь ничего нет во фрагменте
 * в xml стоит вся мудрость заполнения и изменения полей юзером методом двунаправленного датабиндинга
 * а во ViewModel стоит все остальнео и вся обработка. Именно то поэтому тут ничего и нет, а только:
 * - создать ViewModel и передать ей аргументы
 * - раздуть макет
 * - допихать три точки в меню с обработкой
 * - ожидать команды вернуться назад
 */
