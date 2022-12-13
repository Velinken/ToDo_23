package com.app4web.asdzendo.todo.ui

import androidx.databinding.InverseMethod
import com.app4web.asdzendo.todo.launcher.PAEMI
import java.text.SimpleDateFormat
import java.util.*

object BindingConverters {

    @InverseMethod(value = "convertStringToLong")
    @JvmStatic fun convertLongToString(long: Long?): String = long?.toString() ?: ""
    @JvmStatic fun convertStringToLong(text: String): Long? =
        try { text.toLong() } catch (e: NumberFormatException) { 0L }

    @InverseMethod(value = "convertStringToBoolean")
    @JvmStatic fun convertBooleanToString(boolean: Boolean?): String = boolean?.toString() ?: ""
    @JvmStatic fun convertStringToBoolean(text: String): Boolean?  = text.trim().toBoolean()

    @InverseMethod(value = "convertStringToDate")
    @JvmStatic fun convertDateToString(date: Date?): String =
            (date?:Date()).let{SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH).format(it)}
    @JvmStatic fun convertStringToDate(text: String): Date? =
            try { SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH) .parse(text) }
            catch (e: Exception) { Date()}

    // для преобразования в Table
    @InverseMethod(value = "convertStrToCalendar")
    @JvmStatic fun convertCalendarToStr(calendar: Calendar?): String =
            calendar?.let { SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(it.time) }?:"nul"
    @JvmStatic fun convertStrToCalendar(text: String): Calendar = Calendar.getInstance().let {
        try {
            it.time = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).parse(text)?:it.time
            it
        } catch (e: Exception) { it }
    }

    @InverseMethod(value = "convertStringToCalendar")
    @JvmStatic fun convertCalendarToString(calendar: Calendar?): String =
            calendar?.let { SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH).format(it.time) }?:"nul"
    @JvmStatic fun convertStringToCalendar(text: String): Calendar = Calendar.getInstance().let {
        try {
            it.time = SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH).parse(text)?:it.time
            it
        } catch (e: Exception) { it }
    }

    @InverseMethod(value = "convertStringToInt")
    @JvmStatic fun convertIntToString(value: Int): String = value.toString()
    @JvmStatic fun convertStringToInt(text: String): Int =
               try { text.toInt() }
               catch (e: NumberFormatException) { 0 }

    @InverseMethod(value = "convertStringToChar")
    @JvmStatic fun convertCharToString(char: Char?): String = char?.toString() ?: " "
    @JvmStatic fun convertStringToChar(text: String): Char? =
               try { text.toCharArray()[0] }
               catch (e: ArrayIndexOutOfBoundsException) { ' ' }

    @InverseMethod(value = "convertStringToPaemi")
    @JvmStatic fun convertPaemiToString(paemi: PAEMI?): String? = paemi?.name
    @JvmStatic fun convertStringToPaemi(text: String): PAEMI? =
            try { PAEMI.valueOf(text) }
            catch (e: IllegalArgumentException) {
                PAEMI.N
            }
}

// Пока не потребовался , т.к. впрямую указал листенер в XML:
// app:OnNavigationItemSelectedListener = "@{viewmodel::onClickBottomNavView}"

// из XML BottomNavigationView меняет(подставляет) listener на указанный (ему будет передаваться выбранный пункт меню)
// app:onNavigationItemSelected = "@{viewmodel::onClickBottomNavView}" РАБОТАЕТ
//   <!-- https://issue.life/questions/45132691 -->
/*
    @BindingAdapter("onNavigationItemSelected")
    fun setOnNavigationItemSelectedListener(view: BottomNavigationView,
                                            listener: BottomNavigationView.OnNavigationItemSelectedListener?)
            { view.setOnNavigationItemSelectedListener(listener) }
 */
