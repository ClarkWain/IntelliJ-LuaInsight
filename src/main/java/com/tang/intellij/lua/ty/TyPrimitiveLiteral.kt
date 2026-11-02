/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.ty

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.tang.intellij.lua.lang.type.LuaNumber
import com.tang.intellij.lua.search.SearchContext
import java.util.concurrent.ConcurrentHashMap


class TyPrimitiveLiteral private constructor(val primitiveKind: TyPrimitiveKind, val value: String,
                                             val numberKind: TyPrimitiveKind) : Ty(TyKind.PrimitiveLiteral) {
    override val displayName: String by lazy { if (primitiveKind == TyPrimitiveKind.String) "\"${value.replace("\"", "\\\"")}\"" else value }

    // Primitives.TRUE/Primitives.FALSE are TyPrimitiveLiteral, to avoid circular references (a null booleanType) we make this lazy.
    override val booleanType by lazy {
        if (primitiveKind != TyPrimitiveKind.Boolean || value == "true") Primitives.TRUE else Primitives.FALSE
    }

    val primitiveType by lazy {
        Primitives.get(primitiveKind)
    }

    override fun getSuperType(context: SearchContext): ITy {
        return if (primitiveKind == TyPrimitiveKind.Number) Primitives.get(numberKind) else primitiveType
    }

    override fun contravariantOf(context: SearchContext, other: ITy, varianceFlags: Int): Boolean {
        // Even when !LuaSettings.instance.isNilStrict, nil is never assignable to a primitive literal.
        return this == other || (other.isUnknown && varianceFlags and TyVarianceFlags.STRICT_UNKNOWN == 0) || super.contravariantOf(context, other, varianceFlags)
    }

    override fun equals(other: Any?): Boolean {
        return other is TyPrimitiveLiteral && primitiveKind == other.primitiveKind && value.equals(other.value)
    }

    override fun equals(context: SearchContext, other: ITy, equalityFlags: Int): Boolean {
        if (this === other) {
            return true
        }

        val resolvedOther = Ty.resolve(context, other)
        return resolvedOther is TyPrimitiveLiteral && primitiveKind == resolvedOther.primitiveKind && value.equals(resolvedOther.value)
    }

    override fun hashCode(): Int {
        return primitiveKind.hashCode() * 31 * value.hashCode()
    }

    override fun guessMemberType(context: SearchContext, name: String): ITy? {
        return primitiveType.guessMemberType(context, name)
    }

    override fun guessIndexerType(context: SearchContext, indexTy: ITy, exact: Boolean): ITy? {
        return primitiveType.guessIndexerType(context, indexTy, exact)
    }

    companion object {
        private val primitiveLiterals = ConcurrentHashMap<String, TyPrimitiveLiteral>()

        fun getTy(primitiveKind: TyPrimitiveKind, value: String,
                  numberKind: TyPrimitiveKind = TyPrimitiveKind.Number): TyPrimitiveLiteral {
            val id = "$primitiveKind:$value:$numberKind"
            return primitiveLiterals.getOrPut(id, { TyPrimitiveLiteral(primitiveKind, value, numberKind) })
        }

        fun getNumberTy(text: String): TyPrimitiveLiteral? {
            val number = LuaNumber.getValue(text) ?: return null
            val isInteger = integerLiteral.matches(text)
                && (text.startsWith("0x", true) || text.toLongOrNull() != null)
            val kind = if (isInteger) TyPrimitiveKind.Integer else TyPrimitiveKind.Float
            return getTy(TyPrimitiveKind.Number, number.toString(), kind)
        }

        private val integerLiteral = Regex("(?:[0-9]+|0[xX][0-9a-fA-F]+)")
    }
}

object TyPrimitiveLiteralSerializer : TySerializer<TyPrimitiveLiteral>() {
    override fun deserializeTy(flags: Int, stream: StubInputStream): TyPrimitiveLiteral {
        val primitiveKind = stream.readByte().toInt()
        val kind = TyPrimitiveKind.values()[primitiveKind]
        val value = stream.readUTF()
        val numberKind = if (kind == TyPrimitiveKind.Number) TyPrimitiveKind.values()[stream.readByte().toInt()]
            else TyPrimitiveKind.Number
        return TyPrimitiveLiteral.getTy(kind, value, numberKind)
    }

    override fun serializeTy(ty: TyPrimitiveLiteral, stream: StubOutputStream) {
        stream.writeByte(ty.primitiveKind.ordinal)
        stream.writeUTF(ty.value)
        if (ty.primitiveKind == TyPrimitiveKind.Number) {
            stream.writeByte(ty.numberKind.ordinal)
        }
    }
}
