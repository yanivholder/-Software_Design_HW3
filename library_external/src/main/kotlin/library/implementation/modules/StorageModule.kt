package library.implementation.modules
import com.google.inject.Provides
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import library.implementation.*
import library.implementation.implementations.*
import library.implementation.utils.*
import java.util.concurrent.CompletableFuture

class StorageModule : KotlinModule() {
    override fun configure() {
        bind<IUsersManager>().to<UsersManager>()
        bind<ITokensManager>().to<TokensManager>()
        bind<ILoansManager>().to<LoansManager>()
        bind<IBooksManager>().to<BooksManager>()
        bind<IBasicStorageManager>().to<BasicStorageManager>()
        bind<ILibWrapper>.to(LibWrapper)
    }
    @Provides @Singleton @UsersStorage
    fun provideUsersStorage(storageFactory: SecureStorageFactory)
            : @UsersStorage CompletableFuture<SecureStorage> {
        return storageFactory.open(Constants.USERS_DB.toByteArray());
    }
    @Provides @Singleton @BooksStorage
    fun provideBooksStorage(storageFactory: SecureStorageFactory)
            : @BooksStorage CompletableFuture<SecureStorage> {
        return storageFactory.open(Constants.BOOKS_DB.toByteArray());
    }
    @Provides @Singleton @TokensStorage
    fun provideTokensStorage(storageFactory: SecureStorageFactory)
            : @TokensStorage CompletableFuture<SecureStorage> {
        return storageFactory.open(Constants.TOKEN2USERNAMES_DB.toByteArray());
    }
    @Provides @Singleton @LoansStorage
    fun provideLoansStorage(storageFactory: SecureStorageFactory)
            : @LoansStorage CompletableFuture<SecureStorage> {
        return storageFactory.open(Constants.LOANS_DB.toByteArray());
    }
    @Provides
    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
        return secureStorageFactory.open(ByteArray(0))
    }
}