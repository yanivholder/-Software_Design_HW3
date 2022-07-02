package library.tests.modules


import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.loan.LoanService
import il.ac.technion.cs.softwaredesign.loan.LoanServiceModule
import il.ac.technion.cs.softwaredesign.loan.impl.LoanServiceImpl
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.modules.StorageModule
import library.tests.fakes.HashmapStorageFactory
import library.tests.fakes.LoanServiceHashMap

class LibraryTestModule : KotlinModule() {
    override fun configure() {
        bind<SecureStorageFactory>()
            .to<HashmapStorageFactory>()
        install(StorageModule())
        bind<LoanService>()
            .to<LoanServiceHashMap>()
    }
}