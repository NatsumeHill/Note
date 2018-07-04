# OpenWAF 系统规则

| ID | Name | Phase | Severity | Description | Action |
| :--- | :--- | :--- | :--- | :--- | :--- | 
300002 | malicious.trojan.general |access | high | 检测访问木马页面，检查请求头名称中含有关键字的HTTP请求。请求头key值是否以字符串"x_key"或者"x_file"结束，忽略大小写。 | deny 
300003 | malicious.trojan.general.a |access | high | 检测木马访问，检查请求文件名中是否含有关键字。relative request URL（相对请求路径）中包含关键字"rooot%.exe"，忽略大小写。 | deny
300004 | malicious.webshell | access | critical | 检测向常见静态资源文件发出的HTTP POST请求。正则匹配请求url中是否以常见静态资源文件的扩展名作为结束（gif, jpe?g, png, bmp, js, css, txt, exe, docx?, xlsx?, pptx?, zip, rar, 7z）忽略大小写。 | deny
300005 | malicious.webshell.a | header_filter | critical | 检测向常用图片格式文件请求但返回text类型的请求(gif, jpe?g, png, bmp) 忽略大小写 | deny
300006 | malicious.webshell.b | access | critical | 检测对设备文件名的请求(?i)^(?:aux\|con\|nul\|prn\|com\\d\|lpt\\d).*\\.(?:asa\|cer\|aspx?\|asax\|ascx\|ashx\|asmx) | deny
700001 | auto.crawler.general | access | medium | 检测网站爬取工具，检查User-Agent请求头的值是否含有关键字，忽略大小写 | deny
700002 | auto.crawler.general.a | access | medium | 检测网站爬取工具，检查User-Agent请求头的值是否含有关键字 | deny
700003 | auto.crawler.general.b | access | medium | 检测网站爬取工具，检查User-Agent请求头的值是否含有关键字 (Mozilla/5\\.0 \\(compatible; en-US; Gnomit\\) Gnomit/ \| goso_crawler/ \| Dom2Dom/)| deny
700004 | auto.scanner.appscan | access | medium | 检测网站扫描工具，检查请求头的值是否含有关键字 | deny
700006 | auto.scanner.general.a | access | medium | 检测网站扫描工具，检查User-Agent的值含有关键字的HTTP请求 | deny
700007 | auto.scanner.general.b | access | medium | 检测网站扫描工具，检查User-Agent的值含有关键字的HTTP请求 | deny
700008 | auto.scanner.nessus | access | medium | 检测网站扫描工具，检查User-Agent的值含有关键字的HTTP请求 | deny
700009 | auto.scanner.wvs | access | high | 检测网站扫描工具，检查请求头的值含有关键字的HTTP请求 | deny
700010 | auto.scanner.general.c | access | medium | 检测网站扫描工具，检查User-Agent的值含有关键字的HTTP请求 | deny
700011 | auto.crawler.general.c | access | medium | 检测网站扫描工具，检查User-Agent的值含有关键字的HTTP请求 | deny
700012 | auto.scanner.wvs.a | access | medium | 检测网站扫描工具，检查请求头的值含有关键字的HTTP请求 | deny
700014 | auto.scanner.appscan.a | access | medium | 检测网站扫描工具，检查请求头的值是否含有关键字 | deny
700015 | auto.scanner.appscan.b | access | medium | 检测网站扫描工具，检查请求头的值是否含有关键字 | deny
700017 | auto.scanner.general.e | access | medium | 检测网站扫描工具，检查请求头及请求头的值 | deny
700018 | auto.scanner.general.f | access | medium | 检测网站扫描工具，检查请求URI | deny
100022 | attack.commentSpam | access | medium | 检测垃圾信息，检查User-Agent请求头的值为常见垃圾广告发送者的HTTP请求 | deny
100023 | attack.commentSpam.a | access | medium | 检测垃圾信息，检查参数中以http:开头的HTTP请求 | pass
100024 | attack.commentSpam.b | access | medium | 检测垃圾信息，检查参数中含有4个及以上http:/的HTTP请求 | pass
100025 | attack.dirTraversal | access | high | 检测路径遍历攻击，检查请求URI、请求头、请求体 | deny
100026 | attack.dirTraversal.a | access | high | 检测路径遍历攻击，检查请求文件名、参数 | deny
100087 | attack.injection.cf | access | critical | 检测针对ColdFusion服务器的注入攻击，检查Cookie、参数 | deny
100088 | attack.injection.cf.a | access | critical | 检测针对ColdFusion服务器的注入攻击，检查请求URI、请求头、请求体 | deny
100205 | attack.injection.ldap | access | critical | 检测LDAP注入攻击，检查Cookie、参数 | deny
100206 | attack.injection.ldap.a | access | critical | 检测LDAP注入攻击，检查请求URI、请求头、请求体 | deny
100207 | attack.injection.osCmd | access | critical | 检测命令注入攻击，检查Cookie、参数 | pass
200001 | attack.injection.sql.libinjection | access | critical | sqli防护 | deny
200002 | attack.xss.libinjection | access | high | xss防护 | deny