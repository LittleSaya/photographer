package photographer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.alibaba.fastjson.JSONObject;

public class DriverHelper {

	public static WebDriver chrome(JSONObject conf) {
		// 配置 chrome driver 的路径
		System.setProperty("webdriver.chrome.driver", conf.getJSONObject("chromeConf").getString("chromeDriverLocation"));
		
		// 配置 chrome driver
		ChromeOptions options = new ChromeOptions();
		
		// 必要选项
		options.setBinary(conf.getJSONObject("chromeConf").getString("chromeBinaryLocation")); 
		options.setHeadless(true);
		options.setAcceptInsecureCerts(true);
		options.addArguments("--no-sandbox");
		//options.addArguments("stable-release-mode");
		options.addArguments("--window-size=1920,1080");
		
		// 下面这些选项用于修复无法在centos上正常运行的问题（Timed out receiving message from renderer）
//		options.addArguments("--start-maximized");
//		options.addArguments("--enable-automation");
//		options.addArguments("--disable-infobars");
//		options.addArguments("--disable-dev-shm-usage");
//		options.addArguments("--disable-browser-side-navigation");
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		WebDriver driver = new ChromeDriver(options);
		
		// 设置 webdriver 的等待时间
		driver.manage().timeouts().implicitlyWait(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultImplicitly"), TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultPageLoad"), TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultScript"), TimeUnit.SECONDS);
		
		return driver;
	}
	
	public static WebDriver firefox(JSONObject conf) {
		System.setProperty("webdriver.gecko.driver", conf.getString("geckoDriverLocation"));
//		System.setProperty("webdriver.firefox.bin", conf.getString("firefoxBinaryLocation"));
		System.setProperty("webdriver.firefox.marionette", "true");
		System.setProperty("webdriver.firefox.logfile", conf.getString("firefoxLogFile"));
		
		FirefoxOptions opts = new FirefoxOptions();
		opts.setHeadless(true);
//		opts.setBinary(new FirefoxBinary(new File(conf.getString("firefoxBinaryLocation"))));
//		opts.setAcceptInsecureCerts(true);
//		opts.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		opts.addPreference("permissions.default.image", 2); // 不加载图片
		
		WebDriver driver = new FirefoxDriver(opts);
		
		// 设置 webdriver 的等待时间
		driver.manage().timeouts().implicitlyWait(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultImplicitly"), TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultPageLoad"), TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("defaultScript"), TimeUnit.SECONDS);
		
		return driver;
	}
}
