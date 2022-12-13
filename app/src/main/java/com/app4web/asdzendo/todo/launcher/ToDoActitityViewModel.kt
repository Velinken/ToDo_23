package com.app4web.asdzendo.todo.launcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app4web.asdzendo.todo.database.FactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Identifies a androidx.lifecycle.ViewModel's constructor for injection.
 * Идентифицирует android.lifecycle.Конструктор ViewModel для инъекций.
 * Similar to javax.inject.Inject,
 * a ViewModel containing a constructor annotated with ViewModelInject will have its dependencies
 * defined in the constructor parameters injected by Dagger's Hilt.
 * The ViewModel will be available for creation by the HiltViewModelFactory
 * and can be retrieved by default in an Activity or Fragment annotated with AndroidEntryPoint .
 * ViewModel, содержащий конструктор с аннотацией ViewModelInject, будет иметь свои зависимости,
 * определенные в параметрах конструктора, введенных рукоятью кинжала.
 * ViewModel будет доступен для создания HiltViewModelFactory
 * и может быть извлечен по умолчанию в действии или фрагменте, аннотированном AndroidEntryPoint .
 * Example:
 * public class DonutViewModel extends ViewModel {
 * @ViewModelInject
 * public DonutViewModel(@Assisted SavedStateHandle handle, RecipeRepository repository) {
 * // ...
 * }
 * }
 *
 * @AndroidEntryPoint
 * public class CookingActivity extends AppCompatActivity {
 * public void onCreate(Bundle savedInstanceState) {
 * DonutViewModel vm = new ViewModelProvider(this).get(DonutViewModel.class);
 * }
 * }
 *
 * Only one constructor in the ViewModel must be annotated with ViewModelInject.
 * The constructor can optionally define a androidx.hilt.Assisted-annotated androidx.lifecycle.
 * SavedStateHandle parameter along with any other dependency.
 * The SavedStateHandle must not be a type param of javax.inject.Provider nor Lazy
 * and must not be qualified.
 * Только один конструктор в ViewModel должен быть аннотирован с помощью ViewModel Inject.
 * Конструктор может дополнительно определить androidx.hilt.Assisted-аннотированный androidx.lifecycle.
 * Параметр Savestatehandler вместе с любой другой зависимостью.
 * Сохраненный дескриптор состояния не должен быть типом param javax.inject.
 * Поставщик ни ленив и не должен быть квалифицированным..
 * Only dependencies available in the ActivityRetainedComponent  can be injected into the ViewModel.
 * Только зависимости, доступные в компоненте Activity Retained, могут быть введены в ViewModel.
 */

@HiltViewModel
class ToDoActitityViewModel @Inject constructor(
       private val factRepository: FactRepository
): ViewModel() {
    // Наблюдается (т.к. это LifeData) из ToDoActivity
    val count:LiveData<Int> = factRepository.count()
    init {
        Timber.i("TODOActitityViewModel created")
    }
}