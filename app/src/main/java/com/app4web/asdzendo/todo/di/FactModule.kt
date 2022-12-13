package com.app4web.asdzendo.todo.di

import android.content.Context
import com.app4web.asdzendo.todo.database.FactDatabase
import com.app4web.asdzendo.todo.database.FactDatabaseDao
import com.app4web.asdzendo.todo.database.FactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль для HILT
 * Fragment <- ViewModel <- ViewModelFactory <- provideToDoActitityViewModelFactory
 * <- getFactRepository <- FactRepository <- FactDatabase <- FactDatabaseDao <- @Entity Fact
 */

@Module
@InstallIn(SingletonComponent::class)
object FactModule {

    @Singleton
    @Provides
    // Вызывает создание/восстановление ссылки на ьазу данных через HILT
    fun provideFactDatabase(@ApplicationContext appContext: Context) = FactDatabase.getInstance(appContext)

    @Singleton
    @Provides
    // указывет на привязку к HILT интерфейса DAO из созданнной базы данных
    fun provideFactDatabaseDao(factDatabase: FactDatabase) = factDatabase.factDatabaseDao()

    @Singleton
    @Provides
    // Вызывает создание/восстановление ссылки на этот репозиторий с привязкой FactDatabaseDao
    fun provideFactRepository(factDatabaseDao: FactDatabaseDao) =  FactRepository(factDatabaseDao)
}

/*  Для Инета на будущее пока не используется
    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): CharacterService = retrofit.create(CharacterService::class.java)

    @Singleton
    @Provides
    fun provideCharacterRemoteDataSource(characterService: CharacterService) = CharacterRemoteDataSource(characterService)
*/

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 * Статические методы используются для введения классов, необходимых для различных действий и фрагментов.
 * Context - надо исправлять
 *   Не стоит передавать Activity в модель в качестве Context. Это может привести к утечкам памяти.
 *   Если вам в модели понадобился объект Context, то вы можете наследовать не ViewModel, а AndroidViewModel.
 */

