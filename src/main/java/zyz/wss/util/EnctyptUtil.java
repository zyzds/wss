package zyz.wss.util;

import org.springframework.util.StringUtils;

/**
 * 工具类：自定义加解密 适用于匿名请求中对参数进行加密，或其他自定义加解密场景
 * 
 */
public class EnctyptUtil {

	/**
	 * 奇数位异或标准，16进制“60”(String)转换为10进制Integer，可自定义
	 */
	private static final Integer XOR_ODD = Integer.parseInt("60", 16);

	/**
	 * 偶数位异或标准，16进制“51”(String)转换为10进制Integer，可自定义
	 */
	private static final Integer XOR_EVEN = Integer.parseInt("51", 16);

	/**
	 * 解密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static final String decode(String content) {
		if (StringUtils.isEmpty(content))
			return content;
		String result = "";
		int i = 0;
		if (content.length() % 2 != 0) {
			// 若密文字符串长度为奇数，则添加一位“0”
			content = "0" + content;
			// 从第二位开始解析
			i = 1;
		}
		while (i < content.length()) {
			int intValue = content.charAt(i);
			// 异或，区分奇/偶数位
			int xor = i % 2 != 0 ? intValue ^ XOR_ODD : intValue ^ XOR_EVEN;
			result = (char) xor + result;// 倒序排列
			i++;
		}
		return result;
	}

	/**
	 * 加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static final String encode(String content) {
		if (StringUtils.isEmpty(content))
			return content;
		String result = "";
		int i = 0;
		while (i < content.length()) {
			int intValue = content.charAt(i);
			// 异或，区分奇/偶数位
			int xor = i % 2 == 0 ? intValue ^ XOR_ODD : intValue ^ XOR_EVEN;
			result = (char) xor + result;// 倒序排列拼接，拼接前先转换为unicode字符
			i++;
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println("3409b005b3048422c42adfe4a6c17bd0");
		String s;
		System.out.println(s = encode("3409b005b3048422c42adfe4a6c17bd0"));
		System.out.println(decode(s));
	}

}
