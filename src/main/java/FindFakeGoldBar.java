import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FindFakeGoldBar {

    public static void main(String[] args) {
        // Update the path to the location where chromedriver is placed
        System.setProperty("webdriver.chrome.driver", "/Users/midhunpavuluru/Documents/Github/Fetch/chromedriver");
        WebDriver driver = new ChromeDriver();

        FindFakeGoldBar findFakeGoldBar = new FindFakeGoldBar();

        try {
            // Open the website
            driver.get("http://sdetchallenge.fetch.com/");

            // Define the gold bars
            int[] goldBars = {0, 1, 2, 3, 4, 5, 6, 7, 8};
            int fakeBar = findFakeGoldBar.findFakeGoldBar(driver, goldBars);

            // Click on the fake gold bar number
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement fakeBarElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("fake_" + fakeBar)));

            System.out.println("Fake gold bar element found: " + fakeBarElement);

            // Get the alert message
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertMessage = alert.getText();
            System.out.println("Alert message: " + alertMessage);
            alert.accept();

            // Output the number of weighings and list of weighings
            List<WebElement> weighings = driver.findElements(By.className("weighing"));
            System.out.println("Number of weighings: " + weighings.size());
            System.out.println("List of weighings:");
            for (WebElement weighing : weighings) {
                System.out.println(weighing.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    private int findFakeGoldBar(WebDriver driver, int[] goldBars) {
        // Implement the algorithm to find the fake gold bar
        // Here is an example using a binary search-like approach

        int left = 0;
        int right = goldBars.length - 1;

        while (left < right) {
            int mid = (left + right) / 2;
            int[] leftGroup = new int[mid - left + 1];
            int[] rightGroup = new int[right - mid];

            System.arraycopy(goldBars, left, leftGroup, 0, leftGroup.length);
            System.arraycopy(goldBars, mid + 1, rightGroup, 0, rightGroup.length);

            int result = weigh(driver, leftGroup, rightGroup);
            if (result == 0) {
                return goldBars[mid];
            } else if (result == -1) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return goldBars[left];
    }

    private int weigh(WebDriver driver, int[] leftGroup, int[] rightGroup) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Clear the bowls
            WebElement resetButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("reset")));
            resetButton.click();
        } catch (Exception e) {
            System.out.println("Failed to find or click the reset button. Printing page source for debugging:");
            System.out.println(driver.getPageSource());
            throw e;
        }

        // Fill the left bowl
        for (int i = 0; i < leftGroup.length; i++) {
            WebElement leftBowl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("left_" + i)));
            leftBowl.sendKeys(String.valueOf(leftGroup[i]));
        }

        // Fill the right bowl
        for (int i = 0; i < rightGroup.length; i++) {
            WebElement rightBowl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("right_" + i)));
            rightBowl.sendKeys(String.valueOf(rightGroup[i]));
        }

        // Perform the weighing
        WebElement weighButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("weigh")));
        weighButton.click();

        // Get the result
        WebElement resultElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[normalize-space(text())='Weighings']/following-sibling::ol/li")));
        String resultText = resultElement.getText();

        if (resultText.contains("left")) {
            return -1;
        } else if (resultText.contains("right")) {
            return 1;
        } else {
            return 0;
        }
    }
}
