package library.implementation
import library.implementation.Library

import java.util.concurrent.CompletableFuture

class LibraryFake : Library {

    private var map = mutableMapOf<String, ByteArray>()

    override fun write(key: String, value: ByteArray): CompletableFuture<Unit> {
        map[key] = value
        return CompletableFuture.completedFuture(Unit)
    }

    override fun read(key: String): CompletableFuture<ByteArray?> {
        return CompletableFuture.completedFuture(map[key])
    }

}