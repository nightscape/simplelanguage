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
package com.oracle.truffle.sl

import java.util.ArrayList
import java.util.Collections
import java.util.NoSuchElementException

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.Scope
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy
import com.oracle.truffle.api.debug.DebuggerTags
import com.oracle.truffle.api.dsl.NodeFactory
import com.oracle.truffle.api.frame.Frame
import com.oracle.truffle.api.instrumentation.ProvidedTags
import com.oracle.truffle.api.instrumentation.StandardTags
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.`object`.DynamicObject
import com.oracle.truffle.api.source.Source
import com.oracle.truffle.api.source.SourceSection
import com.oracle.truffle.sl.builtins.SLBuiltinNode
import com.oracle.truffle.sl.builtins.SLDefineFunctionBuiltin
import com.oracle.truffle.sl.builtins.SLNanoTimeBuiltin
import com.oracle.truffle.sl.builtins.SLPrintlnBuiltin
import com.oracle.truffle.sl.builtins.SLReadlnBuiltin
import com.oracle.truffle.sl.builtins.SLStackTraceBuiltin
import com.oracle.truffle.sl.nodes.SLEvalRootNode
import com.oracle.truffle.sl.nodes.SLTypes
import com.oracle.truffle.sl.nodes.access.SLReadPropertyCacheNode
import com.oracle.truffle.sl.nodes.access.SLReadPropertyNode
import com.oracle.truffle.sl.nodes.access.SLWritePropertyCacheNode
import com.oracle.truffle.sl.nodes.access.SLWritePropertyNode
import com.oracle.truffle.sl.nodes.call.SLDispatchNode
import com.oracle.truffle.sl.nodes.call.SLInvokeNode
import com.oracle.truffle.sl.nodes.controlflow.SLBlockNode
import com.oracle.truffle.sl.nodes.controlflow.SLBreakNode
import com.oracle.truffle.sl.nodes.controlflow.SLContinueNode
import com.oracle.truffle.sl.nodes.controlflow.SLDebuggerNode
import com.oracle.truffle.sl.nodes.controlflow.SLIfNode
import com.oracle.truffle.sl.nodes.controlflow.SLReturnNode
import com.oracle.truffle.sl.nodes.controlflow.SLWhileNode
import com.oracle.truffle.sl.nodes.expression.SLAddNode
import com.oracle.truffle.sl.nodes.expression.SLBigIntegerLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLDivNode
import com.oracle.truffle.sl.nodes.expression.SLEqualNode
import com.oracle.truffle.sl.nodes.expression.SLFunctionLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLLessOrEqualNode
import com.oracle.truffle.sl.nodes.expression.SLLessThanNode
import com.oracle.truffle.sl.nodes.expression.SLLogicalAndNode
import com.oracle.truffle.sl.nodes.expression.SLLogicalOrNode
import com.oracle.truffle.sl.nodes.expression.SLMulNode
import com.oracle.truffle.sl.nodes.expression.SLStringLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLSubNode
import com.oracle.truffle.sl.nodes.local.SLLexicalScope
import com.oracle.truffle.sl.nodes.local.SLReadLocalVariableNode
import com.oracle.truffle.sl.nodes.local.SLWriteLocalVariableNode
import com.oracle.truffle.sl.parser.SLNodeFactory
import com.oracle.truffle.sl.parser.SimpleLanguageLexer
import com.oracle.truffle.sl.parser.SimpleLanguageParser
import com.oracle.truffle.sl.runtime.SLBigNumber
import com.oracle.truffle.sl.runtime.SLContext
import com.oracle.truffle.sl.runtime.SLFunction
import com.oracle.truffle.sl.runtime.SLFunctionRegistry
import com.oracle.truffle.sl.runtime.SLNull

