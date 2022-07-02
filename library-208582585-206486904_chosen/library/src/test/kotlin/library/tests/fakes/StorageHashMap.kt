package library.tests.fakes

import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.util.concurrent.CompletableFuture

class StorageHashMap : SecureStorage {
    private val database = hashMapOf<String,ByteArray>()

    override fun read(key: ByteArray): CompletableFuture<ByteArray?> {
        return CompletableFuture.completedFuture(database[String(key)])
    }

    override fun write(key: ByteArray, value: ByteArray) : CompletableFuture<Unit> {
        database[String(key)] = value
        return CompletableFuture.completedFuture(Unit)
    }
}