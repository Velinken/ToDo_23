/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app4web.asdzendo.todo

/**
 * Совет. Когда вы пишете свои собственные тесты для тестирования LiveData,
 * вы можете аналогичным образом копировать и использовать этот класс в своем коде.
 */
/*
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
*/
/**
 * Это довольно сложный метод.
 * Он создает вызываемую функцию расширения Kotlin, getOrAwaitValue которая добавляет наблюдателя,
 * получает LiveData значение и затем очищает наблюдателя
 */

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 * Получает значение [оперативные данные] или ждет его, чтобы иметь один, с тайм-аутом.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 * Используйте это расширение из тестов на стороне хоста (JVM). Рекомендуется использовать его рядом с собой
 * `Мгновенное правило TaskExecutor` или аналогичный механизм для асинхронного выполнения задач.
 *
 * функцию расширения, призванную LiveDataTestUtil упростить добавление наблюдателей
 */
/*
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        // Не ждите бесконечно, если текущие данные не установлены.

        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

 */

/**
   * в основном это короткая, многоразовая версия observeForever кода, показанного ниже.
   * Полное объяснение этого класса можно найти в этом сообщении в блоге .
   * https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04
 */

/* Это много шаблонного кода, чтобы увидеть сингл LiveData в тесте!
@Test
fun addNewTask_setsNewTaskEvent() {
    // Given a fresh ViewModel Учитывая свежий взгляд модели
    val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    // Create observer - no need for it to do anything! Создайте наблюдателя - ему не нужно ничего делать!
    val observer = Observer<Event<Unit>> {}
    try {
        // Observe the LiveData forever Наблюдайте за живыми данными вечно
        tasksViewModel.newTaskEvent.observeForever(observer)
        // When adding a new task При добавлении новой задачи
        tasksViewModel.addNewTask()
        // Then the new task event is triggered Затем запускается новое событие задачи
        val value = tasksViewModel.newTaskEvent.value
        assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
    } finally {
        // Whatever happens, don't forget to remove the observer! Что бы ни случилось, не забудьте убрать наблюдателя!
        tasksViewModel.newTaskEvent.removeObserver(observer)
    }
}
 */


