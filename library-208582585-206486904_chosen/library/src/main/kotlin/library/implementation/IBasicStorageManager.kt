package library.implementation

import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.util.concurrent.CompletableFuture

interface IBasicStorageManager {
    /**
     * Store the [data] in the database with [key].
     *
     * @param secureStorage the storage to query.
     * @param key the key to associate with the data.
     * @param data the data to store.
     */
    fun writeData(secureStorage: CompletableFuture<SecureStorage>, key: ByteArray, data: ByteArray) : CompletableFuture<Unit>

    /**
     * Get the [ByteArray] data associated with the [key] from the database.
     *
     * @param secureStorage the storage to query.
     * @param key the key of the wanted data.
     *
     * @return [ByteArray] that contains the data of the associated [key] if exists.
     * @return `null` if the [key] is not found in the database.
     */
    fun readData(secureStorage: CompletableFuture<SecureStorage>, key: ByteArray) : CompletableFuture<ByteArray?>
}