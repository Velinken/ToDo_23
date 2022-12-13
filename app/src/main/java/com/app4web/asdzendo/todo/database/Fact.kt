/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app4web.asdzendo.todo.database

import androidx.room.*
import com.app4web.asdzendo.todo.launcher.PAEMI
import java.util.*

/**
 * Сущность: вместо того, чтобы создавать таблицу SQLite, мы создадим сущность.
 * Entity - это не что иное, как класс модели с аннотацией @Entity.
 * Переменные этого класса - это наши столбцы, а класс-наша таблица.
 */

/**
 * Represents one record of the fact of life of an idea (I), plan(P), action(A), event(Y), money(M).
 * Представляет собой одину запись факта жизни: идеи(I), плана(P), действия(A), события(E), денег(M).
 */
/**
 * @Entity говорит, что это ROOM база данных типа SQLite, т.е. таблица на HDD,
 * а точнее одна строка этой таблицы это data class, который указан ниже
 * В этом классе перечислены поля таблицы, какое из них первое поле, индексы сортировки
 * может быть еще много чего в т.ч. функции, поля не входящие в таблицу и др и т.п.
 * @Entity(tableName = "fact_todo", - имя таблицы
 * indices = [Index(value = ["paemi", "data", "factId"],name = "date_id_index")]) - ключь сортировки
 * @TypeConverters(CalendarConverters::class, PaemiConverters::class) - преобразование полей таблицы туда-обратно
 * Здесь указано, что преобразование лежат в классах: CalendarConverters и PaemiConverters (DatabaseConverters.kt)
 *
 */
//@Fts4           // Полнотекстовый поиск Дает ОШИБКУ
@Entity(tableName = "fact_todo",
        indices = [Index(value = ["paemi", "data", "factId"])])
@TypeConverters(CalendarConverters::class, PaemiConverters::class)
data class Fact(

        //  factId - Обязательный основной ключь @PrimaryKey и иметь сортировку по нему index = true
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(index = true)
        var factId: Int = 0,  // пускай нумеруется сам -  никогда не меняется (надо бы не удалять запись 0L)
        @ColumnInfo(index = true)
        var data: Calendar = GregorianCalendar.getInstance(), // System.currentTimeMillis(),  // Сейчас - дата и время создания записи
        var parent: Int = 0, // Какая запись (неважно какого типа) породила эту
        @ColumnInfo(index = true)
        var paemi: PAEMI = PAEMI.N, //PAEMI.N.ordinal,  // идеи(I), плана(P), действия(A), события(E), денег(M) служебная(S)
        var nameShort: String = "", // Факт PAEMI кратко
        var name: String = "",      // Факт PAEMI Полностью
        var rezult: String = "", // ожидаемый или полученный или получившийся РЕЗУЛЬТАТ
        var toWork: Boolean? = false, // закпущена в работу или снята с работы(отказ или удалена)
        var type: Int = 0, // List<Long>, // Номер ссылки в доп справочнике, нужно множественную
        var dataStart: Calendar = GregorianCalendar.getInstance(), //Date.from(LocalDateTime.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        var dataEnd: Calendar = GregorianCalendar.getInstance(), //LocalDate,
        var deadLine: Calendar = GregorianCalendar.getInstance(), // = data, //LocalDate,
        var duration: Long? = dataStart.timeInMillis - dataEnd.timeInMillis,
        var resources: String = "",
        var money: Int = 0,
        var close: Boolean? = false,  // Факт закрыт
        var defect: String = "",  // Халтура недоделки
        //  var parentList: List<Long>,   // Ссылки на родителей надо делать ?
        //  var childList: List<Long>,    // Ссылка на подчиненных надо делать ?
        var comment: String = "",       // просто свободный комментарий для User
        var system: String = "",       // Специальный информация только для меня
        var url: String = "https://developer.android.com/guide", //List<URL>
        // Можно добавлять, изменять переставлять, но базу заполнять заново после этого или программу перехода по версиям базы
)

/**
 * Все поля одной строчки базы нужны будут в форме detail, там мы их и считываем сейчас
 * В таблице нам не надо светить все поля базы, а надо только 5-7 полей, другие мы не видим
 * Идея - зачем их читать из базы данных когда свайпим, сортируем и т.п. таблицу, создаем класс
 * из этих 5-7-ми полей и под Recycler читаем из базы только эти поля от каждой строчки,
 * экономится память, скорость и комильфо
 * Android SQLite @Entity Имеет для этого даже особую форму записи, т.е. это распространенный стандарт
 */
data class FactTable(
        @ColumnInfo(name = "factId")
        var factId: Int = 0,  // пускай нумеруется сам -  никогда не меняется (надо бы не удалять запись 0L)
        @ColumnInfo(name = "data")
        var data: Calendar = GregorianCalendar.getInstance(), // Сейчас - дата и время создания записи
        @ColumnInfo(name = "parent")
        var parent: Long = 0L, // Какая запись (неважно какого типа) породила эту
        @ColumnInfo(name = "paemi")
        var paemi: PAEMI = PAEMI.N, //PAEMI.N.ordinal,  // идеи(I), плана(P), действия(A), события(E), денег(M) служебная(S)
        @ColumnInfo(name = "nameShort")
        var nameShort: String = "", // Факт PAEMI кратко
        @ColumnInfo(name = "rezult")
        var rezult: String = "", // ожидаемый или полученный или получившийся РЕЗУЛЬТАТ
)

/*  Можно рассмотреть тело класса { val .... get() = ....} - понять зачем
{
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}
 */