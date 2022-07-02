package library.implementation

import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.util.concurrent.CompletableFuture



class LibraryImp @Inject constructor(private val secureStorageFuture: CompletableFuture<SecureStorage>, private val db: IBasicStorageManager) : Library {

    override fun write(key: String, value: ByteArray): CompletableFuture<Unit> {
        return db.writeData(secureStorageFuture, key.toByteArray(), value)
    }

    override fun read(key: String): CompletableFuture<ByteArray?> {
        return db.readData(secureStorageFuture, key.toByteArray())
    }
}