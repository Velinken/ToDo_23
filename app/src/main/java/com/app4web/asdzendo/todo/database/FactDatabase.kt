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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app4web.asdzendo.todo.launcher.BASE_IN_MEMORY
import com.app4web.asdzendo.todo.launcher.FACT_TODO_DATABASE_NAME



/**
 * * This pattern is pretty much the same for any database, so you can reuse it.
 * Этот шаблон практически одинаков для любой базы данных, поэтому вы можете использовать его повторно.
 *
 * @Database - говорит, что это база данных на каком классе она стоит RoomDatabase
 * A database that stores Fact information.
 * База данных, которая хранит информацию о фактах жизни.
 * And a global method to get access to the database.
 * И глобальный метод получения доступа к базе данных.
 */

/**
* вызывается из ToDoInjectorUtils для создания/подключения базы данных
* при создания/подключения FactRepository через getFactRepository
* для передачи в ViewModel через ViewModelFactory
* а ViewModel создаются/подключаются из фрагментов
* Fragment <- ViewModel <- ViewModelFactory <- provideToDoActitityViewModelFactory
* <- getFactRepository <- FactRepository <- FactDatabase <- FactDatabaseDao <- @Entity Fact
* База данных: это абстрактный класс, в котором мы определяем все наши сущности.
*/
@Database(entities = [Fact::class], version = 1, exportSchema = false)
//@TypeConverters(CalendarConverters::class, PaemiConverters::class)  // указаны в Fact.kt в @Entity
abstract class FactDatabase : RoomDatabase() {
    /**
     * Connects the database to the DAO.
     * Подключает базу данных к DAO. - база данных должна знать о DAO
     */
    abstract fun factDatabaseDao(): FactDatabaseDao
    //abstract val factDatabaseDao: FactDatabaseDao
    /**
     * Define a companion object, this allows us to add functions on the FactDatabase class.
     * Определите сопутствующий объект, это позволит нам добавить функции в класс базы данных Fact.
     *
     * For example, clients can call `FactDatabase.getInstance(context)`
     * to instantiate a new FactDatabase.
     * Например, клиенты могут вызвать "базу данных фактов".getInstance (контекст)`
     * создать экземпляр новой базы данных фактов.
     */

    companion object {           // Это статика, если говорить по java
        /**
         * INSTANCE will keep a reference to any database returned via getInstance.
         * Экземпляр будет хранить ссылку на любую базу данных, возвращенную через getInstance.
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         * Это поможет нам избежать многократной инициализации базы данных, которая является дорогостоящей.
         *
         *  The value of a volatile variable will never be cached, and all writes and
         *  reads will be done to and from the main memory. It means that changes made by one
         *  thread to shared data are visible to other threads.
         *  Значение переменной volatile никогда не будет кэшироваться,
         *  а все записи и записи будут выполняться в режиме реального времени.
         * чтение будет выполняться в основную память и из нее.
         * Это означает, что изменения, внесенные одним потоком к общим данным виден другим потокам.
         */
        // For Singleton instantiation
        //  Для одноэлементный экземпляр SunFlower
        // instance хранит и отдает ссылку на FactDatabase по требованию getInstance
        @Volatile private var instance: FactDatabase? = null
        // INSTANCE Переменная будет хранить ссылку на базу данных, когда один экземпляр был создан.
        // Это поможет вам избежать повторного открытия соединений с базой данных, что дорого.

        /**
         * Helper function to get the database.
         * Вспомогательная функция для получения базы данных.
         *
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         * Если база данных уже была получена, то будет возвращена предыдущая база данных.
         * В противном случае создайте новую базу данных.
         *
         * This function is threadsafe, and callers should cache the result for multiple database
         * calls to avoid overhead.
         * Эта функция ориентирована на многопотоковое исполнение,
         * и нужно кэшировать результаты для вызова нескольких баз данных,
         * чтобы избежать накладных расходов.
         *
         * This is an example of a simple Singleton pattern that takes another Singleton as an argument in Kotlin.
         * Это пример простого Одноэлементного паттерна, который принимает другой Одноэлемент в качестве аргумента в Kotlin.
         *
         * To learn more about Singleton read the wikipedia article:
         * Чтобы узнать больше о синглтоне читайте статью в Википедии:
         * https://en.wikipedia.org/wiki/Singleton_pattern
         *
         * @param context The application context Singleton, used to get access to the filesystem.
         * @param context одноэлементный контекст приложения, используемый для получения доступа к файловой системе.
         */
        // getInstance() метод с Context параметром, который понадобится построителю базы данных.
        fun getInstance(context: Context): FactDatabase =   // если instance не null т.е. открыта то вернуть ссылку на нее
            instance ?: synchronized(this) {     // только один поток выполнения одновременно может войти в этот блок кода,
                instance ?: buildDatabase(context).also { instance = it } // заодно запомнить ссылку в instance на будущее
            // если ссылки нет, то вызвать создание новой/открытие сукществующей БД и вернуть ссылку на нее
            }
        // Создайте и предварительно заполните базу данных.
        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): FactDatabase =
            if (BASE_IN_MEMORY) // Это из ToDoCONSTANTS.kt
                // Создать новую базу в памяти
            Room.inMemoryDatabaseBuilder(context, FactDatabase::class.java)
                    .fallbackToDestructiveMigration()
                    .build()
            else
                // Открыть на диске или создать, если ее нет с именем FACT_TODO_DATABASE_NAME из ToDoCONSTANTS.kt
                Room.databaseBuilder(context, FactDatabase::class.java, FACT_TODO_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
    }
}
/** Теперь из HILT di FactModule
 * Мы вызываем getInstance чтобы получить базу данных и открыть ее
 * Если ее не существует, то она создается в памяти или на диске
 * Если она есть - она открывается этой базы данных
 * Если мы уже просили getInstance в этом сеансе и база данных открыта, то возвращается ссылка на
 * открытую базу данных, а не открывается еще раз
 * при окончании программы, ROOM сам закроет базу данных.
 * В нашем случае первое открытие / создание базы данных идет в ToDoActivity при создании ViewModel
 * Фрагменты создавая свои ViewModels тоже вызывают getInstance, но т.к. ToDoActivity ее уже открыла,
 * то они получают ссылку на нее.
 * При повороте смартфона ToDoActivity и Fragment будет убит, вместе со ссылкой на открытую базу данных,
 * но ссылка убита не будет она хранится тут в :  private var instance: FactDatabase?
 * Создастся новый ToDoActivity, он получит ссылку на не убитую ViewModel, а она получит ссылку на открытую базу.
 * ViewModels и Репозитории действуют по той же схеме
 * Это обязательная технология паттерна MVVM
 * Наверное создадут ktx-androidx идиомы и этот модуль исчезнет. Проверять.
 */
