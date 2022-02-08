import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import play.Application;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class BrowserTest extends WithBrowser {

    protected Application provideApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    protected TestBrowser provideBrowser(int port) {
        return Helpers.testBrowser(port);
    }

    @Test
    public void testSignUpPage() {
        browser.goTo("http://localhost:" + play.api.test.Helpers.testServerPort() + "/signup");
        assertTrue(browser.pageSource().contains("Registrieren"));
    }
}
