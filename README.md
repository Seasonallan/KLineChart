# 第一步
在根目录的 build.gradle中 allprojects的repositories里添加jitpack依赖   
maven { url 'https://jitpack.io' }   
# 第二步
在app项目的build.gradle下的dependencies中添加依赖   
implementation 'com.github.Seasonallan:KLineChart:2.5'   
implementation 'com.google.android.material:material:1.2.1' //使用Recyclerview和TabLayout
# 第三步
打开K线图方法
```Java
                Intent intent = new Intent(MainActivity.this, KLineChartActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("coinCode", "BTC_USDT");//币种
                bundle.putString("language", "ZH_CN");//语言
                bundle.putString("webSocketUrl", "");//长连接地址
                bundle.putString("briefUrl", "");//请求币种信息接口
                bundle.putBoolean("riseGreen", true);//绿涨红跌
                bundle.putBoolean("nightMode", true);//夜间模式

                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, 100);
```
         
# 注意事项
使用demo时，gradle版本不一致方案：   
中断更新gradle   
修改根目录下的build.gradle中的classpath "com.android.tools.build:gradle:4.0.1" 为你的版本   
修改根目录下的gradle/wrapper/gradle-wrapper.properties中的distributionUrl为你的版本   
关闭重启项目   

 