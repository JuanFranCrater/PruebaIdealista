package com.juanfbenitez.prueba.idealista.di

import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepositoryImpl
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds the domain [PropertyRepository] abstraction to its `data`-layer implementation, so
 * use cases can depend on the interface without knowing about [PropertyRepositoryImpl]. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(impl: PropertyRepositoryImpl): PropertyRepository
}
