package site.chenjiacheng.grabfood;

import com.google.common.base.Ascii;
import com.sun.tools.example.debug.expr.ASCII_UCodeESC_CharStream;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
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
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Create by chenjiacheng on 2022/4/4
 */
public class GrabFoodApplication {

    private static AndroidDriver driver = null;
    private String PAY_PASSWORD = "123456";

    public static void main(String[] args) {
        try {
            new GrabFoodApplication().run();
        } catch (Exception e) {
            System.out.println("Exception: ->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /***
     * 主程序
     * @throws Exception
     */
    public void run() throws Exception {
        //1. 初始化驱动
        this.initDriver();

        //1.1 初始化配置
        ResourceBundle application = ResourceBundle.getBundle("grap-food");
        this.PAY_PASSWORD = application.getString("pay_passwd");
        System.out.println("PAY_PASSWORD = " + PAY_PASSWORD);

        //2. 进主页面&进购物车页面&进入结算页面
        //2.1 进入购物车页面
        driver.findElementById("com.yaya.zone:id/ani_car").click();
        System.out.println("setup 1-> 购物车: done");
        sleep(TimeUnit.MILLISECONDS, 200);

        //2.2 进入结算页面
        int i = 0;
        while (true) {
            try {
                driver.findElementById("com.yaya.zone:id/btn_submit").click();
                System.out.println("setup 2-> 结算: done");
                System.out.println("正在执行结算 = " + (++i));
                sleep(TimeUnit.MILLISECONDS, 200);
                //android.widget.TextView
                List<AndroidElement> elements = driver.findElementsByClassName("android.widget.TextView");
                boolean flag = false;
                for (AndroidElement element : elements) {
                    if(element.getText().equalsIgnoreCase("确认订单")){
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    break;
                }
            } catch (NoSuchElementException e) {
                //2.2 购物车为空，刷新
                (new TouchAction(driver)).press(PointOption.point(521, 747))
                        .moveTo(PointOption.point(501, 1639))
                        .release().perform();
                //sleep(TimeUnit.SECONDS, 10);
            }
        }


        //2.3 设置预约时间
        //initReservationTime(driver);

        //3.循环操作:支付、判断条件
        for (; ; ) {
            while (true) {
                try {
                    driver.findElementById("com.yaya.zone:id/ll_load_root");
                    driver.findElementById("com.yaya.zone:id/ll_reload_action").click();
                } catch (NoSuchElementException e) {
                    break;
                }
            }


            try {
                //3.2 立即支付
                WebElement pay_btn = driver.findElementById("com.yaya.zone:id/tv_submit");
                pay_btn.click();
                System.out.println("setup 3-> 支付: done");
                sleep(TimeUnit.MILLISECONDS, 200);
            } catch (NoSuchElementException e) {
                //1. 校验是否到了支付状态
                try {
                    driver.findElementById("com.yaya.zone:id/tv_dialog_select_time_title");
                    initReservationTime(driver);
                }catch (NoSuchElementException e2){}

                //2. 支付[这个要改进，不能用异常作为业务判断条件]
                final char ASCII_ZERO = '0';
                for (int index = 0; index < PAY_PASSWORD.length(); index++) {
                    int number = PAY_PASSWORD.charAt(index) - ASCII_ZERO;
                    sleep(TimeUnit.MILLISECONDS, 20);
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
        sleep(TimeUnit.SECONDS, 4);
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
