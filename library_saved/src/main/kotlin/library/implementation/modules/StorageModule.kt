package library.implementation.modules

import com.google.inject.Provides
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.*
import library.implementation.implementations.*
import library.implementation.utils.*
import java.util.concurrent.CompletableFuture

import library.implementation.modules.SecureStorageFake

class StorageModule : KotlinModule() {
    override fun configure() {
        bind<IBasicStorageManager>().to<BasicStorageManager>()
        bind<ILibWrapper>().to<LibWrapper>()

        // TODO REMOVE
        bind<SecureStorage>().to<SecureStorageFake>()
    }

//    @Provides
//    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
//        return secureStorageFactory.open(ByteArray(0))
//    }
}