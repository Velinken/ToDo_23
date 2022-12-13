package com.app4web.asdzendo.todo.database

import androidx.room.TypeConverter
import com.app4web.asdzendo.todo.launcher.PAEMI
import java.util.*

/**
 * В SQL таблице можно хранить только string, long, int, и немного что, только базовое
 * В SQL таблице нельзя хранить enum, data: Calendar, ничего сложного
 * Поэтому при записи в таблицу преобразовать сложное в простое Exp: data -> long
 * Поэтому при чтении из таблицы надо преобразовать простое в сложное Exp: long -> data
 * @TypeConverters именно это и делает конвертеры база <--> переменная
 * (!не путать с BindingConverters! - это конверторы переменная <--> экран)
 */
// @TypeConverters(DatabaseConverters::class)
/**
 * @TypeConverters аннотация должна использоваться, когда мы объявляем свойство,
 * тип которого является пользовательским классом, списком, типом даты или любым другим типом, который Room и SQL не знают, как сериализовать.
 * В этом случае мы используем аннотацию на уровне поля класса, причем только это поле сможет ее использовать.
 * В зависимости от того, где находится аннотация, она будет вести себя по-разному, как описано здесь.
 *
Если вы поставите его на Database, все DAO и сущности в этой базе данных смогут использовать его.
Если вы поставите его на Dao, все методы в Дао смогут использовать его.
Если вы поставите его на Entity, все поля сущности смогут использовать его.
Если вы поместите его на POJO, все поля POJO смогут использовать его.
Если вы поставите его на Entity поле, только это поле сможет его использовать.
Если вы поставите его на Dao метод, все параметры метода будут иметь возможность использовать его.
Если вы поставите его на Dao параметр метода, только это поле сможет его использовать.
 */

/*
Помечает метод как преобразователь типов.
Класс может иметь столько методов @TypeConverter, сколько ему нужно.
Каждый метод конвертера должен получать 1 параметр и иметь тип возврата non-void.
 */

class PaemiConverters {
    @TypeConverter fun PaemiToInt(paemi: PAEMI?): Int = paemi?.ordinal?:0
    @TypeConverter fun IntToPaemi(int: Int?): PAEMI = PAEMI.values()[int?:0]
}

class CalendarConverters {
    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis
    @TypeConverter fun datestampToCalendar(value: Long): Calendar =
            Calendar.getInstance().apply { timeInMillis = value }
}

