package zyz.wss;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class JavaTest {
    @Test
    public void test1() {
        //Assert.isInstanceOf(WSSComponent.class, (WSSComponent) null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date()));
    }
}