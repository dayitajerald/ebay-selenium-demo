package ebay;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class EbayTest {

    private WebDriver driver;
    private EbaySearchPage searchPage;

    private static final String BASE_URL = "https://www.ebay.com";
    private static final String SEARCH_KEYWORD = "wireless headphones";

    // ─── Setup / Teardown ─────────────────────────────────────────────

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        searchPage = new EbaySearchPage(driver);
        System.out.println("✔ Browser launched.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("✔ Browser closed.");
    }

    // ─── Test 1: Launch eBay ──────────────────────────────────────────

    @Test(priority = 1)
    public void testLaunchEbay() throws InterruptedException {
        driver.get(BASE_URL);
        // Wait for the search box — confirms the real eBay page loaded, not a bot-challenge page
        searchPage.waitForHomePageLoad();
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("eBay"), "Home page title mismatch. Got: " + title);
        System.out.println("✔ T1 PASSED — eBay home loaded. Title: " + title);
    }

    // ─── Test 2: Search for a product ────────────────────────────────

    @Test(priority = 2)
    public void testSearchProduct() throws InterruptedException {
        searchPage.searchFor(SEARCH_KEYWORD);
        Thread.sleep(1000);

        Assert.assertTrue(
            searchPage.areResultsDisplayed(),
            "No results found for: " + SEARCH_KEYWORD
        );
        System.out.println("✔ T2 PASSED — Search results loaded for: " + SEARCH_KEYWORD);
    }

    // ─── Test 3: Sort results by price low to high ────────────────────

    @Test(priority = 3)
    public void testSortByPrice() throws InterruptedException {
        searchPage.sortByPriceLowToHigh();
        Thread.sleep(1000);

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("_sop=15"), "Sort param missing in URL: " + url);
        System.out.println("✔ T3 PASSED — Results sorted by price (low to high).");
    }

    // ─── Test 4: Open first product ───────────────────────────────────

    @Test(priority = 4)
    public void testOpenProduct() throws InterruptedException {
        searchPage.openFirstResult();
        Thread.sleep(1000);

        Assert.assertTrue(
            searchPage.isProductTitleVisible(),
            "Product title not visible on product page."
        );
        System.out.println("✔ T4 PASSED — Product page loaded.");
    }

    // ─── Test 5: Click Buy It Now ─────────────────────────────────────

    @Test(priority = 5)
    public void testAddToCart() throws InterruptedException {
        searchPage.clickAddToCartButton();
        Thread.sleep(1000);

        String url = driver.getCurrentUrl();
        Assert.assertTrue(
            url.contains("signin") || url.contains("checkout") || url.contains("cart"),
            "Buy It Now did not navigate to sign-in/checkout. URL: " + url
        );
        System.out.println("✔ T5 PASSED — Buy It Now clicked. URL: " + url);
    }
}
