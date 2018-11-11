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
import com.oracle.truffle.api.`object`.FinalLocationException
import com.oracle.truffle.api.`object`.IncompatibleLocationException
import com.oracle.truffle.api.`object`.Location
import com.oracle.truffle.api.`object`.Property
import com.oracle.truffle.api.`object`.Shape

abstract class SLWritePropertyCacheNode : SLPropertyCacheNode() {

    abstract fun executeWrite(receiver: DynamicObject, name: Any, value: Any)

    @TruffleBoundary
    @Specialization(guards = arrayOf("!receiver.getShape().isValid()"))
    protected fun updateShape(receiver: DynamicObject, name: Any, value: Any) {
        /*
         * Slow path that we do not handle in compiled code. But no need to invalidate compiled
         * code.
         */
        CompilerDirectives.transferToInterpreter()
        receiver.updateShape()
        writeUncached(receiver, name, value)
    }

    companion object {

        /**
         * Polymorphic inline cache for writing a property that already exists (no shape change is
         * necessary).
         */
        @Specialization(limit = "CACHE_LIMIT", guards = arrayOf("cachedName.equals(name)", "shapeCheck(shape, receiver)", "location != null", "canSet(location, value)"), assumptions = arrayOf("shape.getValidAssumption()"))//
        //
        protected fun writeExistingPropertyCached(receiver: DynamicObject, name: Any, value: Any,
                                                  @Cached("name") cachedName: Any,
                                                  @Cached("lookupShape(receiver)") shape: Shape,
                                                  @Cached("lookupLocation(shape, name, value)") location: Location) {
            try {
                location.set(receiver, value, shape)

            } catch (ex: IncompatibleLocationException) {
                /* Our guards ensure that the value can be stored, so this cannot happen. */
                throw IllegalStateException(ex)
            } catch (ex: FinalLocationException) {
                throw IllegalStateException(ex)
            }

        }

        /**
         * Polymorphic inline cache for writing a property that does not exist yet (shape change is
         * necessary).
         */
        @Specialization(limit = "CACHE_LIMIT", guards = arrayOf("namesEqual(cachedName, name)", "shapeCheck(oldShape, receiver)", "oldLocation == null", "canStore(newLocation, value)"), assumptions = arrayOf("oldShape.getValidAssumption()", "newShape.getValidAssumption()"))//
        //
        protected fun writeNewPropertyCached(receiver: DynamicObject, name: Any, value: Any,
                                             @Cached("name") cachedName: Any,
                                             @Cached("lookupShape(receiver)") oldShape: Shape,
                                             @Cached("lookupLocation(oldShape, name, value)") oldLocation: Location,
                                             @Cached("defineProperty(oldShape, name, value)") newShape: Shape,
                                             @Cached("lookupLocation(newShape, name)") newLocation: Location) {
            try {
                newLocation.set(receiver, value, oldShape, newShape)

            } catch (ex: IncompatibleLocationException) {
                /* Our guards ensure that the value can be stored, so this cannot happen. */
                throw IllegalStateException(ex)
            }

        }

        /** Try to find the given property in the shape.  */
        protected fun lookupLocation(shape: Shape, name: Any): Location? {
            CompilerAsserts.neverPartOfCompilation()

            val property = shape.getProperty(name)
                    ?: /* Property does not exist yet, so a shape change is necessary. */
                    return null

            return property.location
        }

        /**
         * Try to find the given property in the shape. Also returns null when the value cannot be store
         * into the location.
         */
        protected fun lookupLocation(shape: Shape, name: Any, value: Any): Location? {
            val location = lookupLocation(shape, name)
            return if (location == null || !location.canSet(value)) {
                /* Existing property has an incompatible type, so a shape change is necessary. */
                null
            } else location

        }

        protected fun defineProperty(oldShape: Shape, name: Any, value: Any): Shape {
            return oldShape.defineProperty(name, value, 0)
        }

        /**
         * There is a subtle difference between [Location.canSet] and [Location.canStore].
         * We need [Location.canSet] for the guard of [.writeExistingPropertyCached] because
         * there we call [Location.set]. We use the more relaxed [Location.canStore] for the
         * guard of [SLWritePropertyCacheNode.writeNewPropertyCached] because there we perform a
         * shape transition, i.e., we are not actually setting the value of the new location - we only
         * transition to this location as part of the shape change.
         */
        protected fun canSet(location: Location, value: Any): Boolean {
            return location.canSet(value)
        }

        /** See [.canSet] for the difference between the two methods.  */
        protected fun canStore(location: Location, value: Any): Boolean {
            return location.canStore(value)
        }

        /**
         * The generic case is used if the number of shapes accessed overflows the limit of the
         * polymorphic inline cache.
         */
        @TruffleBoundary
        @Specialization(replaces = arrayOf("writeExistingPropertyCached", "writeNewPropertyCached"), guards = arrayOf("receiver.getShape().isValid()"))
        protected fun writeUncached(receiver: DynamicObject, name: Any, value: Any) {
            receiver.define(name, value)
        }

        fun create(): SLWritePropertyCacheNode {
            return SLWritePropertyCacheNodeGen.create()
        }
    }

}
