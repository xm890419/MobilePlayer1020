package custom.atguigu.mobileplayer1020.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import custom.atguigu.mobileplayer1020.bean.LyricBean;
import custom.atguigu.mobileplayer1020.utils.DensityUtil;

/**
 * 作者：熊猛 on 2017/1/13 19:54 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：自定义显示歌词的控件
 */

public class LyricShowView extends TextView {
    private final Context mContext;
    private int width;
    private int height;
    private ArrayList<LyricBean> lyricBeans;
    private Paint paint;
    private Paint nopaint;
    private int index = 0;
    private float textHeight;
    private int currentPosition;
    private float sleepTime;
    private float timePoint;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        textHeight = DensityUtil.dip2px(mContext,20);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setTextSize(DensityUtil.dip2px(mContext,16));
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        nopaint = new Paint();
        nopaint.setTextSize(DensityUtil.dip2px(mContext,16));
        nopaint.setColor(Color.WHITE);
        nopaint.setTextAlign(Paint.Align.CENTER);
        nopaint.setAntiAlias(true);

        //lyricBeans = new ArrayList<>();
        //添加歌词的列表
       /* LyricBean lyricBean = new LyricBean();
        for(int i = 0 ;i<1000;i++){

            lyricBean.setContent("ssssss"+i);
            lyricBean.setSleepTime(1000+i);
            lyricBean.setTimePoint(i*1000);
            //添加到集合中、
            lyricBeans.add(lyricBean);
            //从新创建
            lyricBean = new LyricBean();
        }*/
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    //绘制歌词

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyricBeans != null && lyricBeans.size()>0){
            if(index != lyricBeans.size()){
                float plush = 0;
                if(sleepTime == 0){
                    plush = 0;
                }else {
                    // 这一句花的时间： 这一句休眠时间  =  这一句要移动的距离：总距离(行高)
                    //这一句要移动的距离 = （这一句花的时间/这一句休眠时间） * 总距离(行高)
                    plush = ((currentPosition - timePoint)/sleepTime)*textHeight;
                }
                canvas.translate(0,-plush);
            }
            //绘制歌词
            //当前句-绿色
            String content = lyricBeans.get(index).getContent();
            canvas.drawText(content,width/2,height/2,paint);
            //绘制前面部分
            float tempY = height/2;
            for(int i = index-1;i>=0;i--){
                tempY = tempY - textHeight;
                if(tempY <0 ){
                    break;
                }
                String preContent = lyricBeans.get(i).getContent();
                canvas.drawText(preContent,width/2,tempY,nopaint);
            }
            //绘制后面部分
            tempY = height/2;
            for(int i = index+1;i<lyricBeans.size();i++){
                tempY =tempY + textHeight;
                if(tempY > height){
                    break;
                }
                String nextContent = lyricBeans.get(i).getContent();
                canvas.drawText(nextContent,width/2,tempY,nopaint);
            }
        }else {
            //没有歌词
            canvas.drawText("没有歌词...",width/2,height/2,paint);
        }
    }
    //根据当前播放的位置计算高亮那一句，并且与歌曲播放同步
    public void setNextShowLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if(lyricBeans == null || lyricBeans.size() == 0) {
            return;
        }
        for(int i =1;i<lyricBeans.size();i++){
            if(currentPosition < lyricBeans.get(i).getTimePoint()){
                int indexTemp = i - 1;
                if(currentPosition >=lyricBeans.get(indexTemp).getTimePoint()){
                    //就是高亮的那一句
                    index = indexTemp;//某一句歌词的索引
                    sleepTime = lyricBeans.get(index).getSleepTime();
                    timePoint = lyricBeans.get(index).getTimePoint();
                }
            }else {
                index = i;
            }
        }
        invalidate();//强制绘制

    }
    //设置歌词列表
    public void setLyrics(ArrayList<LyricBean> lyricBeans) {
        this.lyricBeans = lyricBeans;
    }
}
