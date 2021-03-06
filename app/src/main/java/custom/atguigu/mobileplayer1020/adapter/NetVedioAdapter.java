package custom.atguigu.mobileplayer1020.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

import custom.atguigu.mobileplayer1020.R;
import custom.atguigu.mobileplayer1020.bean.MediaItem;
import custom.atguigu.mobileplayer1020.utils.Utils;

/**
 * 作者：熊猛Administrator on 2017/1/7 13:44 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：本地视频的适配器
 */

public class NetVedioAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MediaItem> datas;
    private Utils utils;
    private ImageOptions imageOptions;

    public NetVedioAdapter(Context mContext, ArrayList<MediaItem> mediaItems) {
        this.mContext = mContext;
        this.datas = mediaItems;
        utils = new Utils();
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)//加载过程中的默认图片
                .setFailureDrawableId(R.drawable.video_default)//就挨着出错的图片
                .build();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据位置得到对应的数据
        MediaItem mediaItem = datas.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());//设置名称
        //设置文件大小
        viewHolder.tv_size.setText(mediaItem.getDuration() + "秒");
        //设置时间
        viewHolder.tv_duration.setText(mediaItem.getDesc());
        //请求图片
        x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl(),imageOptions);
        /*Picasso.with(mContext)
                .load(mediaItem.getImageUrl())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);*/
        /*Glide.with(mContext)
                .load(mediaItem.getImageUrl())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);*/


        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;

    }
}
