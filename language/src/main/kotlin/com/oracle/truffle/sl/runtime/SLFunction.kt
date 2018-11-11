/*
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates. All rights reserved.
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

import com.oracle.truffle.api.Assumption
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLogger
import com.oracle.truffle.api.interop.ForeignAccess
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.utilities.CyclicAssumption
import com.oracle.truffle.sl.SLLanguage
import com.oracle.truffle.sl.nodes.SLUndefinedFunctionRootNode
import java.util.logging.Level

/**
 * Represents a SL function. On the Truffle level, a callable element is represented by a
 * [call target][RootCallTarget]. This class encapsulates a call target, and adds version
 * support: functions in SL can be redefined, i.e. changed at run time. When a function is
 * redefined, the call target managed by this function object is changed (and [.callTarget] is
 * therefore not a final field).
 *
 *
 * Function redefinition is expected to be rare, therefore optimized call nodes want to speculate
 * that the call target is stable. This is possible with the help of a Truffle [Assumption]: a
 * call node can keep the call target returned by [.getCallTarget] cached until the
 * assumption returned by [.getCallTargetStable] is valid.
 *
 *
 * The [.callTarget] can be `null`. To ensure that only one [SLFunction] instance
 * per name exists, the [SLFunctionRegistry] creates an instance also when performing name
 * lookup. A function that has been looked up, i.e., used, but not defined, has a call target that
 * encapsulates a [SLUndefinedFunctionRootNode].
 */
class SLFunction(language: SLLanguage,
                 /** The name of the function.  */
                 val name: String) : TruffleObject {

    /** The current implementation of this function.  */
    private var callTarget: RootCallTarget? = null

    /**
     * Manages the assumption that the [.callTarget] is stable. We use the utility class
     * [CyclicAssumption], which automatically creates a new [Assumption] when the old
     * one gets invalidated.
     */
    private val callTargetStable: CyclicAssumption

    init {
        this.callTarget = Truffle.getRuntime().createCallTarget(SLUndefinedFunctionRootNode(language, name))
        this.callTargetStable = CyclicAssumption(name)
    }

    fun setCallTarget(callTarget: RootCallTarget) {
        this.callTarget = callTarget
        /*
         * We have a new call target. Invalidate all code that speculated that the old call target
         * was stable.
         */
        LOG.log(Level.FINE, "Installed call target for: {0}", name)
        callTargetStable.invalidate()
    }

    fun getCallTarget(): RootCallTarget? {
        return callTarget
    }

    fun getCallTargetStable(): Assumption {
        return callTargetStable.assumption
    }

    /**
     * This method is, e.g., called when using a function literal in a string concatenation. So
     * changing it has an effect on SL programs.
     */
    override fun toString(): String {
        return name
    }

    /**
     * In case you want some of your objects to co-operate with other languages, you need to make
     * them implement [TruffleObject] and provide additional
     * [foreign access implementation][SLFunctionMessageResolution].
     */
    override fun getForeignAccess(): ForeignAccess {
        return SLFunctionMessageResolutionForeign.ACCESS
    }

    companion object {
        private val LOG = TruffleLogger.getLogger(SLLanguage.ID, SLFunction::class.java!!)
    }
}
