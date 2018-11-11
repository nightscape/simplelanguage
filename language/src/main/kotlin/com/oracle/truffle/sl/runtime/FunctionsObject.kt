/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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
import com.oracle.truffle.api.interop.MessageResolution
import com.oracle.truffle.api.interop.Resolve
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.interop.UnknownIdentifierException
import com.oracle.truffle.api.nodes.Node

import java.util.HashMap

internal class FunctionsObject : TruffleObject {

    val functions: Map<String, SLFunction> = HashMap()

    override fun getForeignAccess(): ForeignAccess {
        return FunctionsObjectMessageResolutionForeign.ACCESS
    }

    @MessageResolution(receiverType = FunctionsObject::class)
    internal class FunctionsObjectMessageResolution {

        @Resolve(message = "HAS_KEYS")
        internal abstract class FunctionsObjectHasKeysNode : Node() {

            fun access(fo: FunctionsObject): Any {
                return true
            }
        }

        @Resolve(message = "KEYS")
        internal abstract class FunctionsObjectKeysNode : Node() {

            @CompilerDirectives.TruffleBoundary
            fun access(fo: FunctionsObject): Any {
                return FunctionsObjectMessageResolution.FunctionNamesObject(fo.functions.keys)
            }
        }

        @Resolve(message = "KEY_INFO")
        internal abstract class FunctionsObjectKeyInfoNode : Node() {

            @CompilerDirectives.TruffleBoundary
            fun access(fo: FunctionsObject, name: String): Any {
                return if (fo.functions.containsKey(name)) {
                    3
                } else {
                    0
                }
            }
        }

        @Resolve(message = "READ")
        internal abstract class FunctionsObjectReadNode : Node() {

            @CompilerDirectives.TruffleBoundary
            fun access(fo: FunctionsObject, name: String): Any? {
                try {
                    return fo.functions[name]
                } catch (ioob: IndexOutOfBoundsException) {
                    return null
                }

            }
        }

        internal class FunctionNamesObject private constructor(private val names: Set<String>) : TruffleObject {

            override fun getForeignAccess(): ForeignAccess {
                return FunctionNamesMessageResolutionForeign.ACCESS
            }

            @MessageResolution(receiverType = FunctionsObjectMessageResolution.FunctionNamesObject::class)
            internal class FunctionNamesMessageResolution {

                @Resolve(message = "HAS_SIZE")
                internal abstract class FunctionNamesHasSizeNode : Node() {

                    fun access(namesObject: FunctionsObjectMessageResolution.FunctionNamesObject): Any {
                        return true
                    }
                }

                @Resolve(message = "GET_SIZE")
                internal abstract class FunctionNamesGetSizeNode : Node() {

                    @CompilerDirectives.TruffleBoundary
                    fun access(namesObject: FunctionsObjectMessageResolution.FunctionNamesObject): Any {
                        return namesObject.names.size
                    }
                }

                @Resolve(message = "READ")
                internal abstract class FunctionNamesReadNode : Node() {

                    @CompilerDirectives.TruffleBoundary
                    fun access(namesObject: FunctionsObjectMessageResolution.FunctionNamesObject, index: Int): Any {
                        if (index >= namesObject.names.size) {
                            throw UnknownIdentifierException.raise(Integer.toString(index))
                        }
                        val iterator = namesObject.names.iterator()
                        var i = index
                        while (i-- > 0) {
                            iterator.next()
                        }
                        return iterator.next()
                    }
                }

            }

            companion object {

                fun isInstance(obj: TruffleObject): Boolean {
                    return obj is FunctionsObjectMessageResolution.FunctionNamesObject
                }
            }
        }
    }

    companion object {

        fun isInstance(obj: TruffleObject): Boolean {
            return obj is FunctionsObject
        }
    }
}
