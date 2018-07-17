# OpenWAF System Rules Test

## 自定义测试用例测试

| ID | Test Result | TestCase |
| :--- | :--- | :--- |
id_300003 | pass | {'uri': ['root%25.exe'], 'headers': None, 'cookies': None}
id_300004 | pass | {'uri': ['x.gif', 'x.docx', '.png', '.bmp', '.jpg ... ..  ..'], 'headers': None, 'cookies': None}
id_300005 | pass | {'uri': ['.gif', '.jpeg', '.png', '.bmp', '.jpg'], 'headers': None, 'cookies': None}
id_300006 | pass | {'uri': ['com1.asmx'], 'headers': None, 'cookies': None}
id_700001 | pass | {'uri': [''], 'headers': [{'User-Agent': 'emailcollect'}, {'User-Agent': 'emailharvest'}], 'cookies': None}
id_700002 | pass | {'uri': [''], 'headers': [{'User-Agent': 'microsoft url control'}, {'User-Agent': 'mozilla/2.0 (compatible; newt activex; win32)'}, {'User-Agent': 'w3mirror'}], 'cookies': None}
id_700003 | pass | {'uri': [''], 'headers': [{'User-Agent': 'Mozilla/5.0 (compatible; en-US; Gnomit) Gnomit/'}, {'User-Agent': 'goso_crawler/'}, {'User-Agent': 'Dom2Dom/'}], 'cookies': None}
id_700004 | pass | {'uri': [''], 'headers': [{'Accept': '*/*', 'Accept-Language': 'en-US', 'User-Agent': 'Mozilla/4%.0 %(compatible; MSIE 6%.0; Win32%)'}], 'cookies': None}
id_700006 | pass | {'uri': [''], 'headers': [{'User-Agent': 'advanced email extractor'}, {'User-Agent': 'absinthe'}, {'User-Agent': 'arachni'}, {'User-Agent': 'autogetcontent'}, {'User-Agent': 'bilbo'}],'cookies': None}
id_700007 | pass | {'uri': [''], 'headers': [{'User-Agent': 'Mozilla/4.4'}, {'User-Agent': 'MSIE 4.0'}, {'User-Agent': 'TencentTraveler/4.0'}, {'User-Agent': 'Internet Explorer/5.0'}], 'cookies': None}
id_700008 | pass | {'uri': ['nessustest', 'appscan_fingerprint'], 'headers': None, 'cookies': None}
id_700009 | pass | {'uri': [''], 'headers': [{'Accept': '__acunetix__'}, {'Acunetix': 'test'}], 'cookies': None}
id_700010 | pass | {'uri': [''], 'headers': [{'User-Agent': 'whitehat.ro'}, {'User-Agent': 'scanner'}], 'cookies': None}
id_700011 | pass | {'uri': [''], 'headers': [{'User-Agent': '__larbin__'}], 'cookies': None}
id_700012 | pass | {'uri': ['users/query?name=__acunetix_wvs_security_test__'], 'headers': None, 'cookies': None}
id_700014 | fail | [{'path': 'http://firewall.com/', 'code': 200, 'headers': {'Accept-Language': 'en%-US', 'Accept': '_text/html,application/xhtml\\%+xml,application/xml;q=0.9,%*/%*;q=0%.8', 'User-Agent': 'Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/7.0)'}}]
id_700015 | pass | {'uri': [''], 'headers': [{'Accept-Language': 'zh%-CN', 'Accept': 'image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*', 'User-Agent': 'Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/7.0)'}], 'cookies': None}
id_700017 | pass | {'uri': [''], 'headers': [{'myvar': '1234', 'x-ratproxy-loop': '_test_'}], 'cookies': None}
id_700018 | pass | {'uri': ['.adsensepostnottherenonobook', '<invalid>hello.html', 'nessus_is_probing_you_', 'appscan_fingerprint'], 'headers': None, 'cookies': None}
id_100022 | pass | {'uri': [''], 'headers': [{'User-Agent': 'microsoft url'}, {'User-Agent': 'missigua'}, {'User-Agent': 'emailcollector'}], 'cookies': None}
id_100023 | fail | [{'path': "http://firewall.com/users/query?@http:[url!<a='test'", 'code': 200}]
id_100024 | fail | [{'path': 'http://firewall.com/users/query?http:/1http:/1http:/1http:/1http:/1http:/1=2e', 'code': 200}]
id_100025 | pass | {'uri': ['..%5c../Windows/System32/cmd.exe?/c+dir+c:\\'], 'headers': None, 'cookies': None}
id_100026 | pass | {'uri': ['users/query?name=~/../boot', 'query?name=../../../../boot.ini%00.jpg'], 'headers': None, 'cookies': None}
id_100087 | pass | {'uri': ['users/query?cfinternaldebug=12', 'users/querty?cfadmin_registry_delete=test'], 'headers': None, 'cookies': [{'cfnewinternalregistry': 'test'}, {'cfusion_settings_refresh': 'test'}]}
id_100088 | pass | {'uri': ['users/query?cfusion_dbconnections_flush=12'], 'headers': [{'cfusion_decrypt': 'test'}, {'cfusion_settings_refresh': 'test'}, {'cfusion_getodbcdsn': 'test'}], 'cookies': None}
id_100205 | pass | {'uri': ["users/query?(|attribute=value)(second_filter)) or (&(attribute=value)(second_filter))='test'", "users/query?(&(parameter1=value1) (parameter2=value)) = 'test'"], 'headers': None, 'cookies': [{'(&(USER=Uname)(PASSWORD=Pwd))': 'test'}, {'test': '(&(USER=slisberger)(&)(PASSWORD=Pwd))'}, {'test': '(&(objectClass=*)(objectClass=*))(&(objectClass=void)(type=Epson*))'}]}
id_100206 | pass | {'uri': ['(&(idprinter=HPLaserJet2100)(department=*b*))(object=printer))', '(!(parameter1=value1) (parameter2=value2))'], 'headers': [{'test': '(&(objectClass=*)(objectClass=*))(&(objectClass=void)(type=Epson*))'}, {'test': '(&(attribute=value)(injected_filter)) (second_filter)'}], 'cookies': None}
id_100207 | fail | [{'path': "http://firewall.com/users/query?;@!cc='test'", 'code': 200}, {'path': 'http://firewall.com/', 'code': 200, 'cookies': {'test': ';@!cc'}}]
id_200001 | pass | {'uri': ['users/query?name=-15 /*!UNION*/ /*!SELECT*/ 1,2,3,4….', 'users/query?name=null%0A/**//*!50000%55nIOn*//*yoyu*/all/**/%0A/*!%53eLEct*/%0A/*nnaa*/+1,2,3,4….'], 'headers': None,'cookies': None}
id_200002 | pass | {'uri': ['users/query?name=\<script>alert(1);\</stript>'], 'headers': None, 'cookies': None}

