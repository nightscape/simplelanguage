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
package com.oracle.truffle.sl.nodes.expression

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal
import com.oracle.truffle.api.TruffleLanguage.ContextReference
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.sl.SLLanguage
import com.oracle.truffle.sl.nodes.SLExpressionNode
import com.oracle.truffle.sl.runtime.SLContext
import com.oracle.truffle.sl.runtime.SLFunction
import com.oracle.truffle.sl.runtime.SLFunctionRegistry

/**
 * Constant literal for a [function][SLFunction] value, created when a function name occurs as
 * a literal in SL source code. Note that function redefinition can change the [ call target][CallTarget] that is executed when calling the function, but the [SLFunction] for a name
 * never changes. This is guaranteed by the [SLFunctionRegistry].
 */
@NodeInfo(shortName = "func")
class SLFunctionLiteralNode(language: SLLanguage,
                            /** The name of the function.  */
                            private val functionName: String) : SLExpressionNode() {

    /**
     * The resolved function. During parsing (in the constructor of this node), we do not have the
     * [SLContext] available yet, so the lookup can only be done at [ first execution][.executeGeneric]. The [CompilationFinal] annotation ensures that the function can still
     * be constant folded during compilation.
     */
    @CompilationFinal
    private var cachedFunction: SLFunction? = null

    private val reference: ContextReference<SLContext>

    init {
        this.reference = language.contextReference
    }

    override fun executeGeneric(frame: VirtualFrame): SLFunction {
        if (cachedFunction == null) {
            /* We are about to change a @CompilationFinal field. */
            CompilerDirectives.transferToInterpreterAndInvalidate()
            /* First execution of the node: lookup the function in the function registry. */
            cachedFunction = reference.get().functionRegistry.lookup(functionName, true)
        }
        return cachedFunction!!
    }

}
