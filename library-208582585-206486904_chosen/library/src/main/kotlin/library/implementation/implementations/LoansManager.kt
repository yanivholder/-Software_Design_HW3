package library.implementation.implementations

import com.google.gson.Gson
import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.*
import il.ac.technion.cs.softwaredesign.loan.LoanService
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import library.implementation.IBooksManager
import library.implementation.ILoansManager
import library.implementation.utils.Loan
import library.implementation.utils.LoansStorage
import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction

class LoansManager @Inject constructor(@LoansStorage private val loansStorage: CompletableFuture<SecureStorage>,
                                       private val loanService: LoanService,
                                       private val booksManager: IBooksManager,
                                       private val basicStorageManager: IBasicStorageManager
) : ILoansManager {
    private val gsonInstance = Gson();
    private var loansQueue: MutableList<Loan> = mutableListOf() //contains queued loans' ids
    private var obtainedLoans: MutableList<Loan> = mutableListOf()  //contains only running loans' ids
    private var canceledLoans: MutableList<Loan> = mutableListOf()  //contains only canceled loans' ids


    /**
     * Get the [Loan] of [loanId] from the database.
     *
     * @param loanId the id of the wanted loan.
     *
     * @return [Loan] that contains the data of the associated loanId if exists.
     * @return `null` if the loan is not found in the database.
     */
    override fun getLoanInfoByLoanId(loanId: String): CompletableFuture<LoanRequestInformation?> {
        for(loan in obtainedLoans) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan.loanInformation)
            }
        }
        for(loan in loansQueue) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan.loanInformation)
            }
        }
        for(loan in canceledLoans) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan.loanInformation)
            }
        }
        return basicStorageManager.readData(loansStorage, loanId.toByteArray())
            .thenApply {
                if(it == null) {
                    null
                } else {
                    val loanJsonString = String(bytes = it);
                    gsonInstance.fromJson(loanJsonString, Loan::class.java).loanInformation;
                }
            }
    }

    /**
     * Store the loan in the database.
     *
     * @param loanRequestInformation The information of the loan to store.
     *
     * @returns A future that contains the loan id generated by the system
     */
    override fun writeLoanToStorage(loanRequestInformation: LoanRequestInformation): CompletableFuture<String> {
        val loan = Loan(loanRequestInformation, this::returnObtainedLoanCallback)
        loansQueue.add(loan)
        val stupidLoanCopyToNullListenerAsBytesArray = gsonInstance.toJson(Loan(loanRequestInformation, null)).toByteArray()

        return basicStorageManager.writeData(loansStorage, loan.id.toByteArray(), stupidLoanCopyToNullListenerAsBytesArray)
            .thenCompose {
                tryAdvanceQueueOnce()
            }.thenApply {
                loan.id
            }
    }


    /**
     * Update the loan in the database.
     *
     * @param loanId the id of the loan to update.
     * @param loanRequestInformation the overriding loan information.
     *
     */
    override fun updateLoanIfExistsInStorage(loanId:String, loanRequestInformation: LoanRequestInformation): CompletableFuture<Unit> {
        var wantedLoan : Loan? = null
        var oldLoanStatus : LoanStatus? = null
        for(loan in obtainedLoans) {
            if(loan.id == loanId) {
                oldLoanStatus = loan.loanInformation.loanStatus
                loan.loanInformation = loanRequestInformation
                wantedLoan = loan
                break
            }
        }
        if(wantedLoan == null) {
            for (loan in loansQueue) {
                if (loan.id == loanId) {
                    oldLoanStatus = loan.loanInformation.loanStatus
                    loan.loanInformation = loanRequestInformation
                    wantedLoan = loan
                    break
                }
            }
        }
        if(wantedLoan == null) {
            for (loan in canceledLoans) {
                if (loan.id == loanId) {
                    oldLoanStatus = loan.loanInformation.loanStatus
                    loan.loanInformation = loanRequestInformation
                    wantedLoan = loan
                    break
                }
            }
        }
        if(wantedLoan == null) {
            wantedLoan = Loan(loanRequestInformation, null)
            wantedLoan.id = loanId;
        } else {
            if(oldLoanStatus == LoanStatus.QUEUED) {
                loansQueue = loansQueue.filter { loan -> loan.id != loanId }.toMutableList()
            }
            else if(oldLoanStatus == LoanStatus.OBTAINED) {
                obtainedLoans = obtainedLoans.filter { loan -> loan.id != loanId }.toMutableList()
            }
            else if(oldLoanStatus == LoanStatus.CANCELED) {
                canceledLoans = canceledLoans.filter { loan -> loan.id != loanId }.toMutableList()
            }

            if(loanRequestInformation.loanStatus == LoanStatus.QUEUED) {
                loansQueue.add(wantedLoan)
            }
            else if(loanRequestInformation.loanStatus == LoanStatus.CANCELED) {
                wantedLoan.listener = null
                canceledLoans.add(wantedLoan)
            }
            else if(loanRequestInformation.loanStatus == LoanStatus.OBTAINED) {
                obtainedLoans.add(wantedLoan)
            }
        }
        val stupidLoanCopyToNullListener = Loan(loanRequestInformation, null)
//        stupidLoanCopyToNullListener.listener = null
//        stupidLoanCopyToNullListener.futureToReturnForWait = CompletableFuture.completedFuture(null)
        val stupidLoanCopyToNullListenerAsBytesArray = gsonInstance.toJson(stupidLoanCopyToNullListener).toByteArray()
        return basicStorageManager.writeData(loansStorage, loanId.toByteArray(), stupidLoanCopyToNullListenerAsBytesArray).thenCompose {
            tryAdvanceQueueUntilBlocked()
        }
    }

    /**
     * @return a future that is finished only when the loan is obtained.
     * If the loan is already obtained or canceled, the future finishes immediately without an error.
     */
    override fun innerWaitForBooks(loanId: String) : CompletableFuture<ObtainedLoan>{
        for(loan in obtainedLoans) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan)
            }
        }
        for(loan in loansQueue) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan)
            }
        }
        for(loan in canceledLoans) {
            if(loan.id == loanId) {
                return CompletableFuture.completedFuture(loan)
            }
        }
        return getLoanInfoByLoanId(loanId).thenApply {
            val loan = Loan(it!!, null)
            loan.finishLoanFuture()
            loan
        }
    }

    //PRIVATE METHODS:
    private fun tryAdvanceQueueUntilBlocked(): CompletableFuture<Unit> {
        return tryAdvanceQueueOnce().thenApply {
            if(it) {
                tryAdvanceQueueUntilBlocked()
            }
        }
    }

    private fun tryAdvanceQueueOnce(): CompletableFuture<Boolean> {
        return if (loansQueue.isEmpty()) {
            CompletableFuture.completedFuture(false)
        } else {
            val loanAtTop = loansQueue[0]
            return tryToObtainBooksFromBooksManager(loanAtTop.loanInformation.requestedBooks).thenCombine(tryToObtainBooksFromExternalLibrary(loanAtTop.loanInformation.requestedBooks), BiFunction { first, second -> first&&second })
                .thenCompose {
                    if (it) {
                        val newLoanInformation = LoanRequestInformation(loanAtTop.loanInformation.loanName, loanAtTop.loanInformation.requestedBooks, loanAtTop.loanInformation.ownerUsername, LoanStatus.OBTAINED)
                        updateLoanIfExistsInStorage(loanAtTop.id, newLoanInformation).thenCombine(
                            CompletableFuture.completedFuture(loanAtTop.finishLoanFuture()), BiFunction { _, _ -> true })
                    } else {
                        CompletableFuture.completedFuture(false)
                    }
                }
        }
    }

    private fun returnObtainedLoanCallback(loanId: String): CompletableFuture<Unit> {
        var loan: Loan? = null
        for(loanIter in obtainedLoans) {
            if(loanIter.id == loanId) {
                loan = loanIter
            }
        }
        if(loan == null) {
            return CompletableFuture.completedFuture(Unit)
        }
        return updateLoanIfExistsInStorage(loanId, LoanRequestInformation(loan.loanInformation.loanName, loan.loanInformation.requestedBooks, loan.loanInformation.ownerUsername, LoanStatus.RETURNED))
            .thenCompose {
                returnObtainedBooksToExternalLibrary(loan.loanInformation.requestedBooks)
            }
            .thenCompose {
                returnObtainedBooksToBooksManager(loan.loanInformation.requestedBooks)
            }
            .thenCompose {
                tryAdvanceQueueUntilBlocked()
            }
    }




    private fun tryToObtainBooksFromExternalLibrary(
        wantedBooks: List<String>,
        obtainedBooks: MutableList<String> = mutableListOf()
    ): CompletableFuture<Boolean> {
        if (wantedBooks.isEmpty()) {
            return CompletableFuture.completedFuture(true)
        }
        return loanService.loanBook(wantedBooks[0])
            .thenCompose {
                obtainedBooks.add(wantedBooks[0])
                tryToObtainBooksFromExternalLibrary(wantedBooks.drop(1), obtainedBooks)
            }
            .handle { res, ex ->
                if (ex == null) CompletableFuture.completedFuture(res)
                else returnObtainedBooksToExternalLibrary(obtainedBooks).thenApply { false }
            }
            .thenCompose { it }
    }

    private fun returnObtainedBooksToExternalLibrary(bookIdsList: List<String>): CompletableFuture<Unit> {
        if (bookIdsList.isEmpty()) {
            return CompletableFuture.completedFuture(Unit)
        }
        return loanService.returnBook((bookIdsList[0]))
            .thenCompose {
                returnObtainedBooksToExternalLibrary(bookIdsList.drop(1))
            }
    }





    private fun tryToObtainBooksFromBooksManager(
        wantedBooks: List<String>,
        obtainedBooks: MutableList<String> = mutableListOf()
    ): CompletableFuture<Boolean> {
        if (wantedBooks.isEmpty()) {
            return CompletableFuture.completedFuture(true)
        }
        return booksManager.loanBook(wantedBooks[0])
            .thenCompose {
                obtainedBooks.add(wantedBooks[0])
                tryToObtainBooksFromBooksManager(wantedBooks.drop(1), obtainedBooks)
            }
            .handle { res, ex ->
                if (ex == null) CompletableFuture.completedFuture(res)
                else returnObtainedBooksToBooksManager(obtainedBooks).thenApply { false }
            }
            .thenCompose { it }
    }

    private fun returnObtainedBooksToBooksManager(bookIdsList: List<String>): CompletableFuture<Unit> {
        if (bookIdsList.isEmpty()) {
            return CompletableFuture.completedFuture(Unit)
        }
        return booksManager.returnBook((bookIdsList[0]))
            .thenCompose {
                returnObtainedBooksToBooksManager(bookIdsList.drop(1))
            }
    }
}