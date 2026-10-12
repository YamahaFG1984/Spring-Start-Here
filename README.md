# Spring Start Here：示例代码与中文精读手册

Laurenţiu Spilcă《Spring Start Here》的随书示例代码，已升级到 **Spring Framework 7 / Spring Boot 4 / Java 25**，并附一份按本仓库实际代码重讲的中文学习手册。

## 学习手册

手册在 [`docs/`](docs/) 目录下，是一组可以直接用浏览器打开的静态页面，从 [`docs/index.html`](docs/index.html) 开始读。

| 部分 | 内容 |
|---|---|
| 第 1–6 章 | Spring 上下文、依赖注入、作用域、AOP |
| 第 7–14 章 | Spring Boot、Spring MVC、REST、数据源、事务、Spring Data |
| 第 15–16 章 | 单元测试、Spring 集成测试 |
| 附录 A | HTTP 与 JSON 入门 |
| 附录 B | 架构风格与 XML 配置 |
| 附录 C | 速查：注解、常见报错、术语 |
| 附录 D | 环境、工具与进阶路线 |

书出版于 2021 年（Spring 5.2 / Boot 2.3 / Java 11）。代码与书对不上的地方，手册以代码为准，并逐章标出差异和原因。

想改手册内容：编辑 `docs/_src/` 下的片段，然后运行 `python3 docs/_src/build.py` 重新生成 `docs/*.html`。不要直接改 `docs/` 根目录下的 html，它们是生成物。

## 示例工程

每个 `ssh_chN_exM` 目录是一个独立的 Maven 工程，对应书中第 N 章的第 M 个示例。各工程演示什么，见手册首页的工程速查表。

| 工程 | 类型 | 怎么运行 |
|---|---|---|
| `ssh_ch2_*` – `ssh_ch6_*` | 纯 Spring（只有 `spring-context`） | 在 IDE 里运行 `Main`，或用本机的 `mvn`（这些工程没有 `mvnw`） |
| `ssh_ch7_*` – `ssh_ch14_*` | Spring Boot | `cd ssh_ch7_ex1 && ./mvnw spring-boot:run` |

运行测试：在工程目录下执行 `./mvnw test`（纯 Spring 工程用 `mvn test`）。

## 环境要求

- **JDK 25**。第 2–6 章的 `Main` 用了 `static void main()` 这种写法，低版本 JDK 无法运行。
- **Maven 3.9+**。
- 数据库：第 12–14 章默认用 H2 内存库，无需安装。只有 `ssh_ch12_ex2` 和 `ssh_ch12_ex3` 需要本地 MySQL，准备方法见手册附录 D。
