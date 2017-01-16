package custom.atguigu.mobileplayer1020.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import custom.atguigu.mobileplayer1020.base.BaseFragment;

/**
 * Created by Administrator on 2017/1/6.
 */

public class NetAudioFragment extends BaseFragment {
    private TextView textView;
    @Override
    public View initView() {
        textView = new TextView(mContext);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("网络音频...");
    }

    @Override
    public void onRefreshData() {
        super.onRefreshData();
        textView.setText("网络音频刷新");
    }
}
