package library.implementation.implementations

import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction

/**
 * Wraps the 100 bytes limit of the [SecureStorage] and encapsulates the read/write operations to them.
 */
class BasicStorageManager : IBasicStorageManager {
    /**
     * Store the [data] in the database with [key].
     *
     * @param secureStorage the storage to query.
     * @param key the key to associate with the data.
     * @param data the data to store.
     */
    override fun writeData(secureStorage: CompletableFuture<SecureStorage>, key: ByteArray, data: ByteArray) : CompletableFuture<Unit> {
        var i = 0;
        var combinedFuture = CompletableFuture.completedFuture(Unit);
        while (i*100 < data.size){
            val currentKeyByteArray = key + ("_" + i).toByteArray();
            val currentValuePartByteArray = data.copyOfRange(i*100, if (((i*100)+100) < data.size) ((i*100)+100) else data.size);
            val aWritingFuture = secureStorage.thenCompose { it.write(currentKeyByteArray, currentValuePartByteArray) };
            combinedFuture = combinedFuture.thenCombine(aWritingFuture, BiFunction { _, _ -> });
            i++
        }
        return combinedFuture;
    }

    /**
     * Get the [ByteArray] data associated with the [key] from the database.
     *
     * @param secureStorage the storage to query.
     * @param key the key of the wanted data.
     *
     * @return [ByteArray] that contains the data of the associated [key] if exists.
     * @return `null` if the [key] is not found in the database.
     */
    override fun readData(secureStorage: CompletableFuture<SecureStorage>, key: ByteArray) : CompletableFuture<ByteArray?> {
        return readDataAux(secureStorage, key, 0);
    }
    
    private fun readDataAux(secureStorage: CompletableFuture<SecureStorage>, key: ByteArray, i: Int) : CompletableFuture<ByteArray?> {
        val currentKeyByteArray = key + ("_" + i).toByteArray();
        return secureStorage.thenCompose { it.read(currentKeyByteArray); }
            .thenCompose {
                if(it == null) {
                    CompletableFuture.completedFuture(null)
                } else {
                    CompletableFuture.completedFuture(it).thenCombine(readDataAux(secureStorage, key, i+1), BiFunction { first, second -> if(second == null) first else first+second })
                }
            }
    }

}