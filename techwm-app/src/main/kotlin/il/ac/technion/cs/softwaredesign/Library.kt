import java.util.concurrent.CompletableFuture

interface Library {
    fun write(key: String, value: ByteArray): CompletableFuture<Unit>

    fun read(key: String): CompletableFuture<ByteArray?>
}