/**
 * SL is a simple language to demonstrate and showcase features of Truffle. The implementation is as
 * simple and clean as possible in order to help understanding the ideas and concepts of Truffle.
 * The language has first class functions, and objects are key-value stores.
 *
 *
 * SL is dynamically typed, i.e., there are no type names specified by the programmer. SL is
 * strongly typed, i.e., there is no automatic conversion between types. If an operation is not
 * available for the types encountered at run time, a type error is reported and execution is
 * stopped. For example, `4 - "2"` results in a type error because subtraction is only defined
 * for numbers.
 *
 *
 *
 * **Types:**
 *
 *  * Number: arbitrary precision integer numbers. The implementation uses the Java primitive type
 * `long` to represent numbers that fit into the 64 bit range, and [SLBigNumber] for
 * numbers that exceed the range. Using a primitive type such as `long` is crucial for
 * performance.
 *  * Boolean: implemented as the Java primitive type `boolean`.
 *  * String: implemented as the Java standard type [String].
 *  * Function: implementation type [SLFunction].
 *  * Object: efficient implementation using the object model provided by Truffle. The
 * implementation type of objects is a subclass of [DynamicObject].
 *  * Null (with only one value `null`): implemented as the singleton
 * [SLNull.SINGLETON].
 *
 * The class [SLTypes] lists these types for the Truffle DSL, i.e., for type-specialized
 * operations that are specified using Truffle DSL annotations.
 *
 *
 *
 * **Language concepts:**
 *
 *  * Literals for [numbers][SLBigIntegerLiteralNode] , [strings][SLStringLiteralNode],
 * and [functions][SLFunctionLiteralNode].
 *  * Basic arithmetic, logical, and comparison operations: [+][SLAddNode], [ -][SLSubNode], [*][SLMulNode], [/][SLDivNode], [logical and][SLLogicalAndNode],
 * [logical or][SLLogicalOrNode], [==][SLEqualNode], !=, [&amp;lt;][SLLessThanNode],
 * [&amp;le;][SLLessOrEqualNode], &gt;, .
 *  * Local variables: local variables must be defined (via a [ write][SLWriteLocalVariableNode]) before they can be used (by a [read][SLReadLocalVariableNode]). Local variables are
 * not visible outside of the block where they were first defined.
 *  * Basic control flow statements: [blocks][SLBlockNode], [if][SLIfNode],
 * [while][SLWhileNode] with [break][SLBreakNode] and [continue][SLContinueNode],
 * [return][SLReturnNode].
 *  * Debugging control: [debugger][SLDebuggerNode] statement uses
 * [DebuggerTags.AlwaysHalt] tag to halt the execution when run under the debugger.
 *  * Function calls: [invocations][SLInvokeNode] are efficiently implemented with
 * [polymorphic inline caches][SLDispatchNode].
 *  * Object access: [SLReadPropertyNode] uses [SLReadPropertyCacheNode] as the
 * polymorphic inline cache for property reads. [SLWritePropertyNode] uses
 * [SLWritePropertyCacheNode] as the polymorphic inline cache for property writes.
 *
 *
 *
 *
 * **Syntax and parsing:**<br></br>
 * The syntax is described as an attributed grammar. The [SimpleLanguageParser] and
 * [SimpleLanguageLexer] are automatically generated by ANTLR 4. The grammar contains semantic
 * actions that build the AST for a method. To keep these semantic actions short, they are mostly
 * calls to the [SLNodeFactory] that performs the actual node creation. All functions found in
 * the SL source are added to the [SLFunctionRegistry], which is accessible from the
 * [SLContext].
 *
 *
 *
 * **Builtin functions:**<br></br>
 * Library functions that are available to every SL source without prior definition are called
 * builtin functions. They are added to the [SLFunctionRegistry] when the [SLContext] is
 * created. Some of the current builtin functions are
 *
 *  * [readln][SLReadlnBuiltin]: Read a String from the [standard][SLContext.getInput].
 *  * [println][SLPrintlnBuiltin]: Write a value to the [standard][SLContext.getOutput].
 *  * [nanoTime][SLNanoTimeBuiltin]: Returns the value of a high-resolution time, in
 * nanoseconds.
 *  * [defineFunction][SLDefineFunctionBuiltin]: Parses the functions provided as a String
 * argument and adds them to the function registry. Functions that are already defined are replaced
 * with the new version.
 *  * [stckTrace][SLStackTraceBuiltin]: Print all function activations with all local
 * variables.
 *
 */
@TruffleLanguage.Registration(id = SLLanguage.ID, name = "SL", defaultMimeType = SLLanguage.MIME_TYPE, characterMimeTypes = arrayOf(SLLanguage.MIME_TYPE), contextPolicy = ContextPolicy.SHARED)
@ProvidedTags(StandardTags.CallTag::class, StandardTags.StatementTag::class, StandardTags.RootTag::class, StandardTags.ExpressionTag::class, DebuggerTags.AlwaysHalt::class)
class SLLanguage : TruffleLanguage<SLContext>() {
    init {
        counter++
    }

    override fun createContext(env: TruffleLanguage.Env): SLContext {
        return SLContext(this, env, ArrayList(EXTERNAL_BUILTINS))
    }

