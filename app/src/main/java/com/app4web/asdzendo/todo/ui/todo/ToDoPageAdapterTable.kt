package com.app4web.asdzendo.todo.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app4web.asdzendo.todo.database.FactTable
import com.app4web.asdzendo.todo.databinding.ToDoRecyclerItemBinding
// Но вместо обычного адаптера наследуется от спец адаптер из Paging 3.0  PagingDataAdapter<Fact, FactViewHolder>(diffCallback)
// Где Fact - класс строчки данных из ROOM, FactViewHolder - стандарт , diffCallback - стандартный из RecyclerView но здесь обязателен
// кроме этого в Репо ему передается FactListener { factID -> onFactClicked(factID) }  для CallBack от клика на строчке
class ToDoPageAdapterTable (private val todoViewModel: ToDoViewModel) : PagingDataAdapter<FactTable, FactViewHolderTable>(diffCallbackTable) {

    // Стандартный метод RecyclerView  - Создает View строчки-карточки место куда Bind занесет данные
    // Он отвечает за внешний вид строчки RecyclerView: конструирует ее и отдает на высветку
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolderTable =
        FactViewHolderTable.from(parent)

    // Стандартный метод RecyclerView  - заполняет реальные данные факта в поля строчки (ID букву, значения полей)
    override fun onBindViewHolder(factViewHolderTable: FactViewHolderTable, position: Int) {
        factViewHolderTable.bind(todoViewModel, getItem(position))
    }

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         * <p>
         * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
         * detect there's only a single item difference from before, so it only needs to animate and
         * rebind a single view.
         *
         * Этот обратный вызов diff информирует адаптер PagedList о том, как вычислить различия в списках при создании нового
         * * Появляются списки постраничных сообщений.
         * <P>в
         * Когда вы добавляете сыр с помощью кнопки "Добавить", PagedListAdapter использует diffCallback для
         * обнаружьте, что есть только одно отличие элемента от предыдущего, поэтому ему нужно только анимировать и
         * повторная привязка одного вида.
         *
         * При указании diffCallback адаптер не перерисовывает не изменившиеся элементы
         * diffCallback говорит ему в каком случае строки одинаковые и их не надо перерисовывать
         * Стандартная конструкция из Recycler View, а для Paging 3.0 обязательная имеет две овериды:
         * areItemsTheSame - когда два итема одни и те же, (здесь совпадает их номер)
         * areContentsTheSame - и когда строки полностью совпадают (все поля факта совпадают)
         *
         * @see DiffUtil
         */
        private val diffCallbackTable = object : DiffUtil.ItemCallback<FactTable>() {
            override fun areItemsTheSame(oldItem: FactTable, newItem: FactTable): Boolean =
                oldItem.factId == newItem.factId
            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             * Обратите внимание, что в kotlin == проверка классов данных сравнивает все содержимое, но в Java,
             * обычно вы реализуете Object#equals и используете его для сравнения содержимого объекта.
             */
            override fun areContentsTheSame(oldItem: FactTable, newItem: FactTable): Boolean =
                oldItem == newItem
        }
    }
}

/**
 * A simple ViewHolder that can bind a Cheese item. It also accepts null items since the data may
 * not have been fetched before it is bound.
 * Простой ViewHolder, который может связать элемент сыра. Он также принимает нулевые элементы, так как данные могут
 * не были принесены до того, как он будет связан.
 * Это стандартный класс RecyclerView он просто технически удобен и укоряет RecyclerView
 */
class FactViewHolderTable private constructor(private val binding: ToDoRecyclerItemBinding)
    : RecyclerView.ViewHolder(binding.root){

    // Построитель внешнего вида помещен в статичный объект, т.к. он один и тот же для всех строчек
    // надувать каждую строчку будет долго и дорого поэтому мы надуваем статичный объект сразу при
    // старте, апотом просто суем его в каждую строчку уже надутый.
    companion object { // аналог STATIC JAVA при вызове создает экземляр класса FactViewHolder(binding)
        // Вызывается из onCreateViewHolder и строит внешний вид этих строчек (надувает)
        fun from(parent: ViewGroup): FactViewHolderTable {
            val layoutInflater = LayoutInflater.from(parent.context)
            // Надувает layout\to_do_recycler_item.xml - портрет или layout-land\to_do_recycler_item.xml - ландшафт
            // В них layout есть окружающий поэтому можно надувать databinding
            val binding = ToDoRecyclerItemBinding.inflate(layoutInflater, parent, false)
            // этот binding прередается в созданном здесь FactViewHolder через адаптер и
            // запоминается в конструкторе в возвращаемой ссылке на этот экземпляр класса FactViewHolder
            return FactViewHolderTable(binding)
            // В дальнейшем RecyclerView по этой ссылке будет вызывать fun bind этого экземпляра
            // соответственно ЭТОТ binding будет доступен в fun bind для занесения туда данных fact
        }
    }

    /**
     * Items might be null if they are not paged in yet.
     * PagedListAdapter will re-bind the ViewHolder when Item is loaded.
     * Элементы могут быть пустыми, если они еще не выгружены.
     * Адаптер PagedList повторно свяжет ViewHolder при загрузке элемента.
     * Он в надутый выше фрагмент загоняет данные с конкретного факта
     * binding. берет из класса созданного FactViewHolder(binding)
     */
    fun bind(todoViewModel: ToDoViewModel, item: FactTable?) {
        binding.fact = item                                 // в *_item.xml в переменную факт дает ссылку на факт, который высвечивать
        binding.viewmodel = todoViewModel               // в *_item.xml в переменную viewmodel дает ссылку на todoViewMode, которую вызывать
        binding.executePendingBindings()                    // говорит биндингу обновить данные сейчасже, не дожидаясь
    }
}

// ИСКЛЮЧИЛ 14,10,2020
// В codelabsTest передается адаптеру сразу private val viewModel: TasksViewModel
// обходятся без класса кликера!!! и еще удобно вызывать несколько кликеров и др.
// Объявляется класс для передачи его адаптеру
// onClick Вызывается из XML при нажатии на элемент списка RecyclerView через лямбду
/*class FactListenerTable(val clickListenerTable: (factId: Int) -> Unit) {
    // вызывается из layout\to_do_recycler_item.xml через onClick лямбду
    fun onClick(factTable: FactTable) = clickListenerTable(factTable.factId)
}*/



