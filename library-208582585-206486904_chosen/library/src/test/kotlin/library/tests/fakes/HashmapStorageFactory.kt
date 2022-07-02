package library.tests.fakes

import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import java.util.concurrent.CompletableFuture

class HashmapStorageFactory : SecureStorageFactory {
    private val databases = hashMapOf<String,SecureStorage>()

    override fun open(name: ByteArray): CompletableFuture<SecureStorage> {
        var database = databases[String(name)]
        if (database == null) {
            database = StorageHashMap()
            databases[String(name)] = database
        }
        return CompletableFuture.completedFuture(database)
    }
}