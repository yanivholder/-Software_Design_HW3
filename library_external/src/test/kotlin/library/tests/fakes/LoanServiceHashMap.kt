package library.tests.fakes

import il.ac.technion.cs.softwaredesign.loan.LoanService
import java.util.concurrent.CompletableFuture

class LoanServiceHashMap : LoanService {
    override fun loanBook(id: String): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun returnBook(id: String): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

}