## sqli自动化工具测试

```shell
        ___
       __H__
 ___ ___[)]_____ ___ ___  {1.2.4#stable}
|_ -| . [(]     | .'| . |
|___|_  [']_|_|_|__,|  _|
      |_|V          |_|   http://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting at 15:50:47

[15:50:48] [INFO] testing connection to the target URL
[15:50:48] [WARNING] the web server responded with an HTTP error code (403) which could interfere with the results of the tests
[15:50:48] [INFO] testing if the target URL content is stable
[15:50:49] [WARNING] target URL content is not stable. sqlmap will base the page comparison on a sequence matcher. If no dynamic nor injectable parameters are detected, or in case of junk results, refer to user's manual paragraph 'Page comparison'

[15:51:28] [INFO] testing if GET parameter 'name' is dynamic
[15:51:28] [INFO] confirming that GET parameter 'name' is dynamic
[15:51:28] [INFO] GET parameter 'name' is dynamic
[15:51:28] [WARNING] heuristic (basic) test shows that GET parameter 'name' might not be injectable
[15:51:28] [INFO] testing for SQL injection on GET parameter 'name'
[15:51:28] [INFO] testing 'AND boolean-based blind - WHERE or HAVING clause'
[15:51:28] [INFO] testing 'MySQL >= 5.0 boolean-based blind - Parameter replace'
[15:51:28] [INFO] testing 'MySQL >= 5.0 AND error-based - WHERE, HAVING, ORDER BY or GROUP BY clause (FLOOR)'
[15:51:28] [INFO] testing 'PostgreSQL AND error-based - WHERE or HAVING clause'
[15:51:28] [INFO] testing 'Microsoft SQL Server/Sybase AND error-based - WHERE or HAVING clause (IN)'
[15:51:28] [INFO] testing 'Oracle AND error-based - WHERE or HAVING clause (XMLType)'
[15:51:28] [INFO] testing 'MySQL >= 5.0 error-based - Parameter replace (FLOOR)'
[15:51:28] [INFO] testing 'MySQL inline queries'
[15:51:28] [INFO] testing 'PostgreSQL inline queries'
[15:51:28] [INFO] testing 'Microsoft SQL Server/Sybase inline queries'
[15:51:28] [INFO] testing 'PostgreSQL > 8.1 stacked queries (comment)'
[15:51:28] [INFO] testing 'Microsoft SQL Server/Sybase stacked queries (comment)'
[15:51:28] [INFO] testing 'Oracle stacked queries (DBMS_PIPE.RECEIVE_MESSAGE - comment)'
[15:51:28] [INFO] testing 'MySQL >= 5.0.12 AND time-based blind'
[15:51:28] [INFO] testing 'PostgreSQL > 8.1 AND time-based blind'
[15:51:29] [INFO] testing 'Microsoft SQL Server/Sybase time-based blind (IF)'
[15:51:29] [INFO] testing 'Oracle AND time-based blind'
[15:51:29] [INFO] testing 'Generic UNION query (NULL) - 1 to 10 columns'
[15:51:29] [WARNING] GET parameter 'name' does not seem to be injectable
[15:51:29] [CRITICAL] all tested parameters do not appear to be injectable. Try to increase values for '--level'/'--risk' options if you wish to perform more tests. Please retry with the switch '--text-only' (along with --technique=BU) as this case looks like a perfect candidate (low textual content along with inability of comparison engine to detect at least one dynamic parameter). If you suspect that there is some kind of protection mechanism involved (e.g. WAF) maybe you could try to use option '--tamper' (e.g. '--tamper=space2comment')
[15:51:29] [WARNING] HTTP error codes detected during run:
403 (Forbidden) - 153 times

[*] shutting down at 15:51:29
```