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

    private final By resultItems = By.xpath(
        "//li[contains(@class,'s-card')]//div[@role='heading' and contains(@class,'s-card__title')]"
    );

    private final By firstResult = By.xpath(
        "/html/body/div[5]/div[8]/div[3]/div[1]/div[3]/ul/li[1]/div/div[2]/div[1]/a/div/span[1]"
    );

    private final By productTitle = By.xpath(
        "//h1[contains(@class,'x-item-title__mainTitle')]//span | " +
        "//div[@id='mainContent']//h1"
    );

    private final By addToCartButton = By.xpath("//*[@id='atcBtn_btn_1']");

    private final By seeCartButton = By.xpath("//*[@id='atcBtn_btn_1']/span/span");

    private final By checkoutButton = By.xpath("//*[@id='mainContent']/div[1]/div[3]/div[2]/div/div/div[2]/button");

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

    /**
     * Clicks the first product link and switches to the new tab if one opens.
     * Waits for the product page to fully load before returning.
     */
    public void openFirstResult() {
        String originalTab = driver.getWindowHandle();
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(firstResult));
        link.click();

        // Switch to new tab if one opened
        wait.until(d -> d.getWindowHandles().size() >= 1);
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalTab)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // Wait for full page load
        wait.until(d -> ((JavascriptExecutor) d)
            .executeScript("return document.readyState").equals("complete"));

        System.out.println("Now on: " + driver.getCurrentUrl());
    }

    /** Returns true if the product title heading is visible on the product page. */
    public boolean isProductTitleVisible() {
        try {
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(productTitle)).getText();
            System.out.println("Product title: " + title);
            return !title.isEmpty();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** Clicks Add to Cart and waits for the modal to appear. */
    public void clickAddToCart() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(addToCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        System.out.println("✔ Add to Cart clicked.");
    }

    /**
     * Reloads the product page then clicks the "See Cart" button.
     * After adding to cart, eBay changes the Add to Cart button to "See Cart" on reload.
     */
    public void reloadAndClickSeeCart() {
        driver.navigate().refresh();
        wait.until(d -> ((JavascriptExecutor) d)
            .executeScript("return document.readyState").equals("complete"));
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(seeCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        System.out.println("✔ See Cart clicked.");
    }

    /** Clicks the Checkout button on the cart page. */
    public void clickCheckout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        System.out.println("✔ Checkout clicked.");
    }
}