# IntelliJ-LuaInsight

English | [简体中文](README.zh-CN.md)

Lua Insight is a Lua plugin for IntelliJ IDEA and Android Studio that brings
[Luanalysis](https://github.com/Benjamin-Dobell/IntelliJ-Luanalysis)'s advanced type
analysis to the maintained [EmmyLua](https://github.com/EmmyLua/IntelliJ-EmmyLua)
codebase, preserving EmmyLua's editor tools and debugger integrations.
This independent fork is maintained by [ClarkWain](https://github.com/ClarkWain).

## Install

1. Download the package for the IDE version from [Releases](https://github.com/ClarkWain/IntelliJ-LuaInsight/releases).
2. Disable EmmyLua, Luanalysis and other Lua language plugins to avoid multiple
   providers handling the same Lua files.
3. Open **Settings → Plugins → gear menu → Install Plugin from Disk**, select the
   ZIP, and restart the IDE. Do not extract the ZIP yourself.

| Release package | Supported IntelliJ Platform range |
| --- | --- |
| `IntelliJ-LuaInsight-1.5.6-IDEA2025.zip` | IDEA 2025.1–2025.3 (251–253) |
| `IntelliJ-LuaInsight-1.5.6-IDEA2026.zip` | IDEA 2026.1–2026.2 (261–262) |

For Android Studio, select a package using the IntelliJ platform build shown in
**Help → About**: builds **251–253** use the **2025** package, and builds
**261–262** use the **2026** package.
Exact tested builds, checksums and runtime evidence are recorded in
[validation](docs/validation.md).

## Features

- Structural `@shape` types, indexed fields, typed dictionaries and tuples.
- Generic classes, constrained generics, generic aliases and recursive aliases.
- Expression and return casts with `@type`, and union exclusion with `@not`.
- Automatic nil checks in `and/or` expressions, guarded branches and after
  non-null assignments. See [flow-sensitive types](docs/flow-types.md).
- Callable classes, overloads, optional parameters and variadic results.
- Type checks for assignments, arguments, returns, inheritance and undeclared members.
- EmmyLua-compatible `int`/`integer`, `float`, `lightuserdata` and primitive/table
  inheritance, including inherited generic dictionary entries.
- Lua 5.1–5.4 definitions, completion, navigation, rename, formatting, documentation
  and the existing EmmyLua debugger integrations.

Type-safety inspections are enabled by default and type mismatches appear as
errors. Adjust their severity and strict-nil/unknown-type handling in the IDE's
inspection and Lua settings when needed.

See the [migration inventory](docs/luanalysis-migration.md) for source revisions
and feature-level regression coverage. The small
[validation project](examples/luanalysis-validation) includes valid generic and
shape examples, plus deliberately invalid examples for checking diagnostics.

## Build and validate

Use JDK 21 to run Gradle. The 2025 target compiles against IDEA 2025.1 with JDK 21;
the 2026 target compiles against IDEA 2026.1 and requires a JDK 25 toolchain.
Set `JAVA_HOME_25_X64` to that JDK if Gradle cannot find it.

```sh
./gradlew buildPlugin -DIDEA_VER=2025 --no-configuration-cache
./gradlew buildPlugin -DIDEA_VER=2026 --no-configuration-cache
```

For optional regression checks, run `./gradlew test` with the same target SDK.

Archives are written to `build/distributions/`. To use an installed IDE as the
SDK, add `-PlocalIdePath=/path/to/ide`. To verify against several installed IDEs,
add `-PverificationIdePaths="/path/to/ide1;/path/to/ide2"` and run `verifyPlugin`.
Compile releases against the oldest IDE in their declared range.

On Windows, this helper also handles non-ASCII checkout paths:

```powershell
.\scripts\validate-local.ps1 -IdePath 'C:\Program Files\Android\Android_Studio_2025'
```

## Credits and license

Based on EmmyLua by [tangzx](https://github.com/tangzx) and its contributors, and
Luanalysis by [Benjamin Dobell](https://github.com/Benjamin-Dobell) and its
contributors. Original copyright notices are retained. Both upstream projects
and this fork use the [Apache License 2.0](LICENSE.txt).

[EmmyLua documentation](https://emmylua.github.io/) ·
[Report an issue in this fork](https://github.com/ClarkWain/IntelliJ-LuaInsight/issues)
