#!/usr/bin/env python3
"""把 docs/_src/*.html 片段（16 章 + 4 个附录 + 首页）包成完整页面，并额外拼一个 all-in-one 单页版（用于发布 Artifact）。"""
import os, re, sys

SRC = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.dirname(SRC)

CHAPTERS = [
    ("ch01", "真实世界中的 Spring",        "为什么用框架、Spring 生态全景、什么时候不该用"),
    ("ch02", "Spring 上下文：定义 bean",   "@Bean、构造型注解、编程式注册"),
    ("ch03", "Spring 上下文：装配 bean",   "方法调用、参数注入、@Autowired、循环依赖、@Qualifier"),
    ("ch04", "Spring 上下文：使用抽象",     "面向接口解耦、选谁进上下文、@Service/@Repository"),
    ("ch05", "Bean 作用域与生命周期",       "单例、原型、饥饿与懒加载、并发陷阱"),
    ("ch06", "用 Spring AOP 实现切面",     "代理机制、@Around、切点表达式、切面链与 @Order"),
    ("ch07", "理解 Spring Boot 与 Spring MVC", "Servlet 容器、starter、自动配置、第一个页面"),
    ("ch08", "用 Boot + MVC 实现 Web 应用", "Thymeleaf 动态视图、请求参数、路径变量、GET/POST"),
    ("ch09", "使用 Spring Web 作用域",     "request / session / application 三种 web 作用域"),
    ("ch10", "实现 REST 服务",             "@RestController、ResponseEntity、异常处理、OpenAPI"),
    ("ch11", "消费 REST 端点",             "RestClient、OpenFeign、RestTemplate、WebClient"),
    ("ch12", "在 Spring 应用中使用数据源",   "DataSource、连接池、JdbcTemplate、自定义配置"),
    ("ch13", "在 Spring 应用中使用事务",     "@Transactional、代理与自调用、回滚规则"),
    ("ch14", "用 Spring Data 实现持久化",   "Repository 接口、派生查询、@Query、Spring Data JDBC"),
    ("ch15", "单元测试",                   "被测单元与依赖替身、Mockito、@Mock/@InjectMocks"),
    ("ch16", "Spring 集成测试",            "@SpringBootTest、MockMvc、@MockitoBean"),
]

APPENDICES = [
    ("appa", "HTTP 与 JSON 入门",       "请求与响应、状态码、请求头、JSON 语法、curl"),
    ("appb", "架构风格与 XML 配置",      "单体、SOA、微服务、无服务器；读懂老项目的 XML"),
    ("appc", "速查：注解、报错、术语",    "注解速查、常见报错速查、术语表"),
    ("appd", "环境、工具与进阶路线",      "MySQL、推荐工具、学完之后学什么、延伸阅读"),
]

PAGES = CHAPTERS + APPENDICES          # 翻页顺序：第 16 章之后接附录
LINK = r'((?:ch\d\d|app[a-d]))'      # 站内页面链接的文件名部分


def label(cid):
    """ch03 → '3'，appa → '附录 A'。"""
    return str(int(cid[2:])) if cid.startswith("ch") else "附录 " + cid[3:].upper()

FONTS = ('<link rel="preconnect" href="https://fonts.googleapis.com">'
         '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
         '<link rel="stylesheet" href="https://fonts.googleapis.com/css2?'
         'family=Newsreader:ital,opsz,wght@0,6..72,400..700;1,6..72,400&'
         'family=Noto+Sans+SC:wght@400;500;700&'
         'family=Noto+Serif+SC:wght@600;700&'
         'family=JetBrains+Mono:wght@400;500;700&display=swap">')

HLJS = """<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/java.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/xml.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/sql.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/properties.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/yaml.min.js"></script>
<script>
(function(){ if(!window.hljs) return;
  document.querySelectorAll('pre code[class^="language-"]').forEach(function(el){
    try{ hljs.highlightElement(el); }catch(e){}
  });
})();
</script>"""

css = open(os.path.join(SRC, "shell.css"), encoding="utf-8").read()


