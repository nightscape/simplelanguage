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

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.Collections

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.Scope
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.TruffleLanguage.Env
import com.oracle.truffle.api.dsl.NodeFactory
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.instrumentation.AllocationReporter
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.api.`object`.DynamicObject
import com.oracle.truffle.api.`object`.Layout
import com.oracle.truffle.api.`object`.Shape
import com.oracle.truffle.api.source.Source
import com.oracle.truffle.sl.SLLanguage
import com.oracle.truffle.sl.builtins.SLBuiltinNode
import com.oracle.truffle.sl.builtins.SLDefineFunctionBuiltinFactory
import com.oracle.truffle.sl.builtins.SLEvalBuiltinFactory
import com.oracle.truffle.sl.builtins.SLGetSizeBuiltinFactory
import com.oracle.truffle.sl.builtins.SLHasSizeBuiltinFactory
import com.oracle.truffle.sl.builtins.SLHelloEqualsWorldBuiltinFactory
import com.oracle.truffle.sl.builtins.SLImportBuiltinFactory
import com.oracle.truffle.sl.builtins.SLIsExecutableBuiltinFactory
import com.oracle.truffle.sl.builtins.SLIsNullBuiltinFactory
import com.oracle.truffle.sl.builtins.SLNanoTimeBuiltinFactory
import com.oracle.truffle.sl.builtins.SLNewObjectBuiltinFactory
import com.oracle.truffle.sl.builtins.SLPrintlnBuiltin
import com.oracle.truffle.sl.builtins.SLPrintlnBuiltinFactory
import com.oracle.truffle.sl.builtins.SLReadlnBuiltin
import com.oracle.truffle.sl.builtins.SLReadlnBuiltinFactory
import com.oracle.truffle.sl.builtins.SLStackTraceBuiltinFactory
import com.oracle.truffle.sl.nodes.SLExpressionNode
import com.oracle.truffle.sl.nodes.SLRootNode
import com.oracle.truffle.sl.nodes.local.SLReadArgumentNode

/**
 * The run-time state of SL during execution. The context is created by the [SLLanguage]. It
 * is used, for example, by [builtin functions][SLBuiltinNode.getContext].
 *
 *
 * It would be an error to have two different context instances during the execution of one script.
 * However, if two separate scripts run in one Java VM at the same time, they have a different
 * context. Therefore, the context is not a singleton.
 */
class SLContext(private val language: SLLanguage, env: TruffleLanguage.Env, externalBuiltins: List<NodeFactory<out SLBuiltinNode>>) {

    /**
     * Return the current Truffle environment.
     */
    val env: Env
    /**
     * Returns the default input, i.e., the source for the [SLReadlnBuiltin]. To allow unit
     * testing, we do not use [System. in] directly.
     */
    val input: BufferedReader
    /**
     * The default default, i.e., the output for the [SLPrintlnBuiltin]. To allow unit
     * testing, we do not use [System.out] directly.
     */
    val output: PrintWriter
    /**
     * Returns the registry of all functions that are currently defined.
     */
    val functionRegistry: SLFunctionRegistry
    private val emptyShape: Shape
    /*
     * Methods for object creation / object property access.
     */

    val allocationReporter: AllocationReporter
    val topScopes: Iterable<Scope> // Cache the top scopes

    /**
     * Returns an object that contains bindings that were exported across all used languages. To
     * read or write from this object the [interop][TruffleObject] API can be used.
     */
    val polyglotBindings: TruffleObject
        get() = env.polyglotBindings as TruffleObject

    init {
        this.env = env
        this.input = BufferedReader(InputStreamReader(env.`in`()))
        this.output = PrintWriter(env.out(), true)
        this.allocationReporter = env.lookup(AllocationReporter::class.java)
        this.functionRegistry = SLFunctionRegistry(language)
        this.topScopes = setOf<Scope>(Scope.newBuilder("global", functionRegistry.functionsObject).build())
        installBuiltins()
        for (builtin in externalBuiltins) {
            installBuiltin(builtin)
        }
        this.emptyShape = LAYOUT.createShape(SLObjectType.SINGLETON)
    }

