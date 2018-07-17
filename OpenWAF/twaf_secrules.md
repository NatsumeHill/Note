# 安全规则配置

在twaf_policy_conf.json和twaf_default_conf.json中的"twaf_secrules"对安全过滤规则进行配置.
模块```twaf_secrules```实现对过滤规则的解析。从下面的源码可以看出，var变量可以是预定义的函数，也可是是自己在规则中实现的函数，而且，（match， value）还可以从function字段中指定：

```lua

if type(v.var) == "function" then
    match, value = v.var(_twaf, rule, ctx)

elseif v["function"] then
    local modules_name = v.var:lower()
    local func         = v["function"]
    match, value = _twaf.modfactory[modules_name][func](nil, _twaf)

-- 如果是request模块中定义的字段
elseif type(request[v.var]) == "function" then
    data = request[v.var](_twaf)

else
    if not v.storage then
        data = _parse_var(_twaf, sctx.gcf, request[v.var], v.parse)
    else
        data = _parse_var(_twaf, sctx.gcf, sctx.storage[v.var], v.parse)
    end
end
```