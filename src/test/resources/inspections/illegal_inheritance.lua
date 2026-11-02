---@<error descr="Illegal self inheritance">class SelfInheritance : SelfInheritance</error>

---@<error descr="Illegal cyclical inheritance from CyclicInheritance2">class CyclicInheritance1 : CyclicInheritance3</error>

---@<error descr="Illegal cyclical inheritance from CyclicInheritance3">class CyclicInheritance2 : CyclicInheritance1</error>

---@<error descr="Illegal cyclical inheritance from CyclicInheritance1">class CyclicInheritance3 : CyclicInheritance2</error>

---@class PrimitiveInheritance : string

---@class TableInheritance : table

---@class GenericTableInheritance : table<string, string>

---@class IntegerInheritance : integer

---@class FloatInheritance : float

---@class LightUserdataInheritance : lightuserdata

---@<error descr="Illegal inheritance from array">class ArrayInheritance : string[]</error>

---@<error descr="Illegal inheritance from primitive type">class NumberInheritance : 1</error>

---@alias AliasedNumber 1

---@<error descr="Illegal inheritance from primitive type">class AliasedNumberInheritance : AliasedNumber</error>
