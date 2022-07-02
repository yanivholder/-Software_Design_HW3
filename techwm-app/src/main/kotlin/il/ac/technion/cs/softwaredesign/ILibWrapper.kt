package il.ac.technion.cs.softwaredesign

import java.util.concurrent.CompletableFuture

interface ILibWrapper {
    fun write(key: String, value: ByteArray): CompletableFuture<Unit>

    fun read(key: String): CompletableFuture<ByteArray?>
}
