package com.app4web.asdzendo.todo.ui.todo
/*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app4web.asdzendo.todo.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest= Config.NONE)
@RunWith(AndroidJUnit4::class)
class ToDoViewModelTest {
    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    //  Когда вы пишете тесты, включающие тестирование LiveData, используйте это правило!
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var toDoViewModel: ToDoViewModel
    // XXXXX  val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext()) xxx- нужен свежий
    @Before
    fun setUp() {
        toDoViewModel = ToDoViewModel(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
    }

    // проверит, что при вызове addNewTask метода Event запускается окно открытия новой задачи
    @Test
    fun fabClick_setsNewFactEvent() {

        // Given a fresh ViewModel Учитывая свежий взгляд модели
        // в @Before val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task При добавлении новой задачи
        toDoViewModel.fabClick()

        // На этом шаге вы используете getOrAwaitValue метод и пишете инструкцию assert,
        // которая проверяет, newTaskEvent был ли запущен.
        // Then the new task event is triggered Затем запускается новое событие задачи
        // тодо test LiveData см LiveDataTestUtil
        val value = toDoViewModel.navigateToFactDetail.getOrAwaitValue()

        assertThat(
            // value.getContentIfNotHandled(),
            value,
            not(nullValue())
        )
    }
        // Что такое getContentIfNoteHandled?
        //В приложении TO-DO вы используете настраиваемый Event класс для LiveData представления одноразовых событий
        // (таких как навигация или всплывающая закусочная) getContentIfNotHandled предоставляет «разовую» возможность.
        // При первом вызове он получает содержимое файла Event.
        // Любые дополнительные вызовы getContentIfNotHandled того же контента будут возвращены null.
        // Вот как Event осуществляется доступ к данным в коде приложения, и поэтому мы используем его для тестов.
        // Вы можете узнать больше о событиях здесь .
        // https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
}
*/

