# 系统默认规则测试

## sqli测试

使用sqlmap攻击进行渗透测试，测试结果表示OpenWAF能够完全防御住sqli。

```shell
# fangkui@fangkui-linux: ~/WorkSpace/attack_script                                                                                                                                             (16:23:42)
(python27) ζ sh webtest.sh
        ___
       __H__
 ___ ___[']_____ ___ ___  {1.2.4#stable}
|_ -| . [']     | .'| . |
|___|_  [(]_|_|_|__,|  _|
      |_|V          |_|   http://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting at 16:23:52

[16:23:52] [INFO] testing connection to the target URL
[16:23:52] [WARNING] the web server responded with an HTTP error code (403) which could interfere with the results of the tests
[16:23:52] [INFO] testing if the target URL content is stable
[16:23:53] [WARNING] target URL content is not stable. sqlmap will base the page comparison on a sequence matcher. If no dynamic nor injectable parameters are detected, or in case of junk results, referto user's manual paragraph 'Page comparison'
how do you want to proceed? [(C)ontinue/(s)tring/(r)egex/(q)uit] C
[16:23:57] [INFO] testing if GET parameter 'id' is dynamic
[16:23:57] [WARNING] GET parameter 'id' does not appear to be dynamic
[16:23:57] [WARNING] heuristic (basic) test shows that GET parameter 'id' might not be injectable
[16:23:57] [INFO] testing for SQL injection on GET parameter 'id'
[16:23:57] [INFO] testing 'AND boolean-based blind - WHERE or HAVING clause'
[16:23:57] [INFO] testing 'MySQL >= 5.0 boolean-based blind - Parameter replace'
[16:23:57] [INFO] testing 'MySQL >= 5.0 AND error-based - WHERE, HAVING, ORDER BY or GROUP BY clause (FLOOR)'
[16:23:57] [INFO] testing 'PostgreSQL AND error-based - WHERE or HAVING clause'
[16:23:57] [INFO] testing 'Microsoft SQL Server/Sybase AND error-based - WHERE or HAVING clause (IN)'
[16:23:57] [INFO] testing 'Oracle AND error-based - WHERE or HAVING clause (XMLType)'
[16:23:57] [INFO] testing 'MySQL >= 5.0 error-based - Parameter replace (FLOOR)'
[16:23:57] [INFO] testing 'MySQL inline queries'
[16:23:57] [INFO] testing 'PostgreSQL inline queries'
[16:23:58] [INFO] testing 'Microsoft SQL Server/Sybase inline queries'
[16:23:58] [INFO] testing 'PostgreSQL > 8.1 stacked queries (comment)'
[16:23:58] [INFO] testing 'Microsoft SQL Server/Sybase stacked queries (comment)'
[16:23:58] [INFO] testing 'Oracle stacked queries (DBMS_PIPE.RECEIVE_MESSAGE - comment)'
[16:23:58] [INFO] testing 'MySQL >= 5.0.12 AND time-based blind'
[16:23:58] [INFO] testing 'PostgreSQL > 8.1 AND time-based blind'
[16:23:58] [INFO] testing 'Microsoft SQL Server/Sybase time-based blind (IF)'
[16:23:58] [INFO] testing 'Oracle AND time-based blind'
[16:23:58] [INFO] testing 'Generic UNION query (NULL) - 1 to 10 columns'
[16:23:58] [WARNING] GET parameter 'id' does not seem to be injectable
[16:23:58] [CRITICAL] all tested parameters do not appear to be injectable. Try to increase values for '--level'/'--risk' options if you wish to perform more tests. Please retry with the switch '--text-only' (along with --technique=BU) as this case looks like a perfect candidate (low textual content along with inability of comparison engine to detect at least one dynamic parameter). If you suspect thatthere is some kind of protection mechanism involved (e.g. WAF) maybe you could try to use option '--tamper' (e.g. '--tamper=space2comment')
[16:23:58] [WARNING] HTTP error codes detected during run:
403 (Forbidden) - 152 times

[*] shutting down at 16:23:58
```

## xss测试

