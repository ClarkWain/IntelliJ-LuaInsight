-- Editor-only type-checking sample. Do not execute it as a Lua program.
-- Expected diagnostics: four argument/assignment errors at the marked lines.

---@class Counters : table<string, int>
---@class Label : string

---@param value integer
local function takesInteger(value) end
---@param value float
local function takesFloat(value) end

---@param counters Counters
---@param pointer lightuserdata
---@param object userdata
---@param label Label
local function check(counters, pointer, object, label)
    takesInteger(1)
    takesFloat(1.0)
    ---@type number
    local numberValue = 1.5
    ---@type integer
    local total = counters.count
    counters.count = 2
    counters["count"] = 3
    counters.count = nil
    ---@type string
    local text = label:sub(2)

    takesInteger(1.5)          -- Expected: integer required.
    takesFloat(1)              -- Expected: float required.
    pointer = object           -- Expected: lightuserdata required.
    counters.count = "invalid" -- Expected: integer or nil required.
end
