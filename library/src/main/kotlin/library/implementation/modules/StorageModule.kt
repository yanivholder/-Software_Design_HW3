package library.implementation.modules
import com.google.inject.Provides
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import library.implementation.*
import library.implementation.implementations.*
import library.implementation.utils.*
import java.util.concurrent.CompletableFuture

class StorageModule : KotlinModule() {
    override fun configure() {
//        bind<IBasicStorageManager>().to<BasicStorageManager>()
//        install(SecureStorageModule())

        bind<SecureStorage>().to<SecureStorageFake>()
        bind<Library>().to<LibraryImp>()
    }

//    @Provides
//    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
//        return secureStorageFactory.open(ByteArray(0))
//    }


}