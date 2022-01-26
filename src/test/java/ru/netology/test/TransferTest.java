package ru.netology.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.valid(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }


    void asserting() {

        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        if (firstCardBalance != secondCardBalance) {
            var average = (firstCardBalance - secondCardBalance) / 2;
            if (firstCardBalance < secondCardBalance) {
                var transferPage = dashboardPage.topUpCard(1);
                transferPage.transfer(Integer.toString(average), DataHelper.secondCard().getCardNumber());
            } else {
                var transferPage = dashboardPage.topUpCard(2);
                transferPage.transfer(Integer.toString(average), DataHelper.firstCard().getCardNumber());
            }
        }
    }


    @Test
       void shouldTopUpFirstCard() {

        asserting();
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId()) + 1;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId()) - 1;
        var transferPage = dashboardPage.topUpCard(1);
        transferPage.transfer("1", DataHelper.secondCard().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        assertEquals(expectedFirstCardBalance, firstCardBalance);
        assertEquals(expectedSecondCardBalance, secondCardBalance);
    }


    @Test
    void shouldTopUpSecondCard() {
        asserting();
        var expectedFirstCardBalance = 0;
        var expectedSecondCardBalance = 20000;
        var transferPage = dashboardPage.topUpCard(2);
        transferPage.transfer("10000", DataHelper.firstCard().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.firstCard().getCardId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.secondCard().getCardId());
        assertEquals(expectedFirstCardBalance, firstCardBalance);
        assertEquals(expectedSecondCardBalance, secondCardBalance);
    }

    @Test
    void shouldGetNotification() {
        asserting();
        var transferPage = dashboardPage.topUpCard(2);
        transferPage.transfer("10005", DataHelper.firstCard().getCardNumber());
        var dashboardPageWithNotification = new DashboardPage();
        transferPage.getNotificationVisible();
    }
}