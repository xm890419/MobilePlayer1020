package custom.atguigu.mobileplayer1020.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import custom.atguigu.mobileplayer1020.IMusicPlayerService;
import custom.atguigu.mobileplayer1020.R;
import custom.atguigu.mobileplayer1020.service.MusicPlayerService;
import custom.atguigu.mobileplayer1020.utils.Utils;
import custom.atguigu.mobileplayer1020.view.BaseVisualizerView;
import custom.atguigu.mobileplayer1020.view.LyricShowView;

//那个地方用这个类Ctrl+G
public class SystemAudiolayerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnSwichLyric;
    private int position;
    private MyReceiver receiver;
    private LyricShowView lyric_show_view;
    /**
     * 进度更新
     */
    private static final int PROGRESS  = 1;
    private static final int SHOW_LYRIC  = 2;
    private Utils utils;
    private boolean notification;
    private BaseVisualizerView baseVisualizerView;
    private Visualizer mVisualizer;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-11 21:08:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_audiolayer);
        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnAudioPlaymode = (Button)findViewById( R.id.btn_audio_playmode );
        btnAudioPre = (Button)findViewById( R.id.btn_audio_pre );
        btnAudioStartPause = (Button)findViewById( R.id.btn__audio_start_pause );
        btnAudioNext = (Button)findViewById( R.id.btn_audio_next );
        btnSwichLyric = (Button)findViewById( R.id.btn_swich_lyric );
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);

        lyric_show_view = (LyricShowView) findViewById(R.id.lyric_show_view);

        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();

        btnAudioPlaymode.setOnClickListener( this );
        btnAudioPre.setOnClickListener( this );
        btnAudioStartPause.setOnClickListener( this );
        btnAudioNext.setOnClickListener( this );
        btnSwichLyric.setOnClickListener( this );
        //设置拖动监听
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-11 21:08:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnAudioPlaymode ) {
            // Handle clicks for btnAudioPlaymode
            changePlaymode();
        } else if ( v == btnAudioPre ) {
            // Handle clicks for btnAudioPre
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == btnAudioStartPause ) {
            // Handle clicks for btnAudioStartPause
            try {
                if(service.isPlaying()){
                    //暂停
                    service.pause();
                    //按钮状态—设置播放
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn__audio_start_selector);
                }else {
                    //播放
                    service.start();
                    //按钮状态—设置暂停
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn__audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == btnAudioNext ) {
            // Handle clicks for btnAudioNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == btnSwichLyric ) {
            // Handle clicks for btnSwichLyric
        }
    }

    private void changePlaymode() {
        //改变模式
        try {
            int playmode = service.getPlayMode();
            if(playmode == MusicPlayerService.REPEAT_NORMAL){
                playmode = MusicPlayerService.REPEAT_SINGLE;
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
                playmode = MusicPlayerService.REPEAT_ALL;
            }else if(playmode == MusicPlayerService.REPEAT_ALL){
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }else{
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保持模式
            service.setPlayMode(playmode);//保存到服务里面
            showButtonState(true);//包装方法Alt+shift+Q


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showButtonState(boolean isShowToast) throws RemoteException {
        int playmode;//从服务里获得最新的播放模式
        playmode = service.getPlayMode();
        if(playmode == MusicPlayerService.REPEAT_NORMAL){
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            if(isShowToast){
                Toast.makeText(this,"顺序播放",Toast.LENGTH_SHORT).show();
            }

        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            if(isShowToast){
                Toast.makeText(this,"单曲播放",Toast.LENGTH_SHORT).show();
            }

        }else if(playmode == MusicPlayerService.REPEAT_ALL){
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            if(isShowToast){
                Toast.makeText(this,"全部播放",Toast.LENGTH_SHORT).show();
            }
        }else{
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            if(isShowToast){

                Toast.makeText(this,"顺序播放",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private IMusicPlayerService service;
    private ServiceConnection conn = new ServiceConnection() {
        //当服务连接成功后回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if(service != null){
                //开始播放
                try {
                    if(notification){
                        //状态栏
                        showViewData();//可以直接再显示一次，或者从新再发广播
                        //再发一次广播
                        //service.notifyChange();
                    }else {
                        //列表来的
                        service.openAudio(position);
                    }
                    //校验它的状态
                    showButtonState(false);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        //当断开的时候回调
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC://显示歌词
                    try {
                        int currentPosition = service.getCurrentPosition();
                        lyric_show_view.setNextShowLyric(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
                case PROGRESS:
                    try {
                        //得到他的进度
                        int currentPosition = service.getCurrentPosition();
                        //得到时间
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));
                        //seekBar进度更新
                        seekbarAudio.setProgress(currentPosition);
                    } catch (RemoteException e) {

                        e.printStackTrace();
                    }
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        //绑定方式启动服务
        startAndBindService();
    }
    //接收广播
    private void initData() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver,intentFilter);//过滤器
        utils = new Utils();
    }
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(MusicPlayerService.OPEN_COMPLETE.equals(intent.getAction())){
                showViewData();
            }
        }
    }
    //显示视图的数据
    private void showViewData() {
        setupVisualizerFxAndUi();
        try {
            tvArtist.setText(service.getArtistName());
            tvName.setText(service.getAudioName());
            //得到总时长
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);
            //更新进度
            handler.sendEmptyMessage(PROGRESS);
            String path = service.getAudioPath();
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path+".lrc");
            if(!file.exists()){
                file = new File(path+".txt");
            }
            LyricParaser lyricParaser = new LyricParaser();
            lyricParaser.readFile(file);
            if(lyricParaser.isExistsLyric()){
                lyric_show_view.setLyrics(lyricParaser.getLyricBeans());
                //歌词同步
                handler.sendEmptyMessage(SHOW_LYRIC);
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
            } catch (RemoteException e) {
            e.printStackTrace();
            }
        System.out.println("audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        baseVisualizerView.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
        }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
             mVisualizer.release();
        }
    }


    @Override
    protected void onDestroy() {
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        if(conn != null){
            unbindService(conn);
            conn = null;
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        //绑定服务
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        //启动服务
        startService(intent);//防止服务多次创建
    }

    private void getData() {
        //true是从状态栏进入当前页面，false是从列表进入
        notification = getIntent().getBooleanExtra("notification",false);
        if(!notification){
            //得到播放位置 列表
            position = getIntent().getIntExtra("position",0);
        }
    }
}
