package site.chenjiacheng;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Create by chenjiacheng on 2022/4/5
 */
public class MainTest {

    @Test
    public void stringTest(){
        Map<String, Object> map = new KeyEvent(AndroidKey.DIGIT_4).build();

        String passwd = "123456";
        char ascii_zero = '0';
        for (int i = 0; i < passwd.length(); i++) {
            char ascii_number = passwd.charAt(i);
            int number = ascii_number-ascii_zero;
            int android_key = number+AndroidKey.DIGIT_0.getCode();
            System.out.printf("number (%d) = android_key(%d)\n",number,AndroidKey.valueOf("DIGIT_"+number).getCode());
        }
    }

}
