package zyz.wss.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import zyz.wss.constant.WSSComponentConst;

public class WssUtil {
    public static String MD5Encode(String str) {
        MessageDigest md;
        StringBuilder sb = new StringBuilder();
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    sb.append(0);
                }
                sb.append(hv);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sb.toString();
    }

    public static LocalDateTime dateToLocalDateTimeConverter(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date localDateTimeToDateConverter(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String nameRepeatHandle(String src) {
        int index = src.lastIndexOf(".");
        String typeName = index == -1 ? "" : "." + (src.substring(src.lastIndexOf(".") + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        src = (index == -1 ? src : src.substring(0, src.lastIndexOf("."))) + sdf.format(new Date()) + typeName;
        return src;
    }

    public static int daysBetween2dates(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return 0;
        }
        long mills1 = d1.getTime();
        long mills2 = d2.getTime();
        long differ = mills2 - mills1;
        return WSSComponentConst.KEEPTIME - (int) (differ / (1000 * 60 * 60 * 24));
    }
}