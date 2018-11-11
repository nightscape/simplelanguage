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
package com.oracle.truffle.sl.nodes.expression

import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.UnexpectedResultException
import com.oracle.truffle.api.profiles.ConditionProfile
import com.oracle.truffle.sl.SLException
import com.oracle.truffle.sl.nodes.SLExpressionNode

/**
 * Logical operations in SL use short circuit evaluation: if the evaluation of the left operand
 * already decides the result of the operation, the right operand must not be executed. This is
 * expressed in using this base class for [SLLogicalAndNode] and [SLLogicalOrNode].
 */
abstract class SLShortCircuitNode(@field:Child private var left: SLExpressionNode, @field:Child private var right: SLExpressionNode) : SLExpressionNode() {

    /**
     * Short circuits might be used just like a conditional statement it makes sense to profile the
     * branch probability.
     */
    private val evaluateRightProfile = ConditionProfile.createCountingProfile()

    override fun executeGeneric(frame: VirtualFrame): Any {
        return executeBoolean(frame)
    }

    override fun executeBoolean(frame: VirtualFrame): Boolean {
        val leftValue: Boolean
        try {
            leftValue = left.executeBoolean(frame)
        } catch (e: UnexpectedResultException) {
            throw SLException.typeError(this, e.result, null)
        }

        val rightValue: Boolean
        try {
            if (evaluateRightProfile.profile(isEvaluateRight(leftValue))) {
                rightValue = right.executeBoolean(frame)
            } else {
                rightValue = false
            }
        } catch (e: UnexpectedResultException) {
            throw SLException.typeError(this, leftValue, e.result)
        }

        return execute(leftValue, rightValue)
    }

    /**
     * This method is called after the left child was evaluated, but before the right child is
     * evaluated. The right child is only evaluated when the return value is {code true}.
     */
    protected abstract fun isEvaluateRight(leftValue: Boolean): Boolean

    /**
     * Calculates the result of the short circuit operation. If the right node is not evaluated then
     * `false` is provided.
     */
    protected abstract fun execute(leftValue: Boolean, rightValue: Boolean): Boolean

}
