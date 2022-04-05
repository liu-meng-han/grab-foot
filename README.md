# grap-food

> 叮咚买菜自动抢菜[目前仅支持叮咚自带支付]

> 目前仅支持安卓

我觉得实现方式上出了些问题，这种方式太繁琐了，准备基于安卓无障碍实现。

## 运行要求
1. AndroidSDK
2. JDK 8
3. Appium-Server

### 1.AndroidSDK 下载&配置
1.1 下载
> https://developer.android.com/studio#downloads

1.2 安装
> 傻瓜式下一步即可

1.3 环境变量配置
```shell
# pwd = ~/.bash_profile
# android home config
export ANDROID_HOME=/Users/chenjiacheng/Library/Android/sdk
export PATH=${PATH}:${ANDROID_HOME}/tools
export PATH=${PATH}:${ANDROID_HOME}/platform-tools
```

### 2.JDK 8 下载&配置
2.1 下载
>https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html

2.2 安装
> Win -> 傻瓜式安装
> Mac -> 傻瓜式安装
> 各系统安装步骤不同，不做展开，自行百度。

2.3 环境变量配置
```.shell
# pwd = ~/.bash_profile
# java home config
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_301.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export CLASS_PATH=$JAVA_HOME/lib
```

### 3.Appium 下载
3.1 appium-desktop 下载
> https://github.com/appium/appium-desktop/releases/tag/v1.22.2


### 4.真机运行
1. 手机端操作
```text
Setup 1: 打开开发者模式
SetUp 2: 通过USB连接到电脑
```

2. 电脑端操作
```.shell
$ adb devices
$ adb tcpip 5555
$ adb connect ${手机ip}:5555
```

3. Appium软件操作
```text
Setup 1: 打开软件
Setup 2: Host = 127.0.0.1
Setup 3: Edit Configuration 配置: 输入各环境的值即可。
Setup 4: 运行
```

4. 代码运行
```text
使用IDEA，打开项目。
修改grap-food.properties
运行即可。
```

5. 抢菜成功通知[通知相关功能正在实现中]
```text
成功后需要你自己去看订单状态。
```
