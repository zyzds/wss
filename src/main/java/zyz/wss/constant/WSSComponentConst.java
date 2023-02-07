package zyz.wss.constant;

import java.util.Arrays;
import java.util.List;

public class WSSComponentConst {
    public static final String USERFILE = "userfile";

    public static final String FOLDER = "folder";

    /**
     * 为没有拓展名的文件准备的路径
     */
    public static final String DEFAULT = "default";

    /**
     * 回收站保存时间
     */
    public static final int KEEPTIME = 10;

    public static final String BIN_DELETE = "delete";

    public static final String BIN_RECOVER = "recover";

    /**
     * 分享链接保存时间（天）
     */
    public static final long SHAREURLEXPIRE = 7;
    
    /**
     * 支持打开的文件格式
     */
    public static final List<String> IMGLIST = Arrays.asList("jpg", "png", "jpeg", "GIF");

    public static final List<String> VIDEOLIST = Arrays.asList("mp4", "ogg", "webm");

    public static final List<String> AUDIOLIST = Arrays.asList("mp3", "ogg", "wav");

    public static final List<String> TEXTLIST = Arrays.asList("txt");
}