package com.bcg.dv

import com.bcg.dv.entities.Account
import com.bcg.dv.repositories.AccountsRepository
import com.bcg.dv.services.AccountsService
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AccountsServiceSpec extends Specification {
    @Subject
    AccountsService acctsService

    AccountsRepository acctsRepoMock = Mock()

    def setup() {
        acctsService = new AccountsService(acctsRepoMock)
    }

    def "getDetails returns target account"() {
        given:
        1 * acctsRepoMock.findById(_) >> { Integer id ->
            Account acct = new Account()
            acct.setId(id)
            acct.setBalance(100.0)
            Optional.of(acct)
        }

        when:
        Optional<Account> optAcct = acctsService.getDetails(1)

        then:
        optAcct.get().getId() == 1
        optAcct.get().getBalance() == 100.0
    }
}