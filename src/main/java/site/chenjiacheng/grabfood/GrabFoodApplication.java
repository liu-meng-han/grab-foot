package site.chenjiacheng.grabfood;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Create by chenjiacheng on 2022/4/4
 */
public class GrabFoodApplication {

    private AndroidDriver driver;

    public static void main(String[] args) {
        try {
            new GrabFoodApplication().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 主程序
     * @throws Exception
     */
    public void run() throws Exception {
        //1. 初始化驱动
        this.initDriver();

        //2. 进主页面&进购物车页面
        //2.1 进入购物车页面
        driver.findElementById("com.yaya.zone:id/ani_car").click();
        System.out.println("setup 1-> 购物车: done");
        sleep(TimeUnit.MILLISECONDS,500);

        //3.循环操作:结算、支付、判断条件
        for (; ; ) {
            //3.1 结算
            driver.findElementById("com.yaya.zone:id/btn_submit").click();
            System.out.println("setup 2-> 结算: done");
            sleep(TimeUnit.MILLISECONDS,500);

            //3.2 支付
            driver.findElementById("com.yaya.zone:id/tv_submit").click();
            System.out.println("setup 3-> 支付: done");
            sleep(TimeUnit.MILLISECONDS,500);

            //3.3 查看预约时间
            List<MobileElement> elements = driver.findElements(By.id("com.yaya.zone:id/cl_item_select_hour_root"));
            if (elements == null || elements.size() == 0) {
                //3.3.1 返回上一层，重新开始
                driver.navigate().back();
                continue;
            }

            //3.4 校验是否有时间预约
            Optional<MobileElement> mobileElementOptional = elements.stream()
                    .filter(mobileElement -> {
                        MobileElement title = mobileElement.findElement(By.id("com.yaya.zone:id/tv_item_select_hour_title"));
                        MobileElement desc = mobileElement.findElement(By.id("com.yaya.zone:id/tv_item_select_hour_desc"));
                        System.out.println(title.getText() + " " + desc.getText());
                        return !(desc.getText().equalsIgnoreCase("已约满"));
                    }).findFirst();

            if (mobileElementOptional.isPresent()) {
                //1. 执行购买程序｜｜执行提醒城西
                System.out.println("setup 5-> 执行购买程序");
                break;
            } else {
                //1. 返回购物车页面，刷新后继续进行
                driver.findElement(By.id("com.yaya.zone:id/iv_dialog_select_time_close")).click();
                driver.navigate().back();
                //2. 增加加载时间
                sleep(TimeUnit.MILLISECONDS,500);
                (new TouchAction(driver)).press(PointOption.point(521, 747))
                        .moveTo(PointOption.point(501, 1639))
                        .release().perform();
                System.out.println("setup 4-> 重试");
            }
        }
    }

    /***
     * 增加延迟
     * @param timeUnit
     * @param time
     */
    public static void sleep(TimeUnit timeUnit, int time) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***
     * 初始化驱动
     * @throws MalformedURLException
     */
    private void initDriver() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:platformVersion", "11.0");
        desiredCapabilities.setCapability("appium:deviceName", "device");
        desiredCapabilities.setCapability("appium:ensureWebviewsHavePages", true);
        desiredCapabilities.setCapability("appium:nativeWebScreenshot", true);
        desiredCapabilities.setCapability("appium:newCommandTimeout", 3600);
        desiredCapabilities.setCapability("appium:connectHardwareKeyboard", true);
        desiredCapabilities.setCapability("noReset", "true");
        desiredCapabilities.setCapability("appPackage", "com.yaya.zone");
        desiredCapabilities.setCapability("appActivity", "cn.me.android.splash.activity.SplashActivity");
        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), desiredCapabilities);
        sleep(TimeUnit.SECONDS,5);
    }

}
