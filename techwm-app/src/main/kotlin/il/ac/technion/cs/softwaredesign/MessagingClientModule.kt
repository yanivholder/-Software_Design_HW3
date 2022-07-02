package il.ac.technion.cs.softwaredesign

import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.IBasicStorageManager
import library.implementation.implementations.BasicStorageManager
import library.implementation.modules.StorageModule
import java.util.concurrent.CompletableFuture

class MessagingClientModule: KotlinModule() {
    override fun configure() {
        install(StorageModule())

        bind<MessagingClientFactory>().to<MessagingClientFactoryImpl>()
    }

}