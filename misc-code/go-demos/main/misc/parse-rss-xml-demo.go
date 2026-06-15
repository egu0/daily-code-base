package main

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"net/http"
)

type CyberSecurityNews struct {
	Titles       []string `xml:"channel>item>title"`
	Links        []string `xml:"channel>item>link"`
	Descriptions []string `xml:"channel>item>description"`
}

func main() {
	resp, _ := http.Get("https://www.freebuf.com/feed")
	bytes, _ := ioutil.ReadAll(resp.Body)
	_ = resp.Body.Close() // ignore the return value

	bodyString := string(bytes)
	fmt.Println(bodyString)

	var n CyberSecurityNews
	xml.Unmarshal(bytes, &n)
	for _, title := range n.Titles {
		fmt.Println(title)
	}
}

/*
const originalXML = `<?xml version="1.0" encoding="UTF-8"?>
<rss xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:atom="http://www.w3.org/2005/Atom" version="2.0">
  <channel>
    <title>FreeBuf网络安全行业门户</title>
    <link>https://www.freebuf.com</link>
    <description/>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" href="https://www.freebuf.com/feed" type="application/rss+xml" rel="self"/>
    <language>zh-CN</language>
    <pubDate>Fri, 22 Sep 2023 12:55:01 +0800</pubDate>
    <item>
      <title><![CDATA[涉及美国海岸警卫队，法国国防技术制造商 Exail暴露了数据库访问权限]]></title>
      <link>https://www.freebuf.com/news/378873.html</link>
      <description><![CDATA[Cyber​​news 研究团队发现，法国高科技工业集团 Exail 暴露了一个带有数据库凭证的可公开访问的环境 (.env) 文件。]]></description>
      <content:encoded><![CDATA[<p>Cyber​​news 研究团队发现，法国高科技工业集团 Exail 暴露了一个带有数据库凭证的可公开访问的环境 (.env) 文件。</p><p/20230922/1695353289_650d09c915ff7d7e27665.png!small" alt="" width="690" height="376]]></content:encoded>
      <category>资讯</category>
      <pubDate>Fri, 22 Sep 2023 11:27:25 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[美国政府发出 Snatch 勒索软件警告]]></title>
      <link>https://www.freebuf.com/news/378862.html</link>
      <description><![CDATA[受害组织来自多个关键基础设施领域，包括国防工业基地、食品和农业以及科技领域。]]></description>
      <content:encoded><![CDATA[<p>昨天（9月21日），美国当局发布了一份新的网络安全公告，介绍了Snatch勒索软件即服务（RaaS）组织使用的最新战术、技术和ch于2018年首次出现，但自2021年以来一直在学习借鉴其他勒索软件的技术，现已发展“壮大”。</p><p>该勒索软件采用了经典的双重勒索“玩法”，如果受害者不付钱，]]></content:encoded>
      <category>资讯</category>
      <pubDate>Fri, 22 Sep 2023 10:41:23 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[金额超2000万，思科收购网安巨头Splunk]]></title>
      <link>https://www.freebuf.com/news/378858.html</link>
      <description><![CDATA[9月21日，思科宣布将收购网络安全公司 Splunk，这笔交易价值约 280 亿美元，收购价格为每股 157 美元。]]></description>
      <content:encoded><![CDATA[<p>9月21日，思科宣布将收购网络安全公司 Splunk，这笔交易价值约 280 亿美元（2047 亿元人民币），收购价格为每股 157 美元30922/1695349900_650cfc8c2aa3caa14383a.jpg!small" alt="" /></p><p>这将成为思科有史以来最大的一笔收购交]]></content:encoded>
      <category>资讯</category>
      <pubDate>Fri, 22 Sep 2023 10:31:53 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[国家标准《网络关键设备安全技术要求 可编程逻辑控制器（PLC）》征求意见稿发布]]></title>
      <link>https://www.freebuf.com/news/378818.html</link>
      <description><![CDATA[2023年9月21日，全国信息安全标准化技术委员会发布国家标准《网络关键设备安全技术要求 可编程逻辑控制器（PLC）》（征求意见稿）。]]></description>
      <content:encoded><![CDATA[<p>2023年9月21日，全国信息安全标准化技术委员会发布国家标准《网络关键设备安全技术要求 可编程逻辑控制器（PLC）》（征求济研究所、中国网络安全审查技术与认证中心、国家计算机网络应急技术处理协调中心国家信息技术安全研究中心、公安部第三研究所、工业和信息化部计算机与微电子发展研究中心(中国软]]></content:encoded>
      <category>资讯</category>
      <pubDate>Thu, 21 Sep 2023 18:33:28 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[FB 赠书第 101 期 | 404 Paper 精粹 2023 上册发布！]]></title>
      <link>https://www.freebuf.com/fevents/378779.html</link>
      <description><![CDATA[本次活动将免费送出 10 本赠书，在 FreeBuf 网站活动文章页下留言参与话题互动。]]></description>
      <content:encoded><![CDATA[<p>2023 年已过大半，<strong>《404 Paper 精粹》</strong>如约发至第 5 期，也就是 2023 上册。这本半年刊是知道创宇 404 实验室作为知道创宇核心部门，专注于 Web 、IoT 、工控等领域内安全漏洞挖掘、攻防技术的研究工作。一直以来，404 实验室小伙伴热衷于用文章记录、分享安全研究中的成果]]></content:encoded>
      <category>活动</category>
      <pubDate>Thu, 21 Sep 2023 15:45:48 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[猎鸭行动 | Qakbot僵尸网络覆灭记，猖獗15年之久]]></title>
      <link>https://www.freebuf.com/articles/378746.html</link>
      <description><![CDATA[为何Qakbot如此臭名昭著，它到底有何能耐，它又是如何在此次执法行动中突然陨落的？]]></description>
      <content:encoded><![CDATA[<p>近期，美国司法部宣布，在一项由FBI牵头、名为“猎鸭行动”的行动中，来自美国、法国、德国、荷兰、英国、罗马尼亚和拉脱维感染的设备。</p><p>执法部门认为，Qakbot与全球至少 40 起针对公司、医疗保健供应商和政府机构的勒索软件攻击存在关联，造成了数亿美元的损失。FBI局长克里斯]]></content:encoded>
      <pubDate>Thu, 21 Sep 2023 13:51:19 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[FreeBuf 早报 | 美国CISA发布身份和访问管理新指南；澳大利亚必胜客顾客信息泄露]]></title>
      <link>https://www.freebuf.com/news/378745.html</link>
      <description><![CDATA[联邦机构如何将身份和访问管理 (IDAM) 功能集成到其身份、凭证和访问管理 (ICAM) 架构中的新指南。]]></description>
      <content:encoded><![CDATA[<h2 id="h2-1">全球动态</h2><h3 id="h3-1">1.英国人一年因诈骗损失 93 亿美元</h3><p>据一份新报告称，在过去一年中，约有1亿美元）。【外刊-<a href="https://www.infosecurity-magazine.com/news/brits-lose-93bn-to-scam]]></content:encoded>
      <category>资讯</category>
      <pubDate>Thu, 21 Sep 2023 13:39:43 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[Nagios XI 网络监控软件曝出多个安全漏洞]]></title>
      <link>https://www.freebuf.com/news/378735.html</link>
      <description><![CDATA[Nagios XI 公司于 2023 年 9 月 11 日发布了 5.11.2 版，解决了上述漏洞问题。]]></description>
      <content:encoded><![CDATA[<h2 id="h2-1"></h2><p>Security Affairs 网站披露，Outpost 24 的研究人员 Astrid Tedenbrant 在 Nagios XI 网络和 IT 基础漏洞，漏洞分别追踪为 CVE-2023-40931、CVE-2023-40932、CVE-2023-40933、CVE-2023-40934，可能导致信息泄露和权限升级]]></content:encoded>
      <category>资讯</category>
      <pubDate>Thu, 21 Sep 2023 11:21:52 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[T-Mobile 程序故障，用户竟能看到他人账户信息]]></title>
      <link>https://www.freebuf.com/news/378727.html</link>
      <description><![CDATA[有T-Mobile 用户表示，他们在登录该公司的官方移动应用程序后竟然可以看到其他人的账户和账单信息。]]></description>
      <content:encoded><![CDATA[<p>据BleepingComputer消息，9月20日，有T-Mobile 用户表示，他们在登录该公司的官方移动应用程序后竟然可以看到其他人的账户net/images/20230921/1695265532_650bb2fc5a903ca8407e0.png!small" width="690" height="]]></content:encoded>
      <category>资讯</category>
      <pubDate>Thu, 21 Sep 2023 11:04:49 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[重大威胁！P2PInfect僵尸软件活动量激增600倍]]></title>
      <link>https://www.freebuf.com/news/378723.html</link>
      <description><![CDATA[P2PInfect对其蜜罐进行的初始访问尝试次数稳步上升，截至今年8月24日，仅单个传感器的事件数已经达到4064次。]]></description>
      <content:encoded><![CDATA[<p><img src="https://image.3001.net/images/20230921/1695263801_650bac39331045a12f5d5.png!small?1695263800132" alt="1695263801_650bac39331045a12f5d5.png!small?1695263800132" /></p><p>今年8月下旬，P2PInfect 僵]]></content:encoded>
      <category>资讯</category>
      <pubDate>Thu, 21 Sep 2023 10:41:52 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[​数风流人物，还看今“招”！加入「北山安全团队」帮会，唤醒挖洞之魂]]></title>
      <link>https://www.freebuf.com/fevents/378684.html</link>
      <description><![CDATA[漏洞挖掘、代码审计、红蓝对抗，我们是专业的~  紧跟大佬不迷路  加入我们，未来属于我们！]]></description>
      <content:encoded><![CDATA[<p style="text-align:center;">数风流人物，还看今“招”！</p><p style="text-align:center;">发光少年，请留步</p><p style;"><strong>网安知识大陆</strong>发布了一则征集令并@了你！</p><p style="text-align:center;">想成为漏洞赏金猎人吗？]]></content:encoded>
      <category>活动</category>
      <pubDate>Wed, 20 Sep 2023 19:05:51 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[FCIS 2023大会邀请 | 暗号1122，来一场酷炫的网安星际旅行]]></title>
      <link>https://www.freebuf.com/articles/378680.html</link>
      <description><![CDATA[2023 年 11 月 22 日- 23 日  上海张江科学会堂  FCIS 2023网络安全创新大会  全新开启！]]></description>
      <content:encoded><![CDATA[<p style="text-align:center;">Hi~</p><p style="text-align:center;">有多少网安人还记得</p><p style="text-align:center安大会的情形</p><p style="text-align:center;">2016 年，上千位网安人</p><p style="text-align:center]]></content:encoded>
      <pubDate>Wed, 20 Sep 2023 18:59:43 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[FreeBuf 早报 | 美国人不相信 AI 的好处超过其风险；英国议会通过在线安全法案]]></title>
      <link>https://www.freebuf.com/news/378671.html</link>
      <description><![CDATA[英国政府表示，在线安全法案为Facebook、YouTube等社交媒体平台制定了更严格的标准，该法案已获得议会同意，并将很快成为法律。]]></description>
      <content:encoded><![CDATA[<h2 id="h2-1">全球动态</h2><h3 id="h3-1">1. LockBit 要求将受害公司收入的 3% 作为赎金</h3><p>LockBit内部拟要求对受害公赎金标准。</p><p>【外刊-<a href="https://cybersecuritynews.com/lockbit-demands-3-revenue-ran]]></content:encoded>
      <category>资讯</category>
      <pubDate>Wed, 20 Sep 2023 17:53:25 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[卡巴斯基：《2023年H1工业自动化系统威胁形势报告》]]></title>
      <link>https://www.freebuf.com/articles/paper/378664.html</link>
      <description><![CDATA[近日，卡巴斯基实验室通过分析卡巴斯基安全网络（KSN）收集的数据，形成了《2023年H1工业自动化系统威胁形势报告》。]]></description>
      <content:encoded><![CDATA[<p>近日，卡巴斯基实验室通过分析卡巴斯基安全网络（KSN）收集的数据，形成了《2023年H1工业自动化系统威胁形势报告》，为20h2 id="h2-1">全球统计数据</h2><h3 id="h3-1">整体威胁</h3><p>2023年上半年，34%的ICS计算机阻止了恶意对象（所有类型的威胁]]></content:encoded>
      <category>安全报告</category>
      <pubDate>Wed, 20 Sep 2023 17:08:56 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[嚣张！黑客袭击国际刑事法院]]></title>
      <link>https://www.freebuf.com/articles/378626.html</link>
      <description><![CDATA[2002 年 7 月 1 号，国际刑事法院（ICC）根据生效的《罗马国际刑事法院规约》成立。]]></description>
      <content:encoded><![CDATA[<p>Bleeping Computer 网站披露，国际刑事法院（ICC）服务部门上周监测到其信息系统出现异常情况，经过分析研究意识到内部系rc="https://image.3001.net/images/20230920/1695188600_650a86781cf5dbbd0d1e0.png!smal]]></content:encoded>
      <pubDate>Wed, 20 Sep 2023 13:43:46 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[超隐形后门HTTPSnoop 正攻击中东电信公司]]></title>
      <link>https://www.freebuf.com/news/378621.html</link>
      <description><![CDATA[中东的电信服务提供商最近沦为ShroudedSnooper网络威胁组织的目标，并被部署了名为 HTTPSnoop 的隐形后门。]]></description>
      <content:encoded><![CDATA[<p>据The Hacker News消息，Cisco Talos分享的一份报告显示，中东的电信服务提供商最近沦为ShroudedSnooper网络威胁组织的目g src="https://image.3001.net/images/20230920/1695180075_650a652b89f1a571abd43.jpg!s]]></content:encoded>
      <category>资讯</category>
      <pubDate>Wed, 20 Sep 2023 11:20:41 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[起底美国情报机关网攻窃密的主要卑劣手段]]></title>
      <link>https://www.freebuf.com/news/378617.html</link>
      <description><![CDATA[国家安全机关破获的系列美国间谍情报机关网络攻击窃密案件中，“黑客帝国”维护“网络霸权”的卑劣伎俩浮出水面。]]></description>
      <content:encoded><![CDATA[<div id="js_content"><div><div><div><p id="js_a11y_wx_profile_logo">近日，中国国家计算机病毒应急处理中心通报，在处置约会”的间谍软件样本。该软件为美国国家安全局开发的网络“间谍”武器，在遍布全球多国的上千台网络设备中潜藏隐秘运行。</p><p>美国在网络安全领域劣迹斑斑。国家安全机关]]></content:encoded>
      <category>资讯</category>
      <pubDate>Wed, 20 Sep 2023 11:14:43 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[国内超1400万部手机被植入木马]]></title>
      <link>https://www.freebuf.com/news/378608.html</link>
      <description><![CDATA[老年机原本只有接打电话等基础功能  但四川攀枝花的张先生却发现  家中的老年机无缘无故产生了不少小额增值收费业务。]]></description>
      <content:encoded><![CDATA[<p style="text-align:center;">老年机原本只有接打电话等基础功能</p><p style="text-align:center;">但四川攀枝花的张先生n:center;">家中的老年机无缘无故产生了不少小额增值收费业务</p><p style="text-align:center;"><strong>警方调查后</s]]></content:encoded>
      <category>资讯</category>
      <pubDate>Wed, 20 Sep 2023 10:03:00 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[WitAwards 2023中国网络安全行业年度评选正式启动]]></title>
      <link>https://www.freebuf.com/articles/378576.html</link>
      <description><![CDATA[历届 WitAwards 始终坚持发掘优秀的企业、品牌、个人等进行评选与颁奖，树立起行业标杆，发挥榜样的力量。]]></description>
      <content:encoded><![CDATA[<p>2023 年，尽管我国网络安全产业依旧还处于蓄力阶段，但随着数字化转型进入深水区，创新意识成为核心驱动要素，网安产业复业、品牌和个人。</p><p>那么，他们是如何取得如此成绩呢，对于我国网安产业整体发展又有哪些值得借鉴的经验和价值？</p><p>这也正是 WitAwards 2023]]></content:encoded>
      <pubDate>Tue, 19 Sep 2023 19:26:14 +0800</pubDate>
    </item>
    <item>
      <title><![CDATA[FreeBuf 早报 | 香港警方逮捕加密货币网红；研究显示英国人对暗网一无所知]]></title>
      <link>https://www.freebuf.com/news/378561.html</link>
      <description><![CDATA[香港警方9月18日逮捕了当地社交媒体名人林作及其他五人。警方表示，该团伙涉嫌“合谋诈骗”投资者。]]></description>
      <content:encoded><![CDATA[<h2 id="h2-1">全球动态</h2><h3 id="h3-1">1. 报告警告称Metaverse 给用户带来严重的隐私风险</h3><p>纽约大学的一份新报告式，否则被称为虚拟世界的沉浸式互联网体验将侵蚀用户的隐私。【外刊-<a href="https://therecord.media/metaverse-privacy-]]></content:encoded>
      <category>资讯</category>
      <pubDate>Tue, 19 Sep 2023 17:19:39 +0800</pubDate>
    </item>
  </channel>
</rss>
`
*/
