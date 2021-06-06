# ScanView

    扫描视图控件，直接继承View自定义，渲染效率高，视图不可见动画自动停止等优化处理，可支持边框和扫描线的定制，支持图片，颜色，xml资源等模式的绘制
    API接口丰富，可控制扫描速度插值器，扫描时间，停止/播放

# 效果图
    
  ![name](https://github.com/wcl9900/ScanView/blob/95a8df11501bae77616e3757c8e9f35e5ff298d7/scanview.gif)

**使用方式**
    
    Gradle
    repositories {
        jcenter()
    }
    cimplementation 'io.github.wcl9900:ScanView:1.0.4'
    
        1.创建,可通过xml布局创建，也可通过对象创建
        
        <com.wcl.scanview.ScanView
            android:id="@+id/scanview"
            android:layout_width="220dp"
            android:layout_height="220dp"
            android:layout_gravity="center"
            app:scanFrameDrawable="@drawable/icon_frame"
            app:scanLineDrawable="@drawable/icon_line"
            app:lineHeight="0.25"
            app:duration="2500"
            app:linePadding="1dp"/>
            
            或者
            
            ScanView scanView = new ScanView（context）
        
        2.使用接口：
        
        //启动扫描
        scanView.play();
        
        //停止扫描
        scanView.stop();
            
        //设定扫描线高度
        scanView.setLineHeight(0.3f);
        
        //扫描线内边距
        scanView.setLinePadding(10, 10,10,10);
        
        //扫描框内边距
        scanView.setFramePadding(0, 0, 0,0);
        
        //扫描时间
        scanView.setDuration(3000);
        
        //扫描线动画插值器
        scanView.setInterpolator(interpolator);
        
        //设定扫描框图片资源
        scanView.setDrawableFrame(R.drawable.icon_frame);
        
        //设定扫描线图片资源
        scanView.setDrawableLine(R.drawable.icon_line);
        
        
        #更多使用方式可查看demo
        