package custom.atguigu.mobileplayer1020.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import custom.atguigu.mobileplayer1020.R;
import custom.atguigu.mobileplayer1020.activity.SystemVideoPlayerActivity;
import custom.atguigu.mobileplayer1020.adapter.NetVedioAdapter;
import custom.atguigu.mobileplayer1020.base.BaseFragment;
import custom.atguigu.mobileplayer1020.bean.MediaItem;
import custom.atguigu.mobileplayer1020.utils.CacheUtils;
import custom.atguigu.mobileplayer1020.utils.Constant;

/**
 * Created by Administrator on 2017/1/6.
 */

public class NetVideoFragment extends BaseFragment {
    /**
     * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;
    private NetVedioAdapter adapter;
    @ViewInject(R.id.listview)
    private ListView listview;
    @ViewInject(R.id.tv_no_media)
    private TextView tv_no_media;
    @ViewInject(R.id.refresh)
    private MaterialRefreshLayout refresh;
    @Override
    public View initView() {

        View view  = View.inflate(mContext, R.layout.fragment_net_video,null);
        //把view注入到xUtils3框中
        x.view().inject(NetVideoFragment.this,view);
        //才初始化好的

        listview.setOnItemClickListener(new MyOnItemClickListener());
        //监听上拉和下拉刷新
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());
        return view;
    }
    //是否加载更多
    private boolean isloadMore = false;
    class MyMaterialRefreshListener extends MaterialRefreshListener{

        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //Toast.makeText(mContext,"下拉刷新",Toast.LENGTH_SHORT).show();
            isloadMore = false;
            getDataFromNet();
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            isloadMore = true;
            getDataFromNet();
            //Toast.makeText(mContext,"上拉刷新",Toast.LENGTH_SHORT).show();
        }
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //传递列表数据
            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        //textView.setText("网络视频...");
        String json = CacheUtils.getString(mContext,Constant.NET_URL);
        if(!TextUtils.isEmpty(json)){
            parsedJson(json);
        }

        getDataFromNet();
    }
    //使用xutils3联网请求数据
    private void getDataFromNet() {
        //网络的路径
        RequestParams params = new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(mContext,Constant.NET_URL,result);
                processData(result);
                if(!isloadMore){
                    //完成刷新
                    refresh.finishRefresh();
                }else {
                    //把上拉刷新隐藏
                    refresh.finishRefreshLoadMore();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    /**
     * 解析json数据：gson解析，fastjson解析和手动解析（原生的api）
     * 显示数据-设置适配器
     * @param json
     */
    private void processData(String json) {
        if(!isloadMore){
            mediaItems = parsedJson(json);
            if(mediaItems != null && mediaItems.size() >0){
                //有数据
                tv_no_media.setVisibility(View.GONE);;
                adapter = new NetVedioAdapter(mContext,mediaItems);
                //设置适配器
                listview.setAdapter(adapter);

            }else{
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }else {
            //加载更多
            ArrayList<MediaItem> mediaItem = parsedJson(json);
            mediaItems.addAll(mediaItem);
            //刷新适配器
            adapter.notifyDataSetChanged();
        }

    }
    /**
     * 使用系统的接口解析json数据
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");


            for (int i=0;i<jsonArray.length();i++){

                MediaItem mediaItem = new MediaItem();

                mediaItems.add(mediaItem);//添加到集合中

                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaItem.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaItem.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    @Override
    public void onRefreshData() {
        super.onRefreshData();
        //textView.setText("网络视频刷新");
    }
}
