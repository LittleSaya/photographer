package photographer.poster.chrome;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.alibaba.fastjson.JSONObject;

import photographer.DriverHelper;

public class QZone {

	public static void postToQZone(
			String animeName, String episodeName, String location,
			String picPath,
			JSONObject conf,
			String albumName) {
		WebDriver driver = null;
		try {
			System.out.println("initializing web drive");
			
			driver = DriverHelper.chrome(conf);

			System.out.println("getting https://qzone.qq.com");
			
			// 打开qq空间的登录页
			driver.get("https://qzone.qq.com");
			
			// 页面加载，等待10秒
			System.out.println("waiting for 10sec (page load)");
			Thread.sleep(10000);
			
			System.out.println("switching to login_frame");
			
			// 切换到登录iframe
			driver.switchTo().frame("login_frame");
			
			System.out.println("loginning");
			
			// 点击“使用账号密码登录”
			WebElement switcherPlogin = driver.findElement(By.id("switcher_plogin"));
			switcherPlogin.click();
			
			// 输入用户名和密码，并点击登录按钮
			WebElement usernameIpt = driver.findElement(By.id("u"));
			usernameIpt.clear();
			usernameIpt.sendKeys(conf.getJSONObject("auth").getJSONObject("qzone").getString("username"));
			WebElement passwordIpt = driver.findElement(By.id("p"));
			passwordIpt.clear();
			passwordIpt.sendKeys(conf.getJSONObject("auth").getJSONObject("qzone").getString("password"));
			WebElement loginBtn = driver.findElement(By.id("login_button"));
			loginBtn.click();
			
			// 登录，页面跳转，等待10秒
			System.out.println("waiting for 10sec (login, page jump)");
			Thread.sleep(10000);
			
			System.out.println("loading album list");
			
			WebElement linkToAlbumList = driver.findElement(By.xpath("//*[@id=\"menuContainer\"]/div/ul/li[3]/a"));
			System.out.println("before click");
			linkToAlbumList.click();
			System.out.println("after click");
			
			// 转到 app_canvas_frame
			driver.switchTo().frame(driver.findElement(By.className("app_canvas_frame")));
			
			System.out.println("finding target album");
			
			// 找到目标相册（只在第一页寻找）
			WebElement linkToAlbum = null;
			try {
				linkToAlbum = driver.findElement(By.linkText(albumName));
			} catch (NoSuchElementException e) {
				throw new Exception("album \"" + albumName + "\" not found, please place it in 1st page");
			}
			
			System.out.println("loading target album");
			
			// 点击链接进入相册
			linkToAlbum.click();
			
			System.out.println("uploading picture");
			
			// 点击“上传照片/视频”按钮
			WebElement openUploadDialog = driver.findElement(By.xpath("//*[@id=\"js-module-container\"]/div[1]/div[2]/div[2]/div/div[1]/a"));
			openUploadDialog.click();
			
			// 对话框加载，等待10秒
			System.out.println("waiting for 10sec (upload dialog)");
			Thread.sleep(10000);
			
			// 切换到 photoUploadDialog
			driver.switchTo().parentFrame();
			driver.switchTo().frame("photoUploadDialog");
			
			// 输入上传文件的本地路径
			File file = new File(picPath);
			WebElement fileInput = driver.findElement(By.xpath("//*[@id=\"container\"]/div[1]/a/input"));
			fileInput.sendKeys(file.getCanonicalPath());
			
			System.out.println("input local pic path: " + file.getCanonicalPath());
			
			// 点击上传按钮
			WebElement uploadBtn = driver.findElement(By.xpath("//*[@id=\"container\"]/div[2]/div[2]/div/a[2]"));
			uploadBtn.click();
			
			// 上传，等待20秒
			System.out.println("waiting for 20sec (uploading)");
			Thread.sleep(20000);
			
			// 将隐式等待时间延长到 120 秒
			driver.manage().timeouts().implicitlyWait(conf.getJSONObject("webdriverTimeouts").getJSONObject("qzone").getLongValue("uploadImplicitly"), TimeUnit.SECONDS);
			
			// 切换到 app_canvas_frame
			driver.switchTo().frame(driver.findElement(By.className("app_canvas_frame")));

			System.out.println("setting name and description");
			
			// 输入名称
			WebElement imgName = driver.findElement(By.id("name_all"));
			imgName.clear();
			imgName.sendKeys(animeName + " " + episodeName + " " + location);
			
			// 输入描述
			WebElement imgDesc = driver.findElement(By.id("desc_all"));
			imgDesc.clear();
			imgDesc.sendKeys(animeName + " " + episodeName + " " + location);
			
			((JavascriptExecutor)driver).executeScript("document.getElementById('name_all').focus()");

			System.out.println("waiting for 3sec (auto save)");
			Thread.sleep(3000); // 等待一段时间再聚焦到描述输入框，使QQ空间自动保存图片的名称和描述
			
			((JavascriptExecutor)driver).executeScript("document.getElementById('desc_all').focus()");
			
			System.out.println("finished");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				driver.quit();
			}
		}
	}
}
