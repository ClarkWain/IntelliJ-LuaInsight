---@type integer
local whole
---@type int
local shortName
---@type float
local floating
---@type number
local general

whole = 1
whole = -1
whole = 0x10
shortName = whole
whole = shortName
floating = 1.0
floating = -1.5
floating = 2e3
general = whole
general = floating

whole = <error descr="Type mismatch. Required: 'integer' Found: 'float'">floating</error>
floating = <error descr="Type mismatch. Required: 'float' Found: 'integer'">whole</error>
whole = <error descr="Type mismatch. Required: 'integer' Found: 'number'">general</error>
floating = <error descr="Type mismatch. Required: 'float' Found: 'number'">general</error>
whole = <error descr="Type mismatch. Required: 'integer' Found: '1'">1.0</error>
floating = <error descr="Type mismatch. Required: 'float' Found: '1'">1</error>

---@param value int
local function takesInteger(value) end
---@param value float
local function takesFloat(value) end

takesInteger(1)
takesInteger(shortName)
takesInteger(<error descr="Type mismatch. Required: 'integer' Found: '1.5'">1.5</error>)
takesFloat(1.0)
takesFloat(floating)
takesFloat(<error descr="Type mismatch. Required: 'float' Found: 'integer'">whole</error>)

---@generic T : number
---@param value T
---@return T
local function identity(value) return value end
whole = identity(whole)
floating = identity(floating)

-- Numeric literal annotations retain Luanalysis value equality.
---@type 1
local one
one = 1
one = 1.0

---@return integer
local function returnsInteger() return 1 end
---@return float
local function returnsFloat() return 1.0 end
whole = returnsInteger()
floating = returnsFloat()