def rail(active):
    """左侧章节导航。active 为 'index' 或 chXX。"""
    rows = ['<a class="rail-brand" href="index.html"><b>Spring Start Here<br>学习手册</b>'
            '<span>Spring Boot 4 · Spring 7 · Java 25</span></a>']
    rows.append('<div class="rail-group">第一部分 · 基础</div><nav>')
    for i, (cid, title, _) in enumerate(CHAPTERS):
        if i == 6:
            rows.append('</nav><div class="rail-group">第二部分 · 实现</div><nav>')
        cur = ' aria-current="page"' if cid == active else ''
        rows.append(f'<a class="ch" href="{cid}.html"{cur}><i>{int(cid[2:]):02d}</i><span>{title}</span></a>')
    rows.append('</nav><div class="rail-group">附录</div><nav>')
    for cid, title, _ in APPENDICES:
        cur = ' aria-current="page"' if cid == active else ''
        rows.append(f'<a class="ch" href="{cid}.html"{cur}><i>{cid[3:].upper()}</i><span>{title}</span></a>')
    rows.append('</nav><div class="rail-group">总览</div><nav>')
    cur = ' aria-current="page"' if active == 'index' else ''
    rows.append(f'<a class="ch" href="index.html"{cur}><i>◆</i><span>目录与版本对照</span></a>')
    rows.append('</nav>')
    return '<aside class="rail">' + "".join(rows) + '</aside>'


def pager(idx):
    parts = []
    if idx > 0:
        c = PAGES[idx - 1]
        parts.append(f'<a class="prev" href="{c[0]}.html"><span>上一篇</span>{label(c[0])}. {c[1]}</a>')
    if 0 <= idx < len(PAGES) - 1:
        c = PAGES[idx + 1]
        parts.append(f'<a class="next" href="{c[0]}.html"><span>下一篇</span>{label(c[0])}. {c[1]}</a>')
    return '<div class="pager">' + "".join(parts) + '</div>' if parts else ''


def page(title, body, active, idx=None):
    nav = pager(idx) if idx is not None else ''
    return (f'<!doctype html>\n<html lang="zh-CN">\n<head>\n<meta charset="utf-8">\n'
            f'<meta name="viewport" content="width=device-width, initial-scale=1">\n'
            f'<title>{title}</title>\n{FONTS}\n<style>\n{css}\n</style>\n</head>\n<body>\n'
            f'<div class="wrap">{rail(active)}<main class="main">\n{body}\n{nav}\n</main></div>\n{HLJS}\n</body>\n</html>\n')


def main():
    frags = {}
    for cid, title, _ in PAGES + [("index", "目录", "")]:
        p = os.path.join(SRC, cid + ".html")
        frags[cid] = open(p, encoding="utf-8").read() if os.path.exists(p) else None

    n = 0
    for i, (cid, title, _) in enumerate(PAGES):
        if frags[cid] is None:
            print("  跳过（缺片段）:", cid); continue
        out = os.path.join(OUT, cid + ".html")
        head = f"第 {label(cid)} 章" if cid.startswith("ch") else label(cid)
        open(out, "w", encoding="utf-8").write(
            page(f"{head} {title} · Spring Start Here 学习手册", frags[cid], cid, i))
        n += 1
    if frags["index"] is not None:
        open(os.path.join(OUT, "index.html"), "w", encoding="utf-8").write(
            page("Spring Start Here 学习手册", frags["index"], "index"))
        n += 1

    # ── all-in-one 单页版（Artifact 用）：无 doctype/head/body，站内链接改成锚点 ──
    body = [frags["index"] or ""]
    for i, (cid, title, _) in enumerate(PAGES):
        if frags[cid] is None: continue
        body.append(f'<section id="{cid}">' + frags[cid] + '</section>')
    one = "\n".join(body)
    one = re.sub(r'href="' + LINK + r'\.html"', r'href="#\1"', one)
    one = re.sub(r'href="index\.html"', r'href="#top"', one)
    railone = re.sub(r'href="' + LINK + r'\.html"', r'href="#\1"', rail("index"))
    railone = re.sub(r'href="index\.html"', r'href="#top"', railone)
    railone = railone.replace(' aria-current="page"', '')
    one = '<div id="top"></div>' + one
    open(os.path.join(OUT, "all-in-one.html"), "w", encoding="utf-8").write(
        '<title>Spring Start Here 学习手册</title>\n' + FONTS + '\n<style>\n' + css +
        '\n.main section{scroll-margin-top:20px}\n.main section+section{margin-top:80px}\n</style>\n'
        '<div class="wrap">' + railone + '<main class="main">\n' + one + '\n</main></div>\n' + HLJS + '\n')
    print(f"已生成 {n} 个页面 + all-in-one.html → {OUT}")


if __name__ == "__main__":
    main()
