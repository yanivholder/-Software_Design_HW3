package il.ac.technion.cs.softwaredesign

import java.util.concurrent.CompletableFuture

class LibraryFake {

    private var map = mutableMapOf<String, ByteArray>()

    fun write(key: String, value: ByteArray): CompletableFuture<Unit> {
        map[key] = value
        return CompletableFuture.completedFuture(Unit)
    }

    fun read(key: String): CompletableFuture<ByteArray?> {
        return CompletableFuture.completedFuture(map[key])
    }

}