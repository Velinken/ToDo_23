/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app4web.asdzendo.todo.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.app4web.asdzendo.todo.launcher.FilterDateEnd
import com.app4web.asdzendo.todo.launcher.FilterDateStart
import com.app4web.asdzendo.todo.launcher.PAEMI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository module for handling data operations.
 * Модуль репозитория для обработки операций с данными.
 * вызывается из всех ViewModels сам вызывает FactDatabaseDao функции
 */
//@Singleton - в образце не указывается, кто-то указывает похоже достаточно в Module
class FactRepository @Inject constructor (private val factDao: FactDatabaseDao) {
   // private val applicationScope = CoroutineScope(Dispatchers.Default)

    // параметры вызова и работы Paging 3.0 т.е всего держать 210 строк, считывать по 70 строк
    private val pagingConfig = PagingConfig(  pageSize = 70, enablePlaceholders = true, maxSize = 210 )

    // Возвращает обычное Fact?
    // используется ниже в update и delete
    private suspend fun get(factID:Int): Fact? = factDao.get(factID)

    // отдает LiveData<Fact>
    // используется: Сейчас вызываем из FactDetailFragment
    fun getFactWithId(factID: Int) = factDao.getFactWithId(factID)

    suspend fun insert(fact: Fact?) =
            withContext(Dispatchers.IO) {
                var newFact = Fact()
                if (fact != null) newFact = fact
                newFact.factId = 0
                factDao.insert(newFact)
            }

    suspend fun update(fact: Fact?) =
            withContext(Dispatchers.IO) {
                if (fact != null)
                   if (get(fact.factId) != null)
                        factDao.update(fact)
            }

    suspend fun delete(fact: Fact?) =
            withContext(Dispatchers.IO) {
                if (fact != null)
                    if (get(fact.factId) != null)
                        factDao.delete(fact)
            }

    suspend fun clear() =
        withContext(Dispatchers.IO) {
            factDao.clear() // Очистить базу данных
        }

    // Вызывается из ToDoActivityViewModel и наблюдается в ToDoActivity (это LifeData)
    // !!!! Не могу поставить в поток - не знаю как и надо ли?
    fun count() = factDao.getCount()

    // !!!! Вот это построение не отменяется при изменении буквы ...
    // вызывается из ToDoViewModel для построения factsPage: Flow<PagingData<Fact>> через поток пейдинг 3.0
    // .flow каким-то образом сам ставит поток, который я не знаю как отменить
    fun getAllPageTableCollect(paemi:PAEMI) =
            when (paemi) {
                PAEMI.R -> Pager(pagingConfig) {  getAllPageTable()  }           //.flow
                PAEMI.N -> Pager(pagingConfig) { getAllFactsPageTable() }        //.flow
                else -> Pager(pagingConfig) { getAllPAEMIFactsPageTable(paemi) } //.flow
            }

    // отдает PagingSource<Int, Fact> ORDER BY factId DESC
    private fun getAllPageTable() = factDao.getAllPageTable()

    // отдает PagingSource<Int, Fact>  ORDER BY data ASC
    private fun getAllFactsPageTable() = factDao.getAllFactsPageTable()

    // Основной фильтр по PAEMI отдает PagingSource<Int, Fact>
    //  WHERE paemi = :paemi AND data BETWEEN :FilterDateStart AND :FilterDateEnd ORDER BY data DESC, factId DESC
    private fun getAllPAEMIFactsPageTable(paemi: PAEMI) =
            factDao.getAllPAEMIFactsPageTable(paemi,FilterDateStart,FilterDateEnd)

 //==================================заполнение базы ===============================================
 // вызывается из меню ... дозаполнить пачку через ToDoViewModel в viewModelScope.launch
     suspend fun addFactDatabase(countFacts: Int = 1000) {
         // заполнение иде пачками пл 10 000 * 8 в корутинах но по очереди, т.к. не хватает памяти под список
         withContext(Dispatchers.IO) {
             val factList1000 = factContent(if(countFacts >= 10_000) 10_000 else countFacts)
             for (count in 1..(countFacts / 10_000)) {
                 val addCount = factDao.insertAll(factList1000)
                 Timber.i("ToDoFactRepository Add Database Строк записи =  ${addCount.size} $count --> ${countFacts / 10_000}")
             }
             // дозаполнение остатка от деления на 10 000 (это в suspende) а еще и в корутине потоке)
             val addCount = factDao.insertAll(factContent(countFacts % 10000))
             Timber.i("ToDoFactRepository Add Database Последняя запись = $countFacts * 8 = ${addCount.size}")
         }
     }

    // создание списка дополнительной пачки строк для базы в количестве countFacts * 8
    private fun factContent(count: Int = 100): List<Fact>  {
        // создается пустой добавляемый список для фактов
        val facts: MutableList<Fact> = ArrayList()
        // Add some sample items.
        // для каждого факта из количества затребованных
        for (id in 1..count)
            // для каждой буковки 0..8 PAEMI
            for (paemi in PAEMI.values()) {
                // создать новый факт из класса факта ( и он заполнится полями по умолчанию)
                val fact = Fact(paemi = paemi, nameShort = "$id Факт", name = "Факт полностью: $id")
                // заполнить дату факта случайным числом в заданных пределах
                with (fact) {
                    data = GregorianCalendar.getInstance()
                    data.set(GregorianCalendar.YEAR,(2000..2040).random())
                    data.set(GregorianCalendar.DAY_OF_YEAR,(1..365).random())
                }
                // добавить в список очередной заполненный факт
                facts.add(fact)
            }
        Timber.i("ToDoFactRepository Add List Строк записи = $count * 8 = ${count * 8}")
        // вернуть вызывающему заполненный список фактов
        return facts
    }
}
