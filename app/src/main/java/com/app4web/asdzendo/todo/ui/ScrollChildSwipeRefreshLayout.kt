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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app4web.asdzendo.todo.R

/**
 * Extends [SwipeRefreshLayout] to support non-direct descendant scrolling views.
 * Расширяет [SwipeRefreshLayout] для поддержки непрямых видов прокрутки потомков.
 *
 *
 * [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 * [SwipeRefreshLayout] работает так, как и ожидалось, когда вид прокрутки является прямым дочерним элементом: он запускает
 * обновление происходит только тогда, когда вид находится сверху. Этот класс добавляет способ (@link #setScrollUpChild}, чтобы
 * определить, какое представление управляет этим поведением.
 * Пока не встроена
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    var scrollUpChild: View? = null

    override fun canChildScrollUp() =
        scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
}

/* XML:
<com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
            android:id="@+id/refresh_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:enabled="@{viewmodel.dataLoading}"
            app:refreshing="@{viewmodel.dataLoading}">

<com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
            android:id="@+id/refresh_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:onRefreshListener="@{viewmodel::refresh}"
            app:refreshing="@{viewmodel.dataLoading}">

</com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout>
 */

/* в фрагменте:
this.setupRefreshLayout(viewDataBinding.refreshLayout)
<com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
        android:id="@+id/refresh_layout"
        app:refreshing="@{viewmodel.dataLoading}"
        app:onRefreshListener="@{viewmodel::refresh}"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
</com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout>

this.setupRefreshLayout(viewDataBinding.refreshLayout)
<com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
        android:id="@+id/refresh_layout"
        app:refreshing="@{viewmodel.dataLoading}"
        app:onRefreshListener="@{viewmodel::refresh}"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
</com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout>
 */
fun Fragment.setupRefreshLayout(
        refreshLayout: ScrollChildSwipeRefreshLayout,
        scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
            ContextCompat.getColor(requireActivity(), R.color.colorAccent),
            ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    // Установите вид прокрутки в пользовательском SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}