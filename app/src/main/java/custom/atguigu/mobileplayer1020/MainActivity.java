package custom.atguigu.mobileplayer1020;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import custom.atguigu.mobileplayer1020.base.BaseFragment;
import custom.atguigu.mobileplayer1020.fragment.LocalAudioFragment;
import custom.atguigu.mobileplayer1020.fragment.LocalVideoFragment;
import custom.atguigu.mobileplayer1020.fragment.NetAudioFragment;
import custom.atguigu.mobileplayer1020.fragment.NetVideoFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_main;
    private ArrayList<BaseFragment> fragments;
    /**
     * Fragment页面的下标位置
     */
    private int position;
    /**
     * 缓存的Fragment
     */
    private Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        //Android6.0动态获取权限
        isGrantExternalRW(this);
        initFragment();
        //设置RadioGroup的监听
        initListenter();
    }

    private void initListenter() {
        //设置RadioGroup选中状态变化的监听
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_local_video:
                        position = 0;
                        break;
                    case R.id.rb_local_audio:
                        position = 1;
                        break;
                    case R.id.rb_net_audio:
                        position = 2;
                        break;
                    case R.id.rb_net_video:
                        position = 3;
                        break;
                }
                //Fragment-当前的Fragment
                Fragment currentFragment = fragments.get(position);
                switchFragment(currentFragment);
            }


        });
        //默认选中本地视频
        rg_main.check(R.id.rb_local_video);
    }

    private void initFragment() {
        /**
         * 初始化Fragment
         * 有先后顺序要求
         */
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoFragment());
        fragments.add(new LocalAudioFragment());
        fragments.add(new NetAudioFragment());
        fragments.add(new NetVideoFragment());

    }
    private void switchFragment(Fragment currentFragment) {
        if(tempFragment != currentFragment){
            //开启事务
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //切换
            if(currentFragment != null){
                //是否添加过
                if(!currentFragment.isAdded()){
                    //把之前显示的给隐藏
                    if(tempFragment != null){
                        ft.hide(tempFragment);
                    }
                    //如果没有添加就添加
                    ft.add(R.id.fl_mainc_content,currentFragment);
                }else {
                    //把之前显示的给隐藏
                    if(tempFragment != null){
                        ft.hide(tempFragment);
                    }
                    //如果添加了就直接显示
                    ft.show(currentFragment);
                }
                //最后一步，提交事务
                ft.commit();
            }
            tempFragment = currentFragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(position != 0){
                //选中首页
                rg_main.check(R.id.rb_local_video);
                return true;
            }else if(!isExit){
                isExit = true;
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = true;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
