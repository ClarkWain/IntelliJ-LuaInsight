---@type lightuserdata
local pointer
---@type userdata
local object
---@type lightuserdata
local anotherPointer

pointer = anotherPointer
pointer = <error descr="Type mismatch. Required: 'lightuserdata' Found: 'userdata'">object</error>
object = <error descr="Type mismatch. Required: 'userdata' Found: 'lightuserdata'">pointer</error>
pointer = <error descr="Type mismatch. Required: 'lightuserdata' Found: '1'">1</error>

---@param value lightuserdata
---@return lightuserdata
local function passPointer(value) return value end
pointer = passPointer(pointer)
passPointer(<error descr="Type mismatch. Required: 'lightuserdata' Found: 'userdata'">object</error>)
