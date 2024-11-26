package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BookingAutomationTest {

    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        // Set up WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test: Search functionality
     */
    @Test
    public void testSearchFunctionality() {
        driver.get("https://www.booking.com/");

        // Enter destination
        WebElement searchBox = driver.findElement(By.id("ss"));
        searchBox.sendKeys("New York");

        // Select check-in and check-out dates
        driver.findElement(By.xpath("//td[@data-date='2024-12-01']")).click(); // Check-in date
        driver.findElement(By.xpath("//td[@data-date='2024-12-05']")).click(); // Check-out date

        // Select number of guests
        driver.findElement(By.id("xp__guests__toggle")).click();
        WebElement adultIncreaseButton = driver.findElement(By.xpath("//button[@aria-label='Increase number of Adults']"));
        adultIncreaseButton.click();

        // Click search
        driver.findElement(By.cssSelector(".sb-searchbox__button")).click();

        // Assert results are displayed
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        assertTrue("Search results are not displayed.",
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".sr_header"))).isDisplayed());
    }

    /**
     * Test: Filtering and sorting options
     */
    @Test
    public void testFilteringAndSorting() {
        driver.get("https://www.booking.com/");

        // Perform a search
        WebElement searchBox = driver.findElement(By.id("ss"));
        searchBox.sendKeys("New York");
        driver.findElement(By.xpath("//td[@data-date='2024-12-01']")).click(); // Check-in date
        driver.findElement(By.xpath("//td[@data-date='2024-12-05']")).click(); // Check-out date
        driver.findElement(By.cssSelector(".sb-searchbox__button")).click();

        // Apply price filter
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement priceFilter = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Price (lowest first)')]")));
        priceFilter.click();

        // Assert filter is applied
        assertTrue("Price filter not applied.", driver.getPageSource().contains("Sorted by price"));

        // Verify results are sorted by price
        List<WebElement> prices = driver.findElements(By.cssSelector(".bui-price-display__value"));
        int previousPrice = 0;
        for (WebElement priceElement : prices) {
            String priceText = priceElement.getText().replaceAll("[^0-9]", "");
            int currentPrice = Integer.parseInt(priceText.isEmpty() ? "0" : priceText);
            assertTrue("Prices are not sorted correctly.", currentPrice >= previousPrice);
            previousPrice = currentPrice;
        }
    }

    /**
     * Test: Booking process
     */
    @Test
    public void testBookingProcess() {
        driver.get("https://www.booking.com/");

        // Search for properties
        WebElement searchBox = driver.findElement(By.id("ss"));
        searchBox.sendKeys("New York");
        driver.findElement(By.xpath("//td[@data-date='2024-12-01']")).click(); // Check-in date
        driver.findElement(By.xpath("//td[@data-date='2024-12-05']")).click(); // Check-out date
        driver.findElement(By.cssSelector(".sb-searchbox__button")).click();

        // Select a property from the search results
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement propertyLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".sr_property_block_main_row")));
        propertyLink.click();

        // Switch to the new tab
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }

        // Select a room option
        WebElement bookNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".bui-button__text")));
        bookNowButton.click();

        // Assert booking page is displayed
        assertTrue("Booking page is not displayed.", driver.getPageSource().contains("Book your stay"));
    }

    /**
     * Test: User authentication
     */
    @Test
    public void testUserAuthentication() {
        driver.get("https://www.booking.com/");

        // Click on Sign in
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign in")));
        signInButton.click();

        // Enter email
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        emailField.sendKeys("invalid_user@example.com");

        // Attempt to login
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".bui-button")));
        continueButton.click();

        // Assert error message is displayed
        assertTrue("Error message not displayed.", driver.getPageSource().contains("Enter your password"));
    }

    /**
     * Test: Responsive design
     */
    @Test
    public void testResponsiveDesign() {
        // Resize the window to mobile size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));

        // Wait for the mobile menu to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Verify the mobile menu is displayed
            WebElement mobileMenu = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".bui-tab-menu__trigger")));
            assertTrue("Mobile menu not displayed in responsive mode.", mobileMenu.isDisplayed());
        } catch (Exception e) {
            System.err.println("Mobile menu not found. Locator might need to be updated.");
            throw e;
        }
    }
}