    /**
     * Adds all builtin functions to the [SLFunctionRegistry]. This method lists all
     * [builtin implementation classes][SLBuiltinNode].
     */
    private fun installBuiltins() {
        installBuiltin(SLReadlnBuiltinFactory.getInstance())
        installBuiltin(SLPrintlnBuiltinFactory.getInstance())
        installBuiltin(SLNanoTimeBuiltinFactory.getInstance())
        installBuiltin(SLDefineFunctionBuiltinFactory.getInstance())
        installBuiltin(SLStackTraceBuiltinFactory.getInstance())
        installBuiltin(SLHelloEqualsWorldBuiltinFactory.getInstance())
        installBuiltin(SLNewObjectBuiltinFactory.getInstance())
        installBuiltin(SLEvalBuiltinFactory.getInstance())
        installBuiltin(SLImportBuiltinFactory.getInstance())
        installBuiltin(SLGetSizeBuiltinFactory.getInstance())
        installBuiltin(SLHasSizeBuiltinFactory.getInstance())
        installBuiltin(SLIsExecutableBuiltinFactory.getInstance())
        installBuiltin(SLIsNullBuiltinFactory.getInstance())
    }

    fun installBuiltin(factory: NodeFactory<out SLBuiltinNode>) {
        /*
         * The builtin node factory is a class that is automatically generated by the Truffle DSL.
         * The signature returned by the factory reflects the signature of the @Specialization
         *
         * methods in the builtin classes.
         */
        val argumentCount = factory.executionSignature.size
        val argumentNodes = arrayOfNulls<SLExpressionNode>(argumentCount)
        /*
         * Builtin functions are like normal functions, i.e., the arguments are passed in as an
         * Object[] array encapsulated in SLArguments. A SLReadArgumentNode extracts a parameter
         * from this array.
         */
        for (i in 0 until argumentCount) {
            argumentNodes[i] = SLReadArgumentNode(i)
        }
        /* Instantiate the builtin node. This node performs the actual functionality. */
        val builtinBodyNode = factory.createNode(argumentNodes as Any)
        builtinBodyNode.addRootTag()
        /* The name of the builtin function is specified via an annotation on the node class. */
        val name = lookupNodeInfo(builtinBodyNode.javaClass)!!.shortName()
        builtinBodyNode.setUnavailableSourceSection()

        /* Wrap the builtin in a RootNode. Truffle requires all AST to start with a RootNode. */
        val rootNode = SLRootNode(language, FrameDescriptor(), builtinBodyNode, BUILTIN_SOURCE.createUnavailableSection(), name)

        /* Register the builtin function in our function registry. */
        functionRegistry.register(name, Truffle.getRuntime().createCallTarget(rootNode))
    }

    /**
     * Allocate an empty object. All new objects initially have no properties. Properties are added
     * when they are first stored, i.e., the store triggers a shape change of the object.
     */
    fun createObject(): DynamicObject? {
        var `object`: DynamicObject? = null
        allocationReporter.onEnter(null, 0, AllocationReporter.SIZE_UNKNOWN)
        `object` = emptyShape.newInstance()
        allocationReporter.onReturnValue(`object`, 0, AllocationReporter.SIZE_UNKNOWN)
        return `object`
    }

    fun parse(source: Source): CallTarget {
        return env.parse(source)
    }

    companion object {

        private val BUILTIN_SOURCE = Source.newBuilder(SLLanguage.ID, "", "SL builtin").build()
        private val LAYOUT = Layout.createLayout()

        fun lookupNodeInfo(clazz: Class<*>?): NodeInfo? {
            if (clazz == null) {
                return null
            }
            val info = clazz.getAnnotation<NodeInfo>(NodeInfo::class.java!!)
            return info ?: lookupNodeInfo(clazz.superclass)
        }

        fun isSLObject(value: TruffleObject): Boolean {
            /*
         * LAYOUT.getType() returns a concrete implementation class, i.e., a class that is more
         * precise than the base class DynamicObject. This makes the type check faster.
         */
            return LAYOUT.type.isInstance(value) && LAYOUT.type.cast(value).shape.objectType === SLObjectType.SINGLETON
        }

        /*
     * Methods for language interoperability.
     */

        fun fromForeignValue(a: Any): Any {
            if (a is Long || a is SLBigNumber || a is String || a is Boolean) {
                return a
            } else if (a is Char) {
                return a.toString()
            } else if (a is Number) {
                return fromForeignNumber(a)
            } else if (a is TruffleObject) {
                return a
            } else if (a is SLContext) {
                return a
            }
            CompilerDirectives.transferToInterpreter()
            throw IllegalStateException(a.toString() + " is not a Truffle value")
        }

        @TruffleBoundary
        private fun fromForeignNumber(a: Any): Long {
            return (a as Number).toLong()
        }

        val current: SLContext
            get() = SLLanguage.currentContext
    }

}
