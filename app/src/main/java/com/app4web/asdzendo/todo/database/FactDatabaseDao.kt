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

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.app4web.asdzendo.todo.launcher.PAEMI
import java.util.*

/**
 * Defines methods for using the Fact class with Room.
 * Определяет методы для использования класса Fact с Room.
 * Здесь стоят SQL запросы к базе, все которые к ней выдаются
 * DAO: расшифровывается как объект доступа к данным.
 * Это интерфейс, который определяет все операции, которые мы должны выполнить в нашей базе данных.
 * Внимание! Это интерфейс, а не класс и не операторы, он реализуется другими классами
 * для целей Hilt не аннотируется но указывается в di/FactModule
 */
@Dao
@TypeConverters(CalendarConverters::class, PaemiConverters::class)
interface FactDatabaseDao {

    // Вызывается из репо, а оно из ViewModel, а оно наблюдается из ToDoActivity
    // Справочно: количество строк в таблице:  SELECT count(*) FROM employee;
    // это обращение к базе данных SQL Lite c просьбой дать количество записей всех в базе fact_todo
    @Query("SELECT COUNT(*) FROM fact_todo")
    fun getCount(): LiveData<Int>

    /**
     * Добавляет в базу сразу список готовых фактов
     * применяется сейчас для заполнения отладочными строками
     * подготовка строк в репозитории
     * она suspend, т.е. функция приостановки должна вызываться из отдельного потока
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facts: List<Fact>): List<Long>

    // стандарт : чтобы был нарастающий ID fact.factid = 0L обязательно или null
    @Insert  // (onConflict = OnConflictStrategy.REPLACE) - будет всавлять или затирать существующую
    suspend fun insert(fact: Fact): Long

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     * При обновлении строки со значением, уже установленным в столбце,
     * заменяет старое значение на новое.
     *
     * @param fact new value to write
     */
    @Update // если не находит заппись то ничего не будет int - количество обновленных записей
    suspend fun update(fact: Fact): Int

    @Delete  // если нет записи - ошибка, int - кол-во удаленных записей
    suspend fun delete(fact: Fact): Int

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     * Выбирает и возвращает строку, которая соответствует предоставленному id  что является ключом.
     *
     * @param id startTimeMilli to match
     * Ей передают номер ID она возвращает data class Fact
     * используется
     */
    @Query("SELECT * from fact_todo WHERE factId = :id")
     suspend fun get(id: Int): Fact?

    /**
     * Deletes all values from the table.
     * Удаляет все значения из таблицы.
     *
     * This does not delete the table, only its contents.
     * При этом таблица не удаляется, а только ее содержимое.
     * Сейчас висит на трех точках
     */
    @Query("DELETE FROM fact_todo")
    suspend fun clear()

    /**
     * Selects and returns the night with given nightId.
     * Выбирает и возвращает fact c id.
     * используется: Сейчас вызываем из FactDetailFragment
     */
    //  @org.jetbrains.annotations.NotNull()
    @Query("SELECT * from fact_todo WHERE factId = :id ")  // Limit 1
    fun getFactWithId(id: Int): LiveData<Fact>

    // --------------------------- TABLE ---------------------------------------

    /**
     * Это запрос дай все строки сортированные по факт ID в обратном порядке с уменьшением
     * Ответ отдается Paging 3.0 частями, как ему надо сам будет запрашивать
     * т.к. новый ROOM знает Paging 3.0 и они дружат
     */
    //  *****************   R запрос при старте и из шторки ********
    @Query("SELECT factId,data,parent,paemi,nameShort,rezult FROM fact_todo ORDER BY factId DESC")
    fun getAllPageTable(): PagingSource<Int, FactTable>

    /**
     * Это запрос дай все строки сортированные по дате по возрастанию
     * Ответ отдается Paging 3.0 частями, как ему надо сам будет запрашивать
     * т.к. новый ROOM знает Paging 3.0 и они дружат
     */
    //  *****************   N запрос при старте и при повторном нажатии на нижнюю букву ********
    @Query("SELECT factId,data,parent,paemi,nameShort,rezult FROM fact_todo ORDER BY data ASC")
    fun getAllFactsPageTable(): PagingSource<Int, FactTable>

    /**
     * Это запрос дай все строки буковки PAEMI  с даты по дату сортированные по дате в обратном порядке с уменьшением и по ID
     * Ответ отдается Paging 3.0 частями, как ему надо сам будет запрашивать
     * т.к. новый ROOM знает Paging 3.0 и они дружат
     */
    //  ************************   PAEMI - основной запрос по нижней переключатель *****************
    @Query("SELECT factId,data,parent,paemi,nameShort,rezult FROM fact_todo WHERE paemi = :paemi" +
            " AND data BETWEEN :FilterDateStart AND :FilterDateEnd ORDER BY data DESC, factId DESC")
    fun getAllPAEMIFactsPageTable(paemi: PAEMI, FilterDateStart: Calendar, FilterDateEnd: Calendar): PagingSource<Int, FactTable>

}
