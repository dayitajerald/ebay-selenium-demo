# eBay Selenium Demo

Automated browser tests for eBay using Selenium WebDriver, TestNG, and Java.

---

## Prerequisites

Make sure the following are installed before running:

- **Java 21** — [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven** — [Download](https://maven.apache.org/download.cgi)
- **Google Chrome** (latest)
- **IntelliJ IDEA** (Community or Ultimate) — [Download](https://www.jetbrains.com/idea/download)

> ChromeDriver is managed automatically via WebDriverManager — no manual setup needed.

---

## Setup — IntelliJ IDEA (Windows & Mac)

### 1. Open the Project
- Launch IntelliJ IDEA
- Click **File → Open** and select the `ebay-selenium-demo` folder
- IntelliJ will detect it as a Maven project — click **Load Maven Project** if prompted

### 2. Set Java 21 SDK
- Go to **File → Project Structure → Project**
- Under **SDK**, select **Java 21** (download it from IntelliJ if not listed: click **Add SDK → Download JDK**)
- Click **Apply → OK**

### 3. Install Dependencies
- Open the **Maven** panel on the right side
- Click the **Reload** (↻) button to download all dependencies
- Or right-click `pom.xml` → **Maven → Reload project**

### 4. Run the Tests
**Option A — Run all tests via Maven**
- Open the **Terminal** tab at the bottom (or press `Alt+F12` on Windows / `⌥F12` on Mac)
- Run:
  ```bash
  mvn test
  ```

**Option B — Run from the IDE**
- Navigate to `src/test/java/ebay/EbayTest.java`
- Right-click the file → **Run 'EbayTest'**
- Or click the green ▶ button next to any individual test method

---

## Setup — Command Line Only (Windows)

1. Install Java 21 and add it to your system PATH
2. Install Maven and add it to your system PATH
3. Open **Command Prompt** and run:
   ```cmd
   git clone <repo-url>
   cd ebay-selenium-demo
   mvn clean install -DskipTests
   mvn test
   ```

---

## Setup — Command Line Only (Mac)

1. Install Java 21 and Maven (via [Homebrew](https://brew.sh)):
   ```bash
   brew install openjdk@21 maven
   ```
2. Run:
   ```bash
   git clone <repo-url>
   cd ebay-selenium-demo
   mvn clean install -DskipTests
   mvn test
   ```

---

## Test Flow

The tests run in sequence, each picking up where the previous left off.

| # | Test | What it does |
|---|------|-------------|
| T1 | Launch eBay | Opens ebay.com and verifies the page loaded |
| T2 | Search | Searches for "wireless headphones" |
| T3 | Sort | Sorts results by Price + Shipping: lowest first |
| T4 | Open Product | Clicks the first result and loads the product page |
| T5 | Add to Cart | Adds the item to cart, reloads, clicks See Cart |
| T6 | Checkout | Clicks the Checkout button on the cart page |

---

## Notes

- Tests run on a **guest session** — no eBay login required
- T6 will land on the eBay sign-in page since checkout requires an account
- All tests share a single browser instance that opens at the start and closes at the end
