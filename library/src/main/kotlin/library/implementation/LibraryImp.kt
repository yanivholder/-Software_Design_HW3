package library.implementation

import com.google.inject.Guice
import com.google.inject.Inject
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.implementations.BasicStorageManager
import library.implementation.modules.StorageModule
import java.lang.invoke.SerializedLambda
import java.util.concurrent.CompletableFuture



//class LibraryImp @Inject constructor(private val secureStorageFuture: CompletableFuture<SecureStorage>) : Library {
class LibraryImp : Library {

//    private val injector = Guice.createInjector(StorageModule())
//    private var secureStorageFuture = injector.getInstance<CompletableFuture<SecureStorage>>()
    val secureStorageFuture: CompletableFuture<SecureStorage> = CompletableFuture.completedFuture(SecureStorageFake())

    val db: BasicStorageManager = BasicStorageManager()

    override fun write(key: String, value: ByteArray): CompletableFuture<Unit> {
        return db.writeData(secureStorageFuture, key.toByteArray(), value)
    }


    override fun read(key: String): CompletableFuture<ByteArray?> {
        return db.readData(secureStorageFuture, key.toByteArray())
    }
}