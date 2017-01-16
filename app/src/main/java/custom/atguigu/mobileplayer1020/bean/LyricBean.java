package custom.atguigu.mobileplayer1020.bean;

/**
 * 作者：熊猛 on 2017/1/13 19:58 *
 * 微信：xm890419
 * QQ ：506083998
 * 描述：一句歌词
 */

public class LyricBean {
    //歌词内容
    private String content;
    //时间戳
    private long timePoint;
    //高亮时间
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public String toString() {
        return "LyricBean{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
