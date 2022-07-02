package library.implementation

import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import library.implementation.utils.Loan
import java.util.concurrent.CompletableFuture

interface ILoansManager {
    /**
     * Get the [Loan] of [loanId] from the database.
     *
     * @param loanId the id of the wanted loan.
     *
     * @return [Loan] that contains the data of the associated loanId if exists.
     * @return `null` if the loan is not found in the database.
     */
    fun getLoanInfoByLoanId(loanId: String): CompletableFuture<LoanRequestInformation?>

    /**
     * Store the loan in the database.
     *
     * @param loanRequestInformation The information of the loan to store.
     *
     * @returns A future that contains the loan id generated by the system
     */
    fun writeLoanToStorage(loanRequestInformation: LoanRequestInformation): CompletableFuture<String>


    /**
     * Update the loan in the database.
     *
     * @param loanId the id of the loan to update.
     * @param loanRequestInformation the overriding loan information.
     *
     */
    fun updateLoanIfExistsInStorage(loanId:String, loanRequestInformation: LoanRequestInformation): CompletableFuture<Unit>

    /**
     * @return a future that is finished only when the loan is obtained.
     * If the loan is already obtained or canceled, the future finishes immediately without an error.
     */
    fun innerWaitForBooks(loanId: String) : CompletableFuture<ObtainedLoan>
}