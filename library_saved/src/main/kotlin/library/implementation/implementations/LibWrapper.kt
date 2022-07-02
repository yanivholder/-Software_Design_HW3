package library.implementation.implementations

import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.ILibWrapper
import java.util.concurrent.CompletableFuture



class LibWrapper @Inject constructor(private val secureStorageFuture: CompletableFuture<SecureStorage>) : ILibWrapper {

    val db: BasicStorageManager = BasicStorageManager()

    override fun write(key: String, value: ByteArray): CompletableFuture<Unit> {
        return db.writeData(secureStorageFuture, key.toByteArray(), value)
    }


    override fun read(key: String): CompletableFuture<ByteArray?> {
        return db.readData(secureStorageFuture, key.toByteArray())
    }
}