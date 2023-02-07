package zyz.wss;

import org.junit.Test;

/* @RunWith(SpringRunner.class)
@SpringBootTest */
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
		String html = "<p style='text-indent:32.0000pt;text-autospace:ideograph-numeric;vertical-align:baseline;line-height:28.0000pt;'><span style='font-family:仿宋_GB2312;font-size:16.0000pt;'><font face='仿宋_GB2312'>会议强调，生产效率提升的关键在于人员的合理调配与生产的有效组织、监督，各车间负责人必须摒弃</font>“产量提升依靠人员增加”的错误思想，严控员工数量，在提高全员劳动生产率上想办法、下功夫。会议规定，自即日起，除装配车间总装班外各车间一律不得扩招员工。</span></p>\n<p style='text-indent:32.0000pt;text-autospace:ideograph-numeric;vertical-align:baseline;line-height:28.0000pt;'><span style='font-family:仿宋_GB2312;font-size:16.0000pt;'><font face='仿宋_GB2312'>会议要求本部以二季度全员生产率提升</font>20%为目标，湖南电气公司以二季度全员生产率提升50%为目标，想方设法提升生产效率，严控产品质量。</span></p>\n<p style='text-indent:32.0000pt;text-autospace:ideograph-numeric;vertical-align:baseline;line-height:28.0000pt;'><span style='font-family:仿宋_GB2312;font-size:16.0000pt;'><font face='仿宋_GB2312'>要求陈保龙总助、吴华林负责督促湖南电气公司生产车间于</font></span><span style='font-family:仿宋_GB2312;font-size:16.0000pt;'>2月28日前完成变压器生产车间物流优化，确保各类</span><span style='font-family:仿宋_GB2312;font-size:16.0000pt;'><font face='仿宋_GB2312'>原材料、组配件放置到所需工序旁，减少二次搬运。</font></span></p>&nbsp;";
		System.out.println(html.replaceAll("<[.[^<]]*>", "").replaceAll("\r|\n", "").replaceAll("\\&[a-zA-Z]{1,10};", ""));
	}

}
