# Luanalysis feature migration

This fork integrates the language and type-analysis implementation from
[Benjamin-Dobell/IntelliJ-Luanalysis](https://github.com/Benjamin-Dobell/IntelliJ-Luanalysis)
into current [EmmyLua/IntelliJ-EmmyLua](https://github.com/EmmyLua/IntelliJ-EmmyLua).
Both projects are Apache-2.0 licensed; source copyright notices are retained.

Source revisions:

- EmmyLua: `7f72caa2` (current upstream at migration time).
- Luanalysis: `d796bb67` (including fixes after its 1.4.0 release).
- Common ancestor: `19c8a30140a6612b24de17032cdc3d01e5260c8b`.

## Target platform

The Android Studio validation target is `AndroidStudio2025.2.3`, build
`AI-252.28238.7.2523.14688667`, with JBR 21.0.8. The primary target is IntelliJ
Platform **252**. Release builds cover the **251–253** and **261–262** ranges.
Compatibility must be verified per target; a declared build range is not a test result.

## Feature inventory

| Luanalysis feature | Implementation | Regression coverage |
| --- | --- | --- |
| EmmyDoc block comments and lambda comment binding | `lua.flex`, `doc.flex`, parser and PSI mixins | parsing, closure and completion fixtures |
| `@type` expression casts and type lists | comment PSI, `Expressions`, `TyMultipleResults` | `type_casts.lua`, `multiple_results.lua` |
| `@not` union exclusion and variadic casts | `TyUnion`, `TyMultipleResults`, expression inference | `type_casts.lua`, `varreturn.lua` |
| `@type` on return statements | return PSI and return inspection | return type tests, `function_closures.lua` |
| Primitive literal types (number, boolean, string) | doc lexer/parser, `TyPrimitiveLiteral` | `boolean.lua`, `numbers.lua`, `string_literals.lua`, primitive completion tests |
| EmmyLua numeric annotation subtypes (`int`/`integer`, `float`) | primitive resolution, numeric literal representation and serialization | `emmy_numeric_types.lua`, `PrimitiveTypesTest` |
| Distinct `lightuserdata` annotations | primitive resolution and standard library type declarations | `lightuserdata.lua`, `PrimitiveTypesTest` |
| Primitive inheritance, including `table<string, int>` | inheritance inspection and substituted primitive member lookup | `primitive_inheritance.lua`, `illegal_inheritance.lua` |
| Generic classes, constraints and dependent parameters | generic PSI, `GenericAnalyzer`, `TyGeneric` | `generic_class_*.lua`, `generics.lua` |
| Generic scope within classes and functions | scope tree and generic resolution | `generic_class_scope.lua`, `function_generics_scope.lua` |
| Generic aliases and recursive aliases | alias stubs, `TyAlias`, substitutions | `generic_alias.lua`, `recursive_alias.lua`, illegal alias tests |
| Structural `@shape` types and detailed field errors | `TyClass`, `ProblemUtil`, indexed member stubs | `shape.lua`, `generic_class_*.lua`, `doc_tables.lua` |
| Indexed fields, typed dictionaries and strongly typed tuples | table grammar, member indexes and variance | `indexed_fields.lua`, `shape.lua`, `tables.lua` |
| Callable classes via `@overload` | signatures and call inference | `lambda_class.lua`, `overloads.lua` |
| Partially typed functions | function grammar and signatures | `function_partially_typed.lua` |
| Optional parameters (short and long forms) | function parameter PSI and required parameter inspection | `function_optional_parameters.lua` |
| `...: T` and variadic return values | function grammar, `TyMultipleResults`, vararg inference | `varargs.lua`, `varreturn.lua`, multiple return completion tests |
| Correct variance, assignment, function arguments and `self` | type system, assignments and signature inspection | `function_covariance.lua`, `generic_self.lua`, `self.lua`, `strict_nil.lua` |
| Undeclared members, illegal overrides/inheritance, generic shadowing | new inspections and extension registrations | type safety and dedicated inspection suites |
| Standard library typings and `.def.lua` support | Lua 5.1–5.4 definitions and library provider | standard library fixtures in type safety suite |
| Completion, documentation, navigation, rename and formatter adjustments | PSI consumers updated for the migrated grammar | completion, rename, move, structure, format and generic suites |

Some fixture names above describe coverage categories; exact paths and executed
test counts are recorded in the validation report after running the suite.

## EmmyLua compatibility (issue #1)

- `int` is an alias for `integer`. `integer` and `float` are distinct subtypes
  of `number`; a plain `number` does not establish either narrower type.
- Numeric expression literals retain their integer/float spelling for subtype
  checks, including through unary minus and stub serialization. Numeric literal
  annotations retain Luanalysis value equality (`1` accepts `1.0`). Merging equal
  literal values with different representations discards the narrower representation.
- Arithmetic and unannotated generic inference retain the migrated conservative
  `number` widening. These annotations do not change pre-5.3 Lua runtime semantics.
- `lightuserdata` remains distinct from the existing `userdata` class.
- Classes may inherit primitive types and generic tables. Inherited dictionary
  values participate in member/indexer inference and assignment checks, while
  declared fields retain precedence. Self/cyclic inheritance, literal bases and
  array bases remain diagnosed.
- Stub version 3 forces an index rebuild because numeric literals now serialize
  their representation and the new built-in names must be resolved again.

## Integration decisions

- Use an independent plugin identity, display name Lua Insight, and private
  extension-point/index namespaces. Source package names
  and existing Lua settings remain intact. Disable other Lua language plugins
  before enabling this fork; an independent ID does not allow multiple language
  implementations to handle the same Lua files simultaneously.
- Preserve upstream debugger implementations and current native debugger package
  versions, adapting their references to the new PSI names.
- Preserve upstream platform compatibility helpers and merge newer API fixes.
- Migrate grammar, generated PSI, serialization, inference and inspections together:
  mixing old and new type representations would corrupt inference and indexing.
- Bump the file stub version so existing indexes rebuild with the new serialization.
- Keep Lua standard library definitions available as classpath resources in tests
  and in the plugin archive.
- Remove the build task that resets/cleans the checkout. Builds must preserve local
  source changes.
- Luanalysis enables type-safety inspections by default and reports type mismatches
  as errors. Existing projects can configure inspections and strict-nil/unknown-type
  handling in the IDE; this is an intentional user-visible behavior change.

## Reproduce against the installed Android Studio

From PowerShell, set `JAVA_HOME` to the IDE's JBR directory, then run:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android_Studio_2025\jbr'
.\gradlew.bat test buildPlugin -PlocalIdePath='C:/Program Files/Android/Android_Studio_2025'
```

For a Windows checkout containing non-ASCII characters, use the validation script:

```powershell
.\scripts\validate-local.ps1 -IdePath 'C:\Program Files\Android\Android_Studio_2025'
```

It validates tests, the plugin archive and the Plugin Verifier against that exact
IDE. It uses an unused temporary ASCII drive mapping when needed and removes only
that mapping on exit. Validation runs with isolated IDE settings and plugin directories.

Without `localIdePath`, the default build downloads IntelliJ IDEA 2025.1.7.2 (251).
The 2026 release target uses IDEA 2026.1.5 (261), selected with `-DIDEA_VER=2026`
and a JDK 25 toolchain. Run Gradle itself with JDK 21.
Validation requires automated regression tests, API compatibility checks and an
actual sandbox IDE run with a Lua sample. Build success alone is insufficient.
