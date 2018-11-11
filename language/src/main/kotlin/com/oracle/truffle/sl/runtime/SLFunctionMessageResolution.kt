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

import com.oracle.truffle.sl.runtime.SLContext.fromForeignValue

import com.oracle.truffle.api.interop.CanResolve
import com.oracle.truffle.api.interop.MessageResolution
import com.oracle.truffle.api.interop.Resolve
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.sl.nodes.call.SLDispatchNode
import com.oracle.truffle.sl.nodes.call.SLDispatchNodeGen

/**
 * The class containing all message resolution implementations of [SLFunction].
 */
/**
 * The class containing all message resolution implementations of [SLFunction].
 */
@MessageResolution(receiverType = SLFunction::class)
class SLFunctionMessageResolution {
    /*
     * An SL function resolves an EXECUTE message.
     */
    @Resolve(message = "EXECUTE")
    abstract class SLForeignFunctionExecuteNode : Node() {

        @Child
        private var dispatch = SLDispatchNodeGen.create()

        fun access(receiver: SLFunction, arguments: Array<Any>): Any {
            val arr = arrayOfNulls<Any>(arguments.size)
            // Before the arguments can be used by the SLFunction, they need to be converted to SL
            // values.
            for (i in arr.indices) {
                arr[i] = fromForeignValue(arguments[i])
            }
            val result = dispatch.executeDispatch(receiver, arr)
            return result
        }
    }

    /*
     * An SL function should respond to an IS_EXECUTABLE message with true.
     */
    @Resolve(message = "IS_EXECUTABLE")
    abstract class SLForeignIsExecutableNode : Node() {
        fun access(receiver: Any): Any {
            return receiver is SLFunction
        }
    }

    @CanResolve
    abstract class CheckFunction : Node() {
        companion object {

            protected fun test(receiver: TruffleObject): Boolean {
                return receiver is SLFunction
            }
        }
    }
}
