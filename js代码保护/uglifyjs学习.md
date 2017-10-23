UglifyJS
============

AST生成 语法分析
------------

对于JS来说，一个文件就是处于一个全局域的语句块，uglify里边把处于全局域的语句快称为 _toplevel_ 的语句块，里边涉及各种状态。下面是一个语句块的状态转移的图片：

![语句块状态转移](/home/baberam/图片/a.jpg  "语句块状态转移")

parse函数状态保存变量S：

```javascript
 var S = {
// 输入：可以自己构造一个token输入器传递进来 否则就用tokenizer返回的next_token作为输入器
input         : (typeof $TEXT == "string"
? tokenizer($TEXT, options.filename,
options.html5_comments, options.shebang)
: $TEXT),
// 当前token
token         : null,
// 上一个token
prev          : null,
// 向前看一个token
peeked        : null,
// 是不是在一个function里面
in_function   : 0,
// 在函数里边标记"use strict"这种指示性字符串
in_directives : true,
// 是否在循环里面
in_loop       : 0,
// labels集合
labels        : []
};
```

AST结点类型描述
------------

```javascript
AST_SymbolFunarg://函数参数
AST_SymbolRef：//引用

```

token扫描 词法分析
------------

_词法分析器(tokenizer)_ 状态保存变量S：

``` javascript
var S = {
text            : $TEXT,
filename        : filename,
pos             : 0,
tokpos          : 0,
line            : 1,
tokline         : 0,
col             : 0,
tokcol          : 0,
newline_before  : false,
// 在某些符号或者关键字后边才能出现正则表达式！
// 需要有标志位标记是否读取正则的token
regex_allowed   : false,
comments_before : [],
directives      : {},
directive_stack : []
};
```

_词法分析器(tokenizer)_ 中token保存的信息：

``` javascript
// 一个token带有如下信息
var ret = {
type    : type,         // token类型
value   : value,        // token对应的值

// token对应的源码位置信息
line    : S.tokline,
col     : S.tokcol,
pos     : S.tokpos,
endline : S.line,
endcol  : S.col,
endpos  : S.pos,
nlb     : S.newline_before,
file    : filename
};
```
