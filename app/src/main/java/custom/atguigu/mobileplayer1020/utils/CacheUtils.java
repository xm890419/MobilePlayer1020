package custom.atguigu.mobileplayer1020.utils;

import android.content.Context;
import android.content.SharedPreferences;

import custom.atguigu.mobileplayer1020.service.MusicPlayerService;

/**
 * 作者：熊猛 on 2017/1/11 18:58 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：缓存工具类
 */

public class CacheUtils {
    public static String getString(Context mContext, String key) {
        //得到缓存的文本数据
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
        //保存数据
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    //得到播放模式
    public static int getPlaymode(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }
    //保存播放模式
    public static void putPlaymode(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();

    }
}
