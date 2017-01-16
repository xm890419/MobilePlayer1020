package custom.atguigu.mobileplayer1020.app;

import android.app.Application;

import org.xutils.x;

/**
 * 作者：熊猛 on 2017/1/10 23:07 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：代表整个软件
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志...
    }
}