    @Throws(Exception::class)
    override fun parse(request: TruffleLanguage.ParsingRequest): CallTarget {
        val source = request.source
        val functions: Map<String, RootCallTarget>
        /*
         * Parse the provided source. At this point, we do not have a SLContext yet. Registration of
         * the functions with the SLContext happens lazily in SLEvalRootNode.
         */
        if (request.argumentNames.isEmpty()) {
            functions = SimpleLanguageParser.parseSL(this, source)
        } else {
            val sb = StringBuilder()
            sb.append("function main(")
            var sep = ""
            for (argumentName in request.argumentNames) {
                sb.append(sep)
                sb.append(argumentName)
                sep = ","
            }
            sb.append(") { return ")
            sb.append(source.characters)
            sb.append(";}")
            val language = if (source.language == null) ID else source.language
            val decoratedSource = Source.newBuilder(language, sb.toString(), source.name).build()
            functions = SimpleLanguageParser.parseSL(this, decoratedSource)
        }

        val main = functions["main"]
        val evalMain: RootNode
        if (main != null) {
            /*
             * We have a main function, so "evaluating" the parsed source means invoking that main
             * function. However, we need to lazily register functions into the SLContext first, so
             * we cannot use the original SLRootNode for the main function. Instead, we create a new
             * SLEvalRootNode that does everything we need.
             */
            evalMain = SLEvalRootNode(this, main, functions)
        } else {
            /*
             * Even without a main function, "evaluating" the parsed source needs to register the
             * functions into the SLContext.
             */
            evalMain = SLEvalRootNode(this, null, functions)
        }
        return Truffle.getRuntime().createCallTarget(evalMain)
    }

    /*
     * Still necessary for the old SL TCK to pass. We should remove with the old TCK. New language
     * should not override this.
     */
    override fun findExportedSymbol(context: SLContext, globalName: String?, onlyExplicit: Boolean): Any? {
        return context.functionRegistry.lookup(globalName!!, false)
    }

    override fun isVisible(context: SLContext?, value: Any?): Boolean {
        return value !== SLNull.SINGLETON
    }

    override fun isObjectOfLanguage(`object`: Any): Boolean {
        if (`object` !is TruffleObject) {
            return false
        }
        val truffleObject = `object`
        return truffleObject is SLFunction || truffleObject is SLBigNumber || SLContext.isSLObject(truffleObject)
    }

    override fun toString(context: SLContext?, value: Any?): String {
        if (value === SLNull.SINGLETON) {
            return "NULL"
        }
        if (value is SLBigNumber) {
            return super.toString(context, value.value)
        }
        return if (value is Long) {
            java.lang.Long.toString((value as Long?)!!)
        } else super.toString(context, value)
    }

    override fun findMetaObject(context: SLContext?, value: Any?): Any {
        if (value is Number || value is SLBigNumber) {
            return "Number"
        }
        if (value is Boolean) {
            return "Boolean"
        }
        if (value is String) {
            return "String"
        }
        if (value === SLNull.SINGLETON) {
            return "Null"
        }
        return if (value is SLFunction) {
            "Function"
        } else "Object"
    }

    override fun findSourceLocation(context: SLContext?, value: Any?): SourceSection? {
        if (value is SLFunction) {
            val f = value as SLFunction?
            return f!!.getCallTarget()!!.rootNode.sourceSection
        }
        return null
    }

    public override fun findLocalScopes(context: SLContext?, node: Node, frame: Frame): Iterable<Scope> {
        val scope = SLLexicalScope.createScope(node)
        return object : Iterable<Scope> {
            override fun iterator(): Iterator<Scope> {
                return object : Iterator<Scope> {
                    private var previousScope: SLLexicalScope? = null
                    private var nextScope: SLLexicalScope? = scope

                    override fun hasNext(): Boolean {
                        if (nextScope == null) {
                            nextScope = previousScope!!.findParent()
                        }
                        return nextScope != null
                    }

                    override fun next(): Scope {
                        if (!hasNext()) {
                            throw NoSuchElementException()
                        }
                        val vscope = Scope.newBuilder(nextScope!!.name, nextScope!!.getVariables(frame)).node(nextScope!!.node).arguments(nextScope!!.getArguments(frame)).build()
                        previousScope = nextScope
                        nextScope = null
                        return vscope
                    }
                }
            }
        }
    }

    override fun findTopScopes(context: SLContext): Iterable<Scope> {
        return context.topScopes
    }

    companion object {
        @Volatile
        var counter: Int = 0

        const val ID = "sl"
        const val MIME_TYPE = "application/x-sl"

        val currentContext: SLContext
            get() = TruffleLanguage.getCurrentContext<SLContext, SLLanguage>(SLLanguage::class.java)

        private val EXTERNAL_BUILTINS = Collections.synchronizedList(ArrayList<NodeFactory<out SLBuiltinNode>>())

        fun installBuiltin(builtin: NodeFactory<out SLBuiltinNode>) {
            EXTERNAL_BUILTINS.add(builtin)
        }
    }

}
