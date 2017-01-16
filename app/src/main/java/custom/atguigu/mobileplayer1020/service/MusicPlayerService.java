package custom.atguigu.mobileplayer1020.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import custom.atguigu.mobileplayer1020.IMusicPlayerService;
import custom.atguigu.mobileplayer1020.R;
import custom.atguigu.mobileplayer1020.activity.SystemAudiolayerActivity;
import custom.atguigu.mobileplayer1020.bean.MediaItem;
import custom.atguigu.mobileplayer1020.utils.CacheUtils;

/**
 * 作者：熊猛 on 2017/1/11 21:25 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：播放音乐的服务
 */

public class MusicPlayerService extends Service {
    public static final String OPEN_COMPLETE = "open_complete";
    //AIDL生成的类
    IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub(){
        //把服务当成成员变量
        MusicPlayerService service = MusicPlayerService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            service.setPlayMode(mode);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }

        @Override
        public void notifyChange() throws RemoteException {
            service.notifyChange(OPEN_COMPLETE);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return mediaItem.getData();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };
    private ArrayList<MediaItem> mediaItems;
    //音频是否加载完成
    private boolean isloaded = false;
    private MediaItem mediaItem;
    private int position;
    //播放器
    private MediaPlayer mediaPlayer;
    //顺序播放
    public static final int REPEAT_NORMAL = 0;
    //单曲循环
    public static final int REPEAT_SINGLE = 1;
    //全部循环
    public static final int REPEAT_ALL = 2;
    //播放模式
    public int playmode = REPEAT_NORMAL;
    private boolean isNext = false;

    //返回代理类
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playmode = CacheUtils.getPlaymode(this,"playmode");
        getDataFromLocal();
    }

    //子线程中得到音频
    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //初始化集合
                mediaItems = new ArrayList<MediaItem>();
                //ContentResolver resolver = mContext.getContentResolver();
                //服务本身就是上下文
                ContentResolver resolver = getContentResolver();
                //sdcard 的视频路径
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sdcard显示的视频名称
                        MediaStore.Audio.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小-byte
                        MediaStore.Audio.Media.DATA,//在sdcard的路径-播放地址
                        MediaStore.Audio.Media.ARTIST//艺术家
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        //添加到集合中
                        mediaItems.add(mediaItem);//可以
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);//播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //音频加载完成
                isloaded = true;

            }
        }.start();
    }

    /**
     * 根据位置打开一个音频并且播放
     * @param position
     */
    void openAudio(int position){
        if(mediaItems != null && mediaItems.size()>0){
           mediaItem = mediaItems.get(position);
            //接收位置
            this.position = position;
            if(mediaPlayer != null){
                mediaPlayer.reset();//释放 上一曲重置
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            //设置三个监听 准备 播放完成 播放出错
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
            mediaPlayer.setOnErrorListener(new MyOnErrorListener());
            //设置播放地址
            try {
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();//异步
                isNext = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(!isloaded){
            Toast.makeText(this,"没有加载完成",Toast.LENGTH_SHORT).show();
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //一般播放出错回拨下一个
            next();
            return true;
        }
    }
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            isNext = true;
            next();

        }
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            //准备好的时候发广播
            notifyChange(OPEN_COMPLETE);//发一个打开完成的字符串
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        //发广播
        sendBroadcast(intent);
    }
    private NotificationManager manager;
    /**
     * 开始播放音频
     */
    void start() {
        mediaPlayer.start();
        //状态栏显示播放状太
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延期意图
        Intent intent = new Intent(this, SystemAudiolayerActivity.class);
        intent.putExtra("notification",true);
        PendingIntent peningIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setContentTitle("321音乐").setContentText("正在播放："+getAudioName())
                    .setContentIntent(peningIntent).build();
            //设置点击后还存在
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        }
        manager.notify(1,notification);
    }

    /**
     * 暂停
     */
    void pause() {
        mediaPlayer.pause();
        //取消显示
        manager.cancel(1);
    }

    /**
     * 得到歌曲的名称
     */
    String getAudioName() {
        if(mediaItem != null){
            return mediaItem.getName();
        }
        return "";
    }

    /**
     * 得到歌曲演唱者的名字
     */
    String getArtistName() {
        if(mediaItem != null){
            return mediaItem.getArtist();
        }
        return "";
    }

    /**
     * 得到歌曲的当前播放进度
     */
    int getCurrentPosition() {
        if(mediaItem != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 得到歌曲的当前总进度
     */
    int getDuration() {
        if(mediaItem != null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 播放下一首歌曲
     */
    void next() {
        //设置下一曲对应的位置
        setNextPosition();
        //根据对应的位置去播放
        openNextAudio();
    }

    private void openNextAudio() {
        int playmode = getPlayMode();
        if(playmode == MusicPlayerService.REPEAT_NORMAL){
            if(position <= mediaItems.size()-1){
                openAudio(position);
            }else {
                position = mediaItems.size()-1;
            }
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if(position <= mediaItems.size()-1){
                openAudio(position);
            }else {
                position = mediaItems.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();
        if(playmode == MusicPlayerService.REPEAT_NORMAL){
            position++;
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            if(!isNext){
                isNext = false;
                position++;
                if(position > mediaItems.size()-1){
                    position = 0;
                }
            }
        }else if(playmode == MusicPlayerService.REPEAT_ALL){
            position++;
            if(position > mediaItems.size()-1){
                position = 0;
            }
        }else {
            position++;
        }
    }

    /**
     * 播放上一首歌曲
     */
    void pre() {
        //设置上一曲对应的位置
        setPrePosition();
        //根据对应的位置去播放
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if(playmode == MusicPlayerService.REPEAT_NORMAL){
            if(position >=0){
                openAudio(position);
            }else {
                position = 0;
            }
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if(position >=0){
                openAudio(position);
            }else {
                position = 0;
            }
        }

    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if(playmode == MusicPlayerService.REPEAT_NORMAL){
            position--;
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            if(!isNext){
                isNext = false;
                position--;
                if(position <0){
                    position = mediaItems.size()-1;
                }
            }
        }else if(playmode == MusicPlayerService.REPEAT_ALL){
            position--;
            if(position <0 ){
                position = mediaItems.size()-1;
            }
        }else {
            position--;
        }

    }

    /**
     * 得到播放模式
     */
    int getPlayMode() {
        return playmode;
    }

    /**
     * 设置播放模式
     */
    void setPlayMode(int mode) {
        this.playmode = mode;
        //保存播放模式
        CacheUtils.putPlaymode(this,"playmode",playmode);
    }
}
