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
package com.app4web.asdzendo.todo.ui

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app4web.asdzendo.todo.database.FactTable
import com.app4web.asdzendo.todo.ui.todo.ToDoPageAdapterTable
//  * Пока не встроена

//  ^[WARN] Incremental annotation processing requested,
//  but support is disabled because the following processors are not incremental:
//  androidx.room.RoomProcessor (DYNAMIC).
@BindingAdapter("completedTask")
fun setStyle(textView: TextView, enabled: Boolean) {
    textView.paintFlags =
        if (enabled)  textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}
/*
 <CheckBox
            android:id="@+id/complete_checkbox"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:onClick="@{(view) -> viewmodel.completeTask(task, ((CompoundButton)view).isChecked())}"
            android:checked="@{task.completed}" />
 */


/**
 * [BindingAdapter]s for the [Task]s list.
 * [Привязка адаптера]для [задачи]список С.
 */
// warning: Application namespace for attribute app:completedTask will be ignored.
// предупреждение: пространство имен приложения для атрибута app:завершенная задача будет проигнорирована.
@BindingAdapter("items")
@SuppressWarnings("unused")
fun setItems(listView: RecyclerView, items: List<FactTable>?) {
    items?.let {
       // (listView.adapter as ToDoPageAdapterTable).submitList(items)
       // (listView.adapter as ToDoPageAdapterTable).submitData(items)
    }
}
/*
 factsPageTable.cancellable().collectLatest {
                adapterPageTable.submitData(it)
 */
/*
private val _items: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
        }
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }
 val items: LiveData<List<Task>> = _items
<androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/tasks_list"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
                        app:items="@{viewmodel.items}" />
                </LinearLayout>
 */

