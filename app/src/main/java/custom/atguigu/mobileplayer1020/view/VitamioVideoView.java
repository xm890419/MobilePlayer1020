package custom.atguigu.mobileplayer1020.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


/**
 * 作者：熊猛 on 2017/1/9 20:09 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {
    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
    /**
     * 根据传入视频的大小设置，设置视频的画面大小
     * @param screenWidth
     * @param screeHeight
     */
    public void setViewSize(int screenWidth,int screeHeight){
        //视频画面的宽和高
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = screenWidth;
        l.height = screeHeight;
        setLayoutParams(l);
    }
}
