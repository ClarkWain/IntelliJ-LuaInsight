package com.tang.intellij.test.inspections

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.stubs.FileLocalStringEnumerator
import com.intellij.psi.util.PsiTreeUtil
import com.tang.intellij.lua.psi.LuaLiteralExpr
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.ty.*
import com.tang.intellij.test.LuaTestBase
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class PrimitiveTypesTest : LuaTestBase() {
    fun testNumericSubtypes() {
        val context = SearchContext.get(project)
        val integer = Ty.getBuiltin("integer")
        val float = Ty.getBuiltin("float")
        assertNotNull(integer)
        assertNotNull(float)
        assertSame(integer, Ty.getBuiltin("int"))
        val strict = TyVarianceFlags.STRICT_UNKNOWN or TyVarianceFlags.STRICT_NIL
        assertTrue(Primitives.NUMBER.contravariantOf(context, integer!!, strict))
        assertTrue(Primitives.NUMBER.contravariantOf(context, float!!, strict))
        assertFalse(integer.contravariantOf(context, float, strict))
        assertFalse(float.contravariantOf(context, integer, strict))
        assertFalse(integer.contravariantOf(context, Primitives.NUMBER, strict))
        assertFalse(float.contravariantOf(context, Primitives.NUMBER, strict))
    }

    fun testLightUserdataIsBuiltin() {
        val context = SearchContext.get(project)
        val pointer = Ty.getBuiltin("lightuserdata")
        assertNotNull(pointer)
        val strict = TyVarianceFlags.STRICT_UNKNOWN or TyVarianceFlags.STRICT_NIL
        assertFalse(pointer!!.contravariantOf(context, Ty.create("userdata"), strict))
        assertFalse(Ty.create("userdata").contravariantOf(context, pointer, strict))
    }

    fun testPrimitiveSerialization() {
        for (name in listOf("integer", "int", "float", "lightuserdata", "number", "string")) {
            val type = Ty.getBuiltin(name)
            assertNotNull(name, type)
            assertSame(name, type, roundTrip(type!!))
        }
    }

    fun testNumericLiteralSerialization() {
        val context = SearchContext.get(project)
        val file = myFixture.configureByText("numbers.lua", "local a = 1; local b = 1.0")
        val literals = PsiTreeUtil.findChildrenOfType(file, LuaLiteralExpr::class.java).toList()
        assertEquals(2, literals.size)
        val integer = Ty.create("integer")
        val float = Ty.create("float")
        val strict = TyVarianceFlags.STRICT_UNKNOWN or TyVarianceFlags.STRICT_NIL
        val intLiteral = roundTrip(literals[0].guessType(context)!!)
        val floatLiteral = roundTrip(literals[1].guessType(context)!!)
        assertTrue(integer.contravariantOf(context, intLiteral, strict))
        assertFalse(float.contravariantOf(context, intLiteral, strict))
        assertTrue(float.contravariantOf(context, floatLiteral, strict))
        assertFalse(integer.contravariantOf(context, floatLiteral, strict))
        assertEquals(intLiteral, floatLiteral) // Existing numeric literal value equality.
        for (union in listOf(intLiteral.union(context, floatLiteral), TyUnion.union(context, listOf(intLiteral, floatLiteral)))) {
            assertFalse(integer.contravariantOf(context, union, strict))
            assertFalse(float.contravariantOf(context, union, strict))
            assertTrue(Primitives.NUMBER.contravariantOf(context, union, strict))
        }
    }

    fun testNumericUnionComposition() {
        val context = SearchContext.get(project)
        val strict = TyVarianceFlags.STRICT_UNKNOWN or TyVarianceFlags.STRICT_NIL
        for ((first, second, narrowed) in listOf(
            Triple("1", "1.0", Primitives.INTEGER),
            Triple("1.0", "1", Primitives.FLOAT)
        )) {
            val common = TyPrimitiveLiteral.getNumberTy(if (first == "1") "2" else "2.0")!!
            val left = TyUnion.union(context, TyPrimitiveLiteral.getNumberTy(first)!!, common)
            val right = TyUnion.union(context, TyPrimitiveLiteral.getNumberTy(second)!!, common)
            val combined = TyUnion.union(context, left, right)
            assertTrue(Primitives.NUMBER.contravariantOf(context, combined, strict))
            assertFalse("Merging equal unions must retain both numeric representations",
                narrowed.contravariantOf(context, combined, strict))
        }
    }

    private fun roundTrip(type: ITy): ITy {
        val bytes = ByteArrayOutputStream()
        val names = FileLocalStringEnumerator(true)
        StubOutputStream(bytes, names).use { Ty.serialize(type, it) }
        return StubInputStream(ByteArrayInputStream(bytes.toByteArray()), names).use { Ty.deserialize(it) }
    }
}
