package TestSuites;

import PageObjects.ChildPageObject;
import PageObjects.MainPageObject;
import Settings.BrowserSettings;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static Data.CommonData.*;

@Epic("Test group")
@Feature("Subgroup 1")
public class TestClass1 extends BrowserSettings {

    @Test
    @TmsLink("11111")
    @Severity(SeverityLevel.CRITICAL)
    @Description("test")
    public void testMethod1() {
        SoftAssert sAssert = new SoftAssert();
        MainPageObject main = new MainPageObject();
        ChildPageObject child = new ChildPageObject(sAssert);
        main.login(loginUser, passwordUser);
        main.openUpperMenu();
        main.gotoLink("https://test.com/messages");
        child.sendTextReply("test reply");
        child.assertReply("test reply");
    }
}
