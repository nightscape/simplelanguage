/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.sl.runtime

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.interop.ForeignAccess
import com.oracle.truffle.api.interop.KeyInfo
import com.oracle.truffle.api.interop.MessageResolution
import com.oracle.truffle.api.interop.Resolve
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.interop.UnknownIdentifierException
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.`object`.DynamicObject
import com.oracle.truffle.sl.nodes.access.SLReadPropertyCacheNode
import com.oracle.truffle.sl.nodes.access.SLReadPropertyCacheNodeGen
import com.oracle.truffle.sl.nodes.access.SLWritePropertyCacheNode
import com.oracle.truffle.sl.nodes.access.SLWritePropertyCacheNodeGen
import com.oracle.truffle.sl.nodes.call.SLDispatchNode
import com.oracle.truffle.sl.nodes.call.SLDispatchNodeGen
import com.oracle.truffle.sl.nodes.interop.SLForeignToSLTypeNode
import com.oracle.truffle.sl.nodes.interop.SLForeignToSLTypeNodeGen

/**
 * The class containing all message resolution implementations of an SL object.
 */
@MessageResolution(receiverType = SLObjectType::class)
class SLObjectMessageResolution {
    /*
     * An SL object resolves the WRITE message and maps it to an object property write access.
     */
    @Resolve(message = "WRITE")
    abstract class SLForeignWriteNode : Node() {

        @Child
        private var write = SLWritePropertyCacheNodeGen.create()
        @Child
        private var nameToSLType = SLForeignToSLTypeNodeGen.create()
        @Child
        private var valueToSLType = SLForeignToSLTypeNodeGen.create()

        fun access(receiver: DynamicObject, name: Any, value: Any): Any {
            val convertedName = nameToSLType.executeConvert(name)
            val convertedValue = valueToSLType.executeConvert(value)
            try {
                write.executeWrite(receiver, convertedName, convertedValue)
            } catch (undefinedName: SLUndefinedNameException) {
                throw UnknownIdentifierException.raise(convertedName.toString())
            }

            return convertedValue
        }
    }

    /*
     * An SL object resolves the READ message and maps it to an object property read access.
     */
    @Resolve(message = "READ")
    abstract class SLForeignReadNode : Node() {

        @Child
        private var read = SLReadPropertyCacheNodeGen.create()
        @Child
        private var nameToSLType = SLForeignToSLTypeNodeGen.create()

        fun access(receiver: DynamicObject, name: Any): Any {
            val convertedName = nameToSLType.executeConvert(name)
            val result: Any
            try {
                result = read.executeRead(receiver, convertedName)
            } catch (undefinedName: SLUndefinedNameException) {
                throw UnknownIdentifierException.raise(convertedName.toString())
            }

            return result
        }
    }

    /*
     * An SL object resolves the REMOVE message and maps it to an object property delete access.
     */
    @Resolve(message = "REMOVE")
    abstract class SLForeignRemoveNode : Node() {

        @Child
        private var nameToSLType = SLForeignToSLTypeNodeGen.create()

        fun access(receiver: DynamicObject, name: Any): Any {
            val convertedName = nameToSLType.executeConvert(name)
            return if (receiver.containsKey(convertedName)) {
                receiver.delete(convertedName)
            } else {
                throw UnknownIdentifierException.raise(convertedName.toString())
            }
        }
    }

    /*
     * An SL object resolves the INVOKE message and maps it to an object property read access
     * followed by an function invocation. The object property must be an SL function object, which
     * is executed eventually.
     */
    @Resolve(message = "INVOKE")
    abstract class SLForeignInvokeNode : Node() {

        @Child
        private var dispatch = SLDispatchNodeGen.create()

        fun access(receiver: DynamicObject, name: String, arguments: Array<Any>): Any {
            val property = receiver.get(name)
            if (property is SLFunction) {
                val arr = arrayOfNulls<Any>(arguments.size)
                // Before the arguments can be used by the SLFunction, they need to be converted to
                // SL
                // values.
                for (i in arguments.indices) {
                    arr[i] = SLContext.fromForeignValue(arguments[i])
                }
                val result = dispatch.executeDispatch(property, arr)
                return result
            } else {
                throw UnknownIdentifierException.raise(name)
            }
        }
    }

    @Resolve(message = "HAS_KEYS")
    abstract class SLForeignHasPropertiesNode : Node() {

        fun access(receiver: DynamicObject): Any {
            return true
        }
    }

    @Resolve(message = "KEY_INFO")
    abstract class SLForeignPropertyInfoNode : Node() {

        fun access(receiver: DynamicObject, name: Any): Int {
            val property = receiver.get(name)
            return if (property == null) {
                KeyInfo.INSERTABLE
            } else if (property is SLFunction) {
                KeyInfo.READABLE or KeyInfo.REMOVABLE or KeyInfo.MODIFIABLE or KeyInfo.INVOCABLE
            } else {
                KeyInfo.READABLE or KeyInfo.REMOVABLE or KeyInfo.MODIFIABLE
            }
        }
    }

    @Resolve(message = "KEYS")
    abstract class SLForeignPropertiesNode : Node() {
        fun access(receiver: DynamicObject): Any {
            return obtainKeys(receiver)
        }

        @CompilerDirectives.TruffleBoundary
        private fun obtainKeys(receiver: DynamicObject): Any {
            val keys = receiver.shape.keyList.toTypedArray()
            return KeysArray(keys)
        }
    }

    @MessageResolution(receiverType = KeysArray::class)
    internal class KeysArray(private val keys: Array<Any>) : TruffleObject {

        @Resolve(message = "HAS_SIZE")
        internal abstract class HasSize : Node() {

            fun access(receiver: KeysArray): Any {
                return true
            }
        }

        @Resolve(message = "GET_SIZE")
        internal abstract class GetSize : Node() {

            fun access(receiver: KeysArray): Any {
                return receiver.keys.size
            }
        }

        @Resolve(message = "READ")
        internal abstract class Read : Node() {

            fun access(receiver: KeysArray, index: Int): Any {
                try {
                    val key = receiver.keys[index]
                    assert(key is String)
                    return key
                } catch (e: IndexOutOfBoundsException) {
                    CompilerDirectives.transferToInterpreter()
                    throw UnknownIdentifierException.raise(index.toString())
                }

            }
        }

        override fun getForeignAccess(): ForeignAccess {
            return KeysArrayForeign.ACCESS
        }

        companion object {

            fun isInstance(array: TruffleObject): Boolean {
                return array is KeysArray
            }
        }

    }

}
