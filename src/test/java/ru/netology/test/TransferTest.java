package ru.netology.test;

import com.codeborne.selenide.Configuration;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    //DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.valid(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
       // DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);

    }

       void asserting() {
       val dashboardPage = new DashboardPage();
        int firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        int secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        if (firstCardBalance != secondCardBalance) {
            int average = (firstCardBalance - secondCardBalance) / 2;
            if (firstCardBalance < secondCardBalance) {
                val transferPage = dashboardPage.topUpCard(1);
                transferPage.transfer(Integer.toString(average), DataHelper.secondCard().getCardNumber());
            }
            else {
                val transferPage = dashboardPage.topUpCard(2);
                transferPage.transfer(Integer.toString(average), DataHelper.firstCard().getCardNumber());
            }
        }
    }

    @Test
    void shouldTopUpFirstCard() {
        asserting();
        val dashboardPage = new DashboardPage();
        int expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId()) + 1;
        int expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId()) - 1;
        val transferPage = dashboardPage.topUpCard(1);
        transferPage.transfer("1", DataHelper.secondCard().getCardNumber());
        int firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        int secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        assertEquals(expectedFirstCardBalance, firstCardBalance);
        assertEquals(expectedSecondCardBalance, secondCardBalance);
    }

    @Test
    void shouldTopUpSecondCard() {
        asserting();
        val dashboardPage = new DashboardPage();
        var expectedFirstCardBalance = 0;
        var expectedSecondCardBalance = 20000;
        val transferPage = dashboardPage.topUpCard(2);
        transferPage.transfer("10000", DataHelper.firstCard().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        assertEquals(expectedFirstCardBalance, firstCardBalance);
        assertEquals(expectedSecondCardBalance, secondCardBalance);
    }

    @Test
    void shouldGetNotification() {
        asserting();
        val dashboardPage = new DashboardPage();
        val transferPage = dashboardPage.topUpCard(2);
        transferPage.transfer("10005", DataHelper.firstCard().getCardNumber());
        val dashboardPageWithNotification = new DashboardPage();
        transferPage.getNotificationVisible();
    }
}