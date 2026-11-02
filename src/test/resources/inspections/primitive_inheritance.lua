-- Declared fields retain precedence, and incompatible overrides still produce errors.
---@class IntMap : table<string, int>
---@field title <error descr="Illegal override of \"title\". Type mismatch. Required: 'integer' Found: 'string'">string</error>
local IntMap = {}

---@type IntMap
local values
---@type integer
local count
---@type string
local title
count = values.answer
count = values["answer"]
title = values.title
values.answer = 1
values["answer"] = 2
values.answer = nil -- Inherited dictionary entry removal is still allowed in strict-nil mode.
values.answer = <error descr="Type mismatch. Required: 'integer | nil' Found: '\"bad\"'">"bad"</error>
values["answer"] = <error descr="Type mismatch. Required: 'integer | nil' Found: '1.5'">1.5</error>
count = <error descr="Type mismatch. Required: 'integer' Found: 'string'">values.title</error>
values.title = <error descr="Type mismatch. Required: 'string' Found: 'nil'">nil</error>

---@type table<string, int>
local dictionary
dictionary = values

---@class DerivedMap : IntMap
local DerivedMap = {}

---@type DerivedMap
local derived
count = derived.answer
derived.answer = <error descr="Type mismatch. Required: 'integer | nil' Found: '\"bad\"'">"bad"</error>

---@class GenericMap<T> : table<string, T>
local GenericMap = {}

---@type GenericMap<int>
local generic
count = generic.answer
generic.answer = 3
generic.answer = nil
generic.answer = <error descr="Type mismatch. Required: 'integer | nil' Found: '\"bad\"'">"bad"</error>

---@class NamedString : string
---@type NamedString
local namedString
---@type string
local text
text = namedString
text = namedString:sub(2)

---@class Count : integer
---@type Count
local namedCount
count = namedCount
