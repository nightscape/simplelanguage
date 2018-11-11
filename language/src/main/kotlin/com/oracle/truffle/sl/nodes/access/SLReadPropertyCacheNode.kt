/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.sl.nodes.access

import com.oracle.truffle.api.CompilerAsserts
import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.dsl.Cached
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.`object`.DynamicObject
import com.oracle.truffle.api.`object`.Location
import com.oracle.truffle.api.`object`.Property
import com.oracle.truffle.api.`object`.Shape
import com.oracle.truffle.sl.runtime.SLUndefinedNameException

abstract class SLReadPropertyCacheNode : SLPropertyCacheNode() {

    abstract fun executeRead(receiver: DynamicObject, name: Any): Any

    protected fun lookupLocation(shape: Shape, name: Any): Location {
        /* Initialization of cached values always happens in a slow path. */
        CompilerAsserts.neverPartOfCompilation()

        val property = shape.getProperty(name)
                ?: /* Property does not exist. */
                throw SLUndefinedNameException.undefinedProperty(this, name)

        return property.location
    }

    /**
     * The generic case is used if the number of shapes accessed overflows the limit of the
     * polymorphic inline cache.
     */
    @TruffleBoundary
    @Specialization(replaces = arrayOf("readCached"), guards = arrayOf("receiver.getShape().isValid()"))
    protected fun readUncached(receiver: DynamicObject, name: Any): Any {
        val result = receiver.get(name)
                ?: /* Property does not exist. */
                throw SLUndefinedNameException.undefinedProperty(this, name)
        return result
    }

    @Specialization(guards = arrayOf("!receiver.getShape().isValid()"))
    protected fun updateShape(receiver: DynamicObject, name: Any): Any {
        CompilerDirectives.transferToInterpreter()
        receiver.updateShape()
        return readUncached(receiver, name)
    }

    companion object {

        /**
         * Polymorphic inline cache for a limited number of distinct property names and shapes.
         */
        @Specialization(limit = "CACHE_LIMIT", guards = arrayOf("namesEqual(cachedName, name)", "shapeCheck(shape, receiver)"), assumptions = arrayOf("shape.getValidAssumption()"))//
        //
        protected fun readCached(receiver: DynamicObject, name: Any,
                                 @Cached("name") cachedName: Any,
                                 @Cached("lookupShape(receiver)") shape: Shape,
                                 @Cached("lookupLocation(shape, name)") location: Location): Any {

            return location.get(receiver, shape)
        }

        fun create(): SLReadPropertyCacheNode {
            return SLReadPropertyCacheNodeGen.create()
        }
    }

}
