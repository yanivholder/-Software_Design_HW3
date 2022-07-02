package il.ac.technion.cs.softwaredesign

import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.IBasicStorageManager
import il.ac.technion.cs.softwaredesign.ILibWrapper
import library.implementation.implementations.BasicStorageManager
import library.implementation.modules.StorageModule
import java.util.concurrent.CompletableFuture

class MessagingClientModule: KotlinModule() {
    override fun configure() {
        install(StorageModule())

//        bind<IBasicStorageManager>().to<BasicStorageManager>()
//        bind<SecureStorage>>().to<CompletableFuture<SecureStorageFake>>()
//        bind<ILibWrapper>().to<LibWrapper>()
        bind<MessagingClientFactory>().to<MessagingClientFactoryImpl>()
//        bind<Library>().to<LibraryFake>()
    }

//    @Provides
//    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
//        return CompletableFuture.completedFuture(SecureStorageFake())
//    }
//    @Provides
//    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
//        return secureStorageFactory.open(ByteArray(0))
//    }
}