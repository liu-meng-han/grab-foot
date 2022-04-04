package site.chenjiacheng.grabfood;

import com.google.common.base.Ascii;
import com.sun.tools.example.debug.expr.ASCII_UCodeESC_CharStream;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
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
    private String DINGDONG_PAY_PASSWD = "123456";

    public static void main(String[] args) {
        try {
            new GrabFoodApplication().run();
        } catch (Exception e) {
            System.out.println("Exception: ->" + e.getMessage());
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

        //2. 进主页面&进购物车页面&进入结算页面
        //2.1 进入购物车页面
        driver.findElementById("com.yaya.zone:id/ani_car").click();
        System.out.println("setup 1-> 购物车: done");
        sleep(TimeUnit.MILLISECONDS, 500);

        //2.2 结算
        driver.findElementById("com.yaya.zone:id/btn_submit").click();
        System.out.println("setup 2-> 结算: done");
        sleep(TimeUnit.MILLISECONDS, 500);

        //2.3 设置预约时间
        //initReservationTime(driver);

        //3.循环操作:支付、判断条件
        for (; ; ) {
            //3.2 立即支付
            try {
                WebElement pay_btn = driver.findElementById("com.yaya.zone:id/tv_submit");
                pay_btn.click();
                System.out.println("setup 3-> 支付: done");
                sleep(TimeUnit.MILLISECONDS, 500);
            } catch (NoSuchElementException e) {
                //1. 利用异常进行判断[这个要改进，不能用异常作为业务判断条件]
                final char ASCII_ZERO = '0';
                for (int i = 0; i < DINGDONG_PAY_PASSWD.length(); i++) {
                    int number = DINGDONG_PAY_PASSWD.charAt(i) - ASCII_ZERO;
                    sleep(TimeUnit.MILLISECONDS, 30);
                    driver.pressKey(new KeyEvent(AndroidKey.valueOf("DIGIT_" + number)));
                }
                System.out.println("任务圆满结束！！！");
                sleep(TimeUnit.SECONDS, 60);
                break;
            }
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
        sleep(TimeUnit.SECONDS, 5);
    }

    @SuppressWarnings("ALL")
    private void initReservationTime(AndroidDriver driver) {
        //3.3 查看预约时间
        List<MobileElement> elements = driver.findElements(By.id("com.yaya.zone:id/cl_item_select_hour_root"));
        if (elements == null || elements.size() == 0) {
            throw new NoSuchElementException("预约时间元素不存在！");
        }

        //3.4 校验是否有时间预约
        Optional<MobileElement> mobileElementOptional = elements.stream()
                .filter(mobileElement -> {
                    MobileElement title = mobileElement.findElement(By.id("com.yaya.zone:id/tv_item_select_hour_title"));
                    MobileElement desc = mobileElement.findElement(By.id("com.yaya.zone:id/tv_item_select_hour_desc"));
                    System.out.println(title.getText() + " " + desc.getText());
                    return !(desc.getText().equalsIgnoreCase("已约满"));
                }).findFirst();

        //3.4 选择可预约的元素
        if (mobileElementOptional.isPresent()) {
            mobileElementOptional.get().click();
        } else {
            throw new UnsupportedOperationException("预约时间已满,任务失败。");
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



}
