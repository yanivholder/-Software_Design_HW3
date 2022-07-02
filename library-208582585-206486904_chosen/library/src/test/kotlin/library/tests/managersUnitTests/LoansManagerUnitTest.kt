package library.tests.managersUnitTests

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.LoanStatus
import library.implementation.ILoansManager
import library.tests.modules.LibraryTestModule
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LoansManagerUnitTest {
    private val injector = Guice.createInjector(LibraryTestModule())
    private val loansManager = injector.getInstance<ILoansManager>()

    @Test
    fun `stored loan can be retrieved`() {
        // Arrange
        val loanInfo = LoanRequestInformation("aLoanName", listOf("aBook", "anotherBook"), "anOwnerName", LoanStatus.QUEUED)
        // Action
        val loanId = loansManager.writeLoanToStorage(loanInfo).join();
        // Assert
        Assertions.assertEquals(loanInfo, loansManager.getLoanInfoByLoanId(loanId).join());

    }

    @Test
    fun `override data for existing loan`() {
        // Arrange
        val loanInfo1 = LoanRequestInformation("aLoanName", listOf("aBook", "anotherBook"), "anOwnerName", LoanStatus.QUEUED)
        val loanInfo2 = LoanRequestInformation(loanInfo1.loanName, loanInfo1.requestedBooks, loanInfo1.ownerUsername, LoanStatus.CANCELED)
        // Action
        val loanId = loansManager.writeLoanToStorage(loanInfo1).join();
        loansManager.updateLoanIfExistsInStorage(loanId, loanInfo2).join();
        // Assert
        Assertions.assertEquals(loanInfo2, loansManager.getLoanInfoByLoanId(loanId).join());
    }

    @Test
    fun `reading non existing loan returns null`() {
        Assertions.assertNull(loansManager.getLoanInfoByLoanId("non existing loanId").join());
    }

    @Test
    fun `loan with more than 100 bytes stored and read successfully`() {
        // Arrange
        val over100BytesString = "Number of ballon d'ORes I have is 100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        val loanInfo = LoanRequestInformation(over100BytesString, listOf("aBook", "anotherBook"), "anOwnerName", LoanStatus.QUEUED)
        // Action
        val loanId = loansManager.writeLoanToStorage(loanInfo).join();
        // Assert
        Assertions.assertEquals(loanInfo, loansManager.getLoanInfoByLoanId(loanId).join());
    }
}