# IntelliJ-LuaInsight

[English](README.md) | 简体中文

Lua Insight 是面向 IntelliJ IDEA 和 Android Studio 的 Lua 插件，将
[Luanalysis](https://github.com/Benjamin-Dobell/IntelliJ-Luanalysis) 的高级类型分析
迁入持续维护的 [EmmyLua](https://github.com/EmmyLua/IntelliJ-EmmyLua)，保留 EmmyLua
的编辑器功能和调试器集成。本项目为独立 fork，由
[ClarkWain](https://github.com/ClarkWain) 维护。

## 安装

1. 在 [Releases](https://github.com/ClarkWain/IntelliJ-LuaInsight/releases) 下载与 IDE 版本对应的安装包。
2. 禁用 EmmyLua、Luanalysis 等其他 Lua 语言插件，避免多个插件同时处理 Lua 文件。
3. 打开 **Settings → Plugins → 齿轮菜单 → Install Plugin from Disk**，选择 ZIP 并重启 IDE。无需手动解压。

| 发布包 | 支持的 IntelliJ 平台范围 |
| --- | --- |
| `IntelliJ-LuaInsight-1.5.6-IDEA2025.zip` | IDEA 2025.1–2025.3（251–253） |
| `IntelliJ-LuaInsight-1.5.6-IDEA2026.zip` | IDEA 2026.1–2026.2（261–262） |

Android Studio 按 **Help → About** 中的 IntelliJ 平台 Build 选择安装包：
**251–253** 对应 **2025 包**，**261–262** 对应 **2026 包**。
具体测试版本、校验值和实际运行截图见 [验证记录](docs/validation.md)。

## 特性

- `@shape` 结构类型、索引字段、类型化字典与元组。
- 泛型类、泛型约束、泛型别名和递归别名。
- 表达式及返回值的 `@type` 转换，以及 `@not` 联合类型排除。
- `and/or` 短路表达式、条件分支和非空赋值后的自动判空，详见
  [类型收窄说明](docs/flow-types.zh-CN.md)。
- 可调用类、重载、可选参数和可变返回值。
- 赋值、参数、返回值、继承和未声明成员的类型检查。
- 兼容 EmmyLua 的 `int`/`integer`、`float`、`lightuserdata` 和基础类型／表类型继承，
  支持从泛型字典父类推断字段和索引类型。
- Lua 5.1–5.4 标准库定义，以及补全、导航、重命名、格式化、文档和原有调试器集成。

类型安全检查默认启用，类型不匹配默认显示为错误。可在 IDE 的 Inspections 和 Lua
设置中调整严重程度、严格 nil 检查和未知类型处理。

[迁移清单](docs/luanalysis-migration.md) 记录了来源版本和特性回归覆盖。
[验证项目](examples/luanalysis-validation) 包含正确的泛型、结构类型示例，以及用于验证
诊断的故意错误示例。

## 构建与验证

使用 JDK 21 运行 Gradle。2025 包使用 IDEA 2025.1 SDK 和 JDK 21 编译；2026 包使用
IDEA 2026.1 SDK 编译，需要 JDK 25 工具链。若 Gradle 未找到 JDK 25，将
`JAVA_HOME_25_X64` 设置为其安装路径。

```sh
./gradlew buildPlugin -DIDEA_VER=2025 --no-configuration-cache
./gradlew buildPlugin -DIDEA_VER=2026 --no-configuration-cache
```

需要回归检查时，可使用相同目标 SDK 单独执行 `./gradlew test`。

输出目录为 `build/distributions/`。可加 `-PlocalIdePath=/path/to/ide` 使用本地 IDE SDK，
或用 `-PverificationIdePaths="/path/to/ide1;/path/to/ide2"` 指定多个 IDE 并执行
`verifyPlugin`。发布包应使用声明范围中最早的 IDE 编译。

Windows 下可用以下脚本复验，脚本会处理中文路径导致的 Java 参数文件编码问题：

```powershell
.\scripts\validate-local.ps1 -IdePath 'C:\Program Files\Android\Android_Studio_2025'
```

## 致谢与许可

基于 [tangzx](https://github.com/tangzx) 及贡献者维护的 EmmyLua，以及
[Benjamin Dobell](https://github.com/Benjamin-Dobell) 及贡献者维护的 Luanalysis。
保留原有版权声明；两个上游项目及本 fork 均采用 [Apache License 2.0](LICENSE.txt)。

[EmmyLua 中文文档](https://emmylua.github.io/zh_CN/) ·
[向本 fork 提交问题](https://github.com/ClarkWain/IntelliJ-LuaInsight/issues)
