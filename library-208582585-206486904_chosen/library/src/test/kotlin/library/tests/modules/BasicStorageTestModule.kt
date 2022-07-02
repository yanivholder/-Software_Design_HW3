package library.tests.modules


import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.IBasicStorageManager
import library.implementation.implementations.BasicStorageManager
import library.tests.fakes.HashmapStorageFactory
import library.tests.fakes.StorageHashMap

class BasicStorageTestModule : KotlinModule() {
    override fun configure() {
        bind<SecureStorageFactory>()
            .to<HashmapStorageFactory>()
        bind<SecureStorage>()
            .to<StorageHashMap>()
        bind<IBasicStorageManager>().to<BasicStorageManager>()
    }
}