import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Programmer {
	private String name, sex, phone_number, email;

	private int age, work_experience;

	private Map<String, String> recordMap;

	private List<ProjectExperience> pes;

	private String professional_skill_str;

	public Map<String, String> getRecordMap() {
		return recordMap;
	}

	public void setRecordMap(Map<String, String> recordMap) {
		this.recordMap = recordMap;
	}

	public static void main(String[] args) {
		Programmer programmer = new Programmer("汤福乐", "man", 27, 5,
				"13418463415", "tangyoule@126.com");
		System.out.println("简历");
		System.out.println("姓名:" + programmer.getName());
		System.out.println("年龄:" + programmer.getAge());
		System.out.println("性别:" + programmer.getSex());
		System.out.println("工作年限:" + programmer.getWork_experience());
		System.out.println("手机号码:" + programmer.getPhone_number());
		System.out.println("邮箱地址:" + programmer.getEmail());
		System.out.println("工作经验");
		for (Map.Entry<String, String> entry : programmer.getRecordMap()
				.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		System.out.println("项目经验");
		for (ProjectExperience pe : programmer.getPes()) {
			System.out.println(pe.getProject_name());
			System.out.println("项目描述：" + pe.getProject_dec());
			System.out.println("责任描述：" + pe.getDuty_dec());
		}
		System.out.println(programmer.getProfessional_skill_str());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWork_experience() {
		return work_experience;
	}

	public void setWork_experience(int work_experience) {
		this.work_experience = work_experience;
	}

	public List<ProjectExperience> getPes() {
		return pes;
	}

	public void setPes(List<ProjectExperience> pes) {
		this.pes = pes;
	}

	public String getProfessional_skill_str() {
		return professional_skill_str;
	}

	public void setProfessional_skill_str(String professional_skill_str) {
		this.professional_skill_str = professional_skill_str;
	}

	/**
	 * 构造方法 name 姓名 sex 性别 age 年龄 work_experience 工作年限 phone_number 手机号码 email
	 * 邮箱地址
	 **/
	public Programmer(String name, String sex, int age, int work_experience,
			String phone_number, String email) {
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.work_experience = work_experience;
		this.phone_number = phone_number;
		this.email = email;
		initRecord();
		initProjectExperience();
		initProfessionalSkill();
	}

	private void initProfessionalSkill() {
		StringBuffer sb = new StringBuffer("专业技能:");
		sb.append(
				"扎实的J2SE基础，熟悉Http、Socket通信、多线程编程，熟悉OOP、AOP等设计思想、熟悉java中多种常用设计模式；")
				.append("熟悉Android基础，能设计最优性能的布局方案、掌握自定义控件、事件分发机制，了解Binder机制、进程间通信、插件化技术；")
				.append("能熟练阅读、查找Android API，英文部分技术文档，能快速熟练掌握各种常用开源项目（Xutils、Glide…），了解部分Android源码、JNI、NDK、Framework。")
				.append("能熟练使用常用SDK如：极光推送、Google(地图、推送)、Map Box、ShareSDK、友盟、百度定位、四月兄弟（BLE）、银联支付…")
				.append("有Android项目屏幕多种适配、内存优化、性能优化以及构架项目的经验。")
				.append("熟悉服务器开发，掌握SSH/SSI、SpringMVC以及Webservice等服务端框架、LeanCloud云服务平台,熟悉Jquery、EasyUI等前端框架开发,能熟练使用es、as等开发工具以及Git、svn等代码管理工具，经常使用的技术网站Github，Stack Overflow。良好的编码习惯、资源协调能力、快捷有效的工作能力和优秀的团队合作精神、抗压能力以及对新技术的高度热情。");
		professional_skill_str = sb.toString();
	}

	private void initProjectExperience() {
		pes = new ArrayList<>();
		ProjectExperience pe1 = getProjectExperience(
				"Fodel物流",
				"物流O2O项目，以解决最后一公里配送为切入点，实现线上注册，线上抢单，线下配送的物流app，包括管理员端和派送员端。 主要功能： 管理员：接收wms装箱线上推送包裹，分配包裹，监控派送员配送情况和积分收入统计功能等。 配送员：接收管理员分配包裹推送，派送订单路径规划。以及包裹派送签收以及配送情况统计等功能。",
				"负责架构整个项目体系，完成各个模块功能开发。");
		ProjectExperience pe2 = getProjectExperience(
				"虾逛",
				"虾逛是一款商城内位置服务LBS的APP , 立志于改变传统的Shopping Mall的服务模式和用户体验 , 把线下实体店与移动互联网 进行有机结合 , 旨在为消费者提供更便捷更准确的购物餐饮体验 , 同时为商城打造智能化升级与O2O闭环 , 达到为线上线下引流的效果，主要功能包括室内定位，寻车导航，商家优惠活动信息推送等。",
				"1、完成应用框架搭建 2、编写程序代码，调试代码，BUG修复。 3、代码评审走读，代码规范。 4、开发任务安排，项目进度跟进。 5、应用发布。");
		ProjectExperience pe3 = getProjectExperience(
				"预约挂号客户端",
				"预约挂号客户端是一款可以在线预约异地和包括深圳本地的线上的160家医院的医生的android手机应用软件。提供医院、科室、医生以及排班号源查询，可以及时的为用户提供便捷的了解所需要的信息。预约订单提供手机支付、话费支付、以及银联绑定支付等三种支付方式，使得支付更容易。",
				"1、负责项目需求沟通； 2、V3.0版本现有功能维护以及新功能开发； 3、版本系统设计、开发以及性能调优。");
		ProjectExperience pe4 = getProjectExperience(
				"广西集团通讯录客户端",
				"广西集团通讯录是广西移动分公司和广西农垦局的多部门大用户量的背景下，合作开发的一款手机APP，应用除了具有一般普通意义上通讯录功能如：通话记录、通话详情、拨打电话、发短信等功能外还提供多部门、联系人数据同步；即时聊天等功能的特色的一款通讯录。系统采用TCP协议，短连接服务提供数据同步，更节省流量。",
				"1、负责需求文档、设计文档编写。 2、需求、设计评审、工作量评估、功能开发分工协调。 3、程序设计、代码编写、BUG修改。 4、性能调优。");
		ProjectExperience pe5 = getProjectExperience(
				"深圳社保通客户端",
				"深圳社保通客户端是由深圳移动无线城市以及深圳人力资源和社会保障局合作提供联合开发的一款手机应用，功能主要包括:参保用户基本情况查询、个人账户查询、医保消费查询、社保缴费明细查询、授权码生成、社保卡挂失等。",
				"1、参与需求沟通； 2、程序设计以及代码编写； 3、BUG修改以及打包发布。");
		ProjectExperience pe6 = getProjectExperience(
				"深圳无线城市",
				"深圳无限城市是深圳移动重点推广项目，它包含范围广，涉及领域大，深圳交警、便民、影视、医疗、社保等一系列产品。为深圳市民提供方便、快捷的生活：深圳交警提供违章查询，路况查询等等；预约挂号为市民提供便捷的综合医疗服务平台；影视频道可以提供兑换券购买、订座等服务。",
				"主要负责深圳无线城市两大主要的频道： 影视频道：在影视频道的开发过程中，全权负责，包括需求沟通、程序设计、以及现网问题维护等。 医疗频道：主要负责现网维护以及合作商联调工作。处理用户投诉的现网BUG以及优化程序的工作。");
		pes.add(pe1);
		pes.add(pe2);
		pes.add(pe3);
		pes.add(pe4);
		pes.add(pe5);
		pes.add(pe6);
	}

	private void initRecord() {

		recordMap = new HashMap<>();
		recordMap.put("2011/06-2012/11", "深圳飞锦科技有限公司");
		recordMap.put("2012/11-2015/02", "深圳艾派应用系统有限公司");
		recordMap.put("2015/02-2015/07", "深圳志展云图科技有限公司");
		recordMap.put("2015/07/-至今", "深圳德凯胜科技有限公司");
	}

	public ProjectExperience getProjectExperience(String project_name,
			String project_dec, String duty_dec) {
		return new ProjectExperience(project_name, project_dec, duty_dec);
	}

	/** 内部类 项目经验 **/
	class ProjectExperience {
		private String project_name;

		public String getProject_name() {
			return project_name;
		}

		public void setProject_name(String project_name) {
			this.project_name = project_name;
		}

		public String getProject_dec() {
			return project_dec;
		}

		public void setProject_dec(String project_dec) {
			this.project_dec = project_dec;
		}

		public String getDuty_dec() {
			return duty_dec;
		}

		public void setDuty_dec(String duty_dec) {
			this.duty_dec = duty_dec;
		}

		private String project_dec;

		private String duty_dec;

		/**
		 * 构造方法 project_name 项目名称 project_dec 项目描述 duty_dec 职责描述
		 **/
		public ProjectExperience(String project_name, String project_dec,
				String duty_dec) {
			this.project_name = project_name;
			this.project_dec = project_dec;
			this.duty_dec = duty_dec;
		}
	}
}
