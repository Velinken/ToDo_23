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
package com.app4web.asdzendo.todo.util

/**
 * Extension functions and Binding Adapters.
 * Функции расширения и связующие адаптеры.
 * Пока не встроена
 */

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.app4web.asdzendo.todo.ui.Event
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 * Преобразует статическую java-функцию Snackbar.make() в функцию расширения на представлении.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 * Запускает сообщение snack bar, когда изменяется значение, содержащееся в событии snack bar Task Message Live.
 */
fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}

/*
 private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }
private fun setupSnackbar() {
        viewDataBinding.root.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        //view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }
 */

/*
showSnackbarMessage(R.string.task_marked_complete)
showSnackbarMessage(R.string.task_marked_active)
showSnackbarMessage(R.string.loading_tasks_error)
EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_task_message)
private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
 */