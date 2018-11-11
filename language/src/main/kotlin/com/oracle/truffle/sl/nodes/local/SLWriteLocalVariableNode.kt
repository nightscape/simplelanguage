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
package com.oracle.truffle.sl.nodes.local

import com.oracle.truffle.api.dsl.Fallback
import com.oracle.truffle.api.dsl.NodeChild
import com.oracle.truffle.api.dsl.NodeField
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.frame.FrameSlotKind
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.sl.nodes.SLExpressionNode

/**
 * Node to write a local variable to a function's [frame][VirtualFrame]. The Truffle frame API
 * allows to store primitive values of all Java primitive types, and Object values.
 */
@NodeChild("valueNode")
@NodeField(name = "slot", type = FrameSlot::class)
abstract class SLWriteLocalVariableNode : SLExpressionNode() {

    /**
     * Returns the descriptor of the accessed local variable. The implementation of this method is
     * created by the Truffle DSL based on the [NodeField] annotation on the class.
     */
    abstract val slot: FrameSlot

    /**
     * Specialized method to write a primitive `long` value. This is only possible if the
     * local variable also has currently the type `long` or was never written before,
     * therefore a Truffle DSL [custom guard][.isLongOrIllegal] is specified.
     */
    @Specialization(guards = arrayOf("isLongOrIllegal(frame)"))
    protected fun writeLong(frame: VirtualFrame, value: Long): Long {
        /* Initialize type on first write of the local variable. No-op if kind is already Long. */
        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Long)

        frame.setLong(slot, value)
        return value
    }

    @Specialization(guards = arrayOf("isBooleanOrIllegal(frame)"))
    protected fun writeBoolean(frame: VirtualFrame, value: Boolean): Boolean {
        /* Initialize type on first write of the local variable. No-op if kind is already Long. */
        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Boolean)

        frame.setBoolean(slot, value)
        return value
    }

    /**
     * Generic write method that works for all possible types.
     *
     *
     * Why is this method annotated with [Specialization] and not [Fallback]? For a
     * [Fallback] method, the Truffle DSL generated code would try all other specializations
     * first before calling this method. We know that all these specializations would fail their
     * guards, so there is no point in calling them. Since this method takes a value of type
     * [Object], it is guaranteed to never fail, i.e., once we are in this specialization the
     * node will never be re-specialized.
     */
    @Specialization(replaces = arrayOf("writeLong", "writeBoolean"))
    protected fun write(frame: VirtualFrame, value: Any): Any {
        /*
         * Regardless of the type before, the new and final type of the local variable is Object.
         * Changing the slot kind also discards compiled code, because the variable type is
         * important when the compiler optimizes a method.
         *
         * No-op if kind is already Object.
         */
        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Object)

        frame.setObject(slot, value)
        return value
    }

    /**
     * Guard function that the local variable has the type `long`.
     *
     * @param frame The parameter seems unnecessary, but it is required: Without the parameter, the
     * Truffle DSL would not check the guard on every execution of the specialization.
     * Guards without parameters are assumed to be pure, but our guard depends on the
     * slot kind which can change.
     */
    protected fun isLongOrIllegal(frame: VirtualFrame): Boolean {
        val kind = frame.frameDescriptor.getFrameSlotKind(slot)
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal
    }

    protected fun isBooleanOrIllegal(frame: VirtualFrame): Boolean {
        val kind = frame.frameDescriptor.getFrameSlotKind(slot)
        return kind == FrameSlotKind.Boolean || kind == FrameSlotKind.Illegal
    }
}
