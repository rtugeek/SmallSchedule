SmallSchedule
=============
我13年的参赛作品

一个实用简单的课程表软件

集成友盟社会化分享 反馈组件 （http://www.umeng.com/）

集成百度第三方账号登陆 个人数据存储 （http://developer.baidu.com/services）

如果有报错，或出现异常，请自行更新SDK

集成Zxing二维码库 在项目QrCodeLibrary中

集成天气预报 项目中： com.free.schedule.service.WeatherService.java

参考：http://blog.csdn.net/hytfly/article/details/20064479

###添加课程代码
    ContentValues cv =new ContentValues();
    cv.put("name", "吃饭");//课程名
    cv.put("place", "食堂");//上课地点
    cv.put("dayOfWeek", 0);//星期几的课 从0开始 0星期一 1星期二...
    cv.put("startSection", 0);//第几节课开始  从0开始 0第一节 1第二节...
    cv.put("endSection",0);//第几节课结束，如果只有一节则跟startSection相同
    cv.put("teacher", "张老师");//任课老师
    ClassManager classManager = new ClassManager(context);
    classManager.insertClass(cv);

Mail : rtugeek@gmail.com
QQ : 873037232

 ![image](https://github.com/rtugeek/SmallSchedule/blob/master/Pic/0.png)![image](https://github.com/rtugeek/SmallSchedule/blob/master/Pic/1.png)

![image](https://github.com/rtugeek/SmallSchedule/blob/master/Pic/2.png)![image](https://github.com/rtugeek/SmallSchedule/blob/master/Pic/3.png)

