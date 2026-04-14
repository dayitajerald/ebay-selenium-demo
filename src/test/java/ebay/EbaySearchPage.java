package ebay;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class EbaySearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ─── Locators ─────────────────────────────────────────────────────

    private final By searchBox    = By.xpath("//input[@id='gh-ac']");
    private final By searchButton = By.xpath("//input[@id='gh-btn'] | //button[@id='gh-search-btn']");
    private final By sortDropdown = By.xpath("//select[@name='_sop'] | //select[contains(@class,'x-flyout__select')]");

    // Title headings inside each result card (eBay now uses s-card)
    private final By resultItems = By.xpath(
        "//li[contains(@class,'s-card')]//div[@role='heading' and contains(@class,'s-card__title')]"
    );

    // First product link inside the card container header
    private final By firstResult = By.xpath(
        "/html/body/div[5]/div[8]/div[3]/div[1]/div[3]/ul/li[1]/div/div[2]/div[1]/a/div/span[1]"
    );

    // Product title on item page — tries the two most common eBay title structures
    private final By productTitle = By.xpath(
        "//h1[contains(@class,'x-item-title__mainTitle')]//span | " +
        "//div[@id='mainContent']//h1"
    );

    private final By addToCartButton = By.xpath(
    "//*[@id=\"atcBtn_btn_1\"]"
);

    // ─── Constructor ──────────────────────────────────────────────────

    public EbaySearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ─── Actions ──────────────────────────────────────────────────────

    /** Waits for the search box to be visible — confirms the real eBay home page loaded. */
    public void waitForHomePageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
    }

    /** Types a keyword into the search box and submits. */
    public void searchFor(String keyword) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(keyword);
        driver.findElement(searchButton).click();
    }

    /** Returns true if at least one result title is present. */
    public boolean areResultsDisplayed() {
        List<WebElement> results = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(resultItems)
        );
        return !results.isEmpty();
    }

    /**
     * Sorts results by price low to high.
     * Tries the dropdown first; falls back to appending _sop=15 to the URL.
     */
    public void sortByPriceLowToHigh() {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
            new Select(dropdown).selectByValue("15");
        } catch (Exception e) {
            String url = driver.getCurrentUrl();
            url = url.contains("_sop=")
                ? url.replaceAll("_sop=\\d+", "_sop=15")
                : url + "&_sop=15";
            driver.get(url);
        }
    }

    /** Clicks the first product link in the results list. */
    public void openFirstResult() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(firstResult));
        link.click();
    }

    /** Returns true if the product title heading is visible on the product page. */
    public boolean isProductTitleVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(productTitle)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Scrolls Buy It Now into view and clicks it via JavaScript.
     * JS click bypasses eBay's lazy-load overlay that blocks a normal Selenium click.
     */
    public void clickAddToCartButton() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(addToCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}
