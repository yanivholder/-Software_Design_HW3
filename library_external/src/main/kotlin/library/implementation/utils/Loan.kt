package library.implementation.utils

import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import java.util.UUID
import java.util.concurrent.CompletableFuture

class Loan(
    var loanInformation: LoanRequestInformation,
    var listener: ((String) -> CompletableFuture<Unit>)?,
    var futureToReturnForWait : CompletableFuture<ObtainedLoan> = CompletableFuture(),
) : ObtainedLoan {
    var id: String = UUID.randomUUID().toString()

    override fun returnBooks(): CompletableFuture<Unit> {
        if(listener == null) {
            return CompletableFuture.completedFuture(Unit)
        }
        return listener!!(id)
    }

    fun finishLoanFuture() {
        futureToReturnForWait.complete(this)
    }

    constructor(loan: Loan) : this(loan.loanInformation, loan.listener, loan.futureToReturnForWait) {
        this.id = loan.id
    }
}