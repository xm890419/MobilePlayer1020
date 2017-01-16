package custom.atguigu.mobileplayer1020.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import custom.atguigu.mobileplayer1020.R;
import custom.atguigu.mobileplayer1020.activity.SearchActivity;

/**
 * Created by Administrator on 2017/1/6.
 */

public class TitleBarView extends LinearLayout implements View.OnClickListener {
    private Context mContent;
    private TextView tv_search;
    private RelativeLayout rl_game;
    private ImageView iv_record;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContent = context;
    }
    /**
     * 当布局加载完成后回调该方法
     * 得到孩子的实例
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                //Toast.makeText(mContent,"搜索",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContent, SearchActivity.class);
                mContent.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(mContent,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(mContent,"记录",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
