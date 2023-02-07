package zyz.wss.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

public class VerificationCodeUtil {
    public class VerificationCode {
        public static final int EXPIRE = 360; // 过期时间360秒

        private String id;
        private BufferedImage image;
        private String answer;

        VerificationCode() {
            this.id = UUID.randomUUID().toString();
        }

        public String getId() {
            return id;
        }

        void setId(String id) {
            this.id = id;
        }

        public String getAnswer() {
            return answer;
        }

        void setAnswer(String answer) {
            this.answer = answer;
        }

        public BufferedImage getImage() {
            return image;
        }

        void setImage(BufferedImage image) {
            this.image = image;
        }
    }

    public static VerificationCode VerificationCodeGenerator() {
        VerificationCode code = new VerificationCodeUtil().new VerificationCode();
        String question = generateRomdomQuestion(code);
        code.setImage(drawPic(question));
        return code;
    }

    private static String generateRomdomQuestion(VerificationCode code) {
        Random random = new Random();
        int numA = random.nextInt(100);
        int numB = random.nextInt(10);
        int opt = random.nextInt(3);
        String optString = null;
        int answer;
        switch (opt) {
            case 0:
                answer = numA + numB;optString = "+";
                break;
            case 1:
                answer = numA - numB;optString = "-";
                break;
            case 2:
                answer = numA * numB;optString = "*";
                break;
            default:
                answer = 0;
        }
        code.setAnswer(String.valueOf(answer));
        return String.valueOf(numA) + optString + String.valueOf(numB) + "=?";
    }

    private static int h = 75;// height
    private static int w = 150;// width
    private static Random r = new Random();
    private static String[] fontNames = { "宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312" };
    private static Color bgColor = new Color(255, 255, 255);

    private static BufferedImage drawPic(String content) {
        BufferedImage image = createImage();
        Graphics2D g = (Graphics2D) image.getGraphics();
        for (int i = 0; i < content.length(); i++) {
            float x = i * (w / 6) * 1.0F;
            g.setFont(randomFont());
            g.setColor(randomColor());
            g.drawString(String.valueOf(content.charAt(i)), x, h - 15);
            g.drawLine(r.nextInt(150), r.nextInt(75), r.nextInt(150), r.nextInt(75));
        }
        return image;
    }

    private static Font randomFont() {
        int r1 = r.nextInt(fontNames.length);
        int r2 = r.nextInt(4);
        int r3 = r.nextInt(5) + 40;
        Font f = new Font(fontNames[r1], r2, r3);
        return f;
    }

    private static Color randomColor() {
        int red = r.nextInt(150);
        int blue = r.nextInt(150);
        int green = r.nextInt(150);
        return new Color(red, green, blue);
    }

    private static BufferedImage createImage() {
        BufferedImage image = new BufferedImage(150, 75, BufferedImage.TYPE_INT_RGB);// 创建图片缓冲区
        Graphics2D g = (Graphics2D) image.getGraphics();// 得到绘制环境
        g.setColor(bgColor);// 设置背景颜色
        g.fillRect(0, 0, w, h);// 填充整个图片
        return image;
    }

    public static void main(String[] args) throws IOException {
        VerificationCode code = VerificationCodeGenerator();
        BufferedImage image = code.getImage();
        OutputStream stream = new FileOutputStream(new File("D:\\code.png"));
        ImageIO.write(image, "png", stream);
        System.out.println(code.getAnswer());
    }
}