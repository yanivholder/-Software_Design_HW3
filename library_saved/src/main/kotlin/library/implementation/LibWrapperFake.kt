package library.implementation

import java.io.Serializable
import java.util.concurrent.CompletableFuture

class LibWrapperFake : ILibWrapper, Serializable{
    private val map : MutableMap<String, ByteArray> = mutableMapOf()

    override fun write(key: String, value: ByteArray): CompletableFuture<Unit>{
        map[key] = value
        return CompletableFuture.completedFuture(Unit)
    }

    override fun read(key: String): CompletableFuture<ByteArray?>{
        return CompletableFuture.completedFuture(map[key])
    }
}
