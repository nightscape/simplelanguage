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
package com.oracle.truffle.sl.parser

import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap

import com.oracle.truffle.api.frame.FrameSlotKind
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.Token

import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.source.Source
import com.oracle.truffle.api.source.SourceSection
import com.oracle.truffle.sl.SLLanguage
import com.oracle.truffle.sl.nodes.SLBinaryNode
import com.oracle.truffle.sl.nodes.SLExpressionNode
import com.oracle.truffle.sl.nodes.SLRootNode
import com.oracle.truffle.sl.nodes.SLStatementNode
import com.oracle.truffle.sl.nodes.access.SLReadPropertyNode
import com.oracle.truffle.sl.nodes.access.SLReadPropertyNodeGen
import com.oracle.truffle.sl.nodes.access.SLWritePropertyNode
import com.oracle.truffle.sl.nodes.access.SLWritePropertyNodeGen
import com.oracle.truffle.sl.nodes.call.SLInvokeNode
import com.oracle.truffle.sl.nodes.controlflow.SLBlockNode
import com.oracle.truffle.sl.nodes.controlflow.SLBreakNode
import com.oracle.truffle.sl.nodes.controlflow.SLContinueNode
import com.oracle.truffle.sl.nodes.controlflow.SLDebuggerNode
import com.oracle.truffle.sl.nodes.controlflow.SLFunctionBodyNode
import com.oracle.truffle.sl.nodes.controlflow.SLIfNode
import com.oracle.truffle.sl.nodes.controlflow.SLReturnNode
import com.oracle.truffle.sl.nodes.controlflow.SLWhileNode
import com.oracle.truffle.sl.nodes.expression.SLAddNodeGen
import com.oracle.truffle.sl.nodes.expression.SLBigIntegerLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLDivNodeGen
import com.oracle.truffle.sl.nodes.expression.SLEqualNodeGen
import com.oracle.truffle.sl.nodes.expression.SLFunctionLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLLessOrEqualNodeGen
import com.oracle.truffle.sl.nodes.expression.SLLessThanNodeGen
import com.oracle.truffle.sl.nodes.expression.SLLogicalAndNode
import com.oracle.truffle.sl.nodes.expression.SLLogicalNotNodeGen
import com.oracle.truffle.sl.nodes.expression.SLLogicalOrNode
import com.oracle.truffle.sl.nodes.expression.SLLongLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLMulNodeGen
import com.oracle.truffle.sl.nodes.expression.SLParenExpressionNode
import com.oracle.truffle.sl.nodes.expression.SLStringLiteralNode
import com.oracle.truffle.sl.nodes.expression.SLSubNodeGen
import com.oracle.truffle.sl.nodes.expression.SLUnboxNodeGen
import com.oracle.truffle.sl.nodes.local.SLReadArgumentNode
import com.oracle.truffle.sl.nodes.local.SLReadLocalVariableNode
import com.oracle.truffle.sl.nodes.local.SLReadLocalVariableNodeGen
import com.oracle.truffle.sl.nodes.local.SLWriteLocalVariableNode
import com.oracle.truffle.sl.nodes.local.SLWriteLocalVariableNodeGen

/**
 * Helper class used by the SL [Parser] to create nodes. The code is factored out of the
 * automatically generated parser to keep the attributed grammar of SL small.
 */
class SLNodeFactory(private val language: SLLanguage, /* State while parsing a source unit. */
                    private val source: Source) {
    private val allFunctions: MutableMap<String, RootCallTarget>

    /* State while parsing a function. */
    private var functionStartPos: Int = 0
    private var functionName: String? = null
    private var functionBodyStartPos: Int = 0 // includes parameter list
    private var parameterCount: Int = 0
    private var frameDescriptor: FrameDescriptor? = null
    private var methodNodes: MutableList<SLStatementNode>? = null

    /* State while parsing a block. */
    private var lexicalScope: LexicalScope? = null

    /**
     * Local variable names that are visible in the current block. Variables are not visible outside
     * of their defining block, to prevent the usage of undefined variables. Because of that, we can
     * decide during parsing if a name references a local variable or is a function name.
     */
    internal class LexicalScope(val outer: LexicalScope?) {
        val locals: MutableMap<String, FrameSlot>

        init {
            this.locals = HashMap()
            if (outer != null) {
                locals.putAll(outer.locals)
            }
        }
    }

    init {
        this.allFunctions = HashMap()
    }

    fun getAllFunctions(): Map<String, RootCallTarget> {
        return allFunctions
    }

    fun startFunction(nameToken: Token, bodyStartToken: Token) {
        assert(functionStartPos == 0)
        assert(functionName == null)
        assert(functionBodyStartPos == 0)
        assert(parameterCount == 0)
        assert(frameDescriptor == null)
        assert(lexicalScope == null)

        functionStartPos = nameToken.startIndex
        functionName = nameToken.text
        functionBodyStartPos = bodyStartToken.startIndex
        frameDescriptor = FrameDescriptor()
        methodNodes = ArrayList()
        startBlock()
    }

    fun addFormalParameter(nameToken: Token) {
        /*
         * Method parameters are assigned to local variables at the beginning of the method. This
         * ensures that accesses to parameters are specialized the same way as local variables are
         * specialized.
         */
        val readArg = SLReadArgumentNode(parameterCount)
        val assignment = createAssignment(createStringLiteral(nameToken, false), readArg, parameterCount)
        methodNodes!!.add(assignment!!)
        parameterCount++
    }

    fun finishFunction(bodyNode: SLStatementNode?) {
        if (bodyNode == null) {
            // a state update that would otherwise be performed by finishBlock
            lexicalScope = lexicalScope!!.outer
        } else {
            methodNodes!!.add(bodyNode)
            val bodyEndPos = bodyNode.sourceEndIndex
            val functionSrc = source.createSection(functionStartPos, bodyEndPos - functionStartPos)
            val methodBlock = finishBlock(methodNodes, functionBodyStartPos, bodyEndPos - functionBodyStartPos)
            assert(lexicalScope == null) { "Wrong scoping of blocks in parser" }

            val functionBodyNode = SLFunctionBodyNode(methodBlock)
            functionBodyNode.setSourceSection(functionSrc.charIndex, functionSrc.charLength)

            val rootNode = SLRootNode(language, frameDescriptor, functionBodyNode, functionSrc, functionName)
            allFunctions[functionName] = Truffle.getRuntime().createCallTarget(rootNode)
        }

        functionStartPos = 0
        functionName = null
        functionBodyStartPos = 0
        parameterCount = 0
        frameDescriptor = null
        lexicalScope = null
    }

    fun startBlock() {
        lexicalScope = LexicalScope(lexicalScope)
    }

    fun finishBlock(bodyNodes: List<SLStatementNode>, startPos: Int, length: Int): SLStatementNode? {
        lexicalScope = lexicalScope!!.outer

        if (containsNull(bodyNodes)) {
            return null
        }

        val flattenedNodes = ArrayList<SLStatementNode>(bodyNodes.size)
        flattenBlocks(bodyNodes, flattenedNodes)
        for (statement in flattenedNodes) {
            if (statement.hasSource() && !isHaltInCondition(statement)) {
                statement.addStatementTag()
            }
        }
        val blockNode = SLBlockNode(flattenedNodes.toTypedArray<SLStatementNode>())
        blockNode.setSourceSection(startPos, length)
        return blockNode
    }

    private fun isHaltInCondition(statement: SLStatementNode): Boolean {
        return statement is SLIfNode || statement is SLWhileNode
    }

    private fun flattenBlocks(bodyNodes: Iterable<SLStatementNode>, flattenedNodes: MutableList<SLStatementNode>) {
        for (n in bodyNodes) {
            if (n is SLBlockNode) {
                flattenBlocks(n.statements, flattenedNodes)
            } else {
                flattenedNodes.add(n)
            }
        }
    }

    /**
     * Returns an [SLDebuggerNode] for the given token.
     *
     * @param debuggerToken The token containing the debugger node's info.
     * @return A SLDebuggerNode for the given token.
     */
    internal fun createDebugger(debuggerToken: Token): SLStatementNode {
        val debuggerNode = SLDebuggerNode()
        srcFromToken(debuggerNode, debuggerToken)
        return debuggerNode
    }

    /**
     * Returns an [SLBreakNode] for the given token.
     *
     * @param breakToken The token containing the break node's info.
     * @return A SLBreakNode for the given token.
     */
    fun createBreak(breakToken: Token): SLStatementNode {
        val breakNode = SLBreakNode()
        srcFromToken(breakNode, breakToken)
        return breakNode
    }

    /**
     * Returns an [SLContinueNode] for the given token.
     *
     * @param continueToken The token containing the continue node's info.
     * @return A SLContinueNode built using the given token.
     */
    fun createContinue(continueToken: Token): SLStatementNode {
        val continueNode = SLContinueNode()
        srcFromToken(continueNode, continueToken)
        return continueNode
    }

    /**
     * Returns an [SLWhileNode] for the given parameters.
     *
     * @param whileToken The token containing the while node's info
     * @param conditionNode The conditional node for this while loop
     * @param bodyNode The body of the while loop
     * @return A SLWhileNode built using the given parameters. null if either conditionNode or
     * bodyNode is null.
     */
    fun createWhile(whileToken: Token, conditionNode: SLExpressionNode?, bodyNode: SLStatementNode?): SLStatementNode? {
        if (conditionNode == null || bodyNode == null) {
            return null
        }

        conditionNode.addStatementTag()
        val start = whileToken.startIndex
        val end = bodyNode.sourceEndIndex
        val whileNode = SLWhileNode(conditionNode, bodyNode)
        whileNode.setSourceSection(start, end - start)
        return whileNode
    }

    /**
     * Returns an [SLIfNode] for the given parameters.
     *
     * @param ifToken The token containing the if node's info
     * @param conditionNode The condition node of this if statement
     * @param thenPartNode The then part of the if
     * @param elsePartNode The else part of the if (null if no else part)
     * @return An SLIfNode for the given parameters. null if either conditionNode or thenPartNode is
     * null.
     */
    fun createIf(ifToken: Token, conditionNode: SLExpressionNode?, thenPartNode: SLStatementNode?, elsePartNode: SLStatementNode?): SLStatementNode? {
        if (conditionNode == null || thenPartNode == null) {
            return null
        }

        conditionNode.addStatementTag()
        val start = ifToken.startIndex
        val end = elsePartNode?.sourceEndIndex ?: thenPartNode.sourceEndIndex
        val ifNode = SLIfNode(conditionNode, thenPartNode, elsePartNode)
        ifNode.setSourceSection(start, end - start)
        return ifNode
    }

    /**
     * Returns an [SLReturnNode] for the given parameters.
     *
     * @param t The token containing the return node's info
     * @param valueNode The value of the return (null if not returning a value)
     * @return An SLReturnNode for the given parameters.
     */
    fun createReturn(t: Token, valueNode: SLExpressionNode?): SLStatementNode {
        val start = t.startIndex
        val length = if (valueNode == null) t.text.length else valueNode.sourceEndIndex - start
        val returnNode = SLReturnNode(valueNode)
        returnNode.setSourceSection(start, length)
        return returnNode
    }

    /**
     * Returns the corresponding subclass of [SLExpressionNode] for binary expressions.
     * These nodes are currently not instrumented.
     *
     * @param opToken The operator of the binary expression
     * @param leftNode The left node of the expression
     * @param rightNode The right node of the expression
     * @return A subclass of SLExpressionNode using the given parameters based on the given opToken.
     * null if either leftNode or rightNode is null.
     */
    fun createBinary(opToken: Token, leftNode: SLExpressionNode?, rightNode: SLExpressionNode?): SLExpressionNode? {
        if (leftNode == null || rightNode == null) {
            return null
        }
        val leftUnboxed: SLExpressionNode
        if (leftNode is SLBinaryNode) {  // SLBinaryNode never returns boxed value
            leftUnboxed = leftNode
        } else {
            leftUnboxed = SLUnboxNodeGen.create(leftNode)
        }
        val rightUnboxed: SLExpressionNode
        if (rightNode is SLBinaryNode) {  // SLBinaryNode never returns boxed value
            rightUnboxed = rightNode
        } else {
            rightUnboxed = SLUnboxNodeGen.create(rightNode)
        }

        val result: SLExpressionNode
        when (opToken.text) {
            "+" -> result = SLAddNodeGen.create(leftUnboxed, rightUnboxed)
            "*" -> result = SLMulNodeGen.create(leftUnboxed, rightUnboxed)
            "/" -> result = SLDivNodeGen.create(leftUnboxed, rightUnboxed)
            "-" -> result = SLSubNodeGen.create(leftUnboxed, rightUnboxed)
            "<" -> result = SLLessThanNodeGen.create(leftUnboxed, rightUnboxed)
            "<=" -> result = SLLessOrEqualNodeGen.create(leftUnboxed, rightUnboxed)
            ">" -> result = SLLogicalNotNodeGen.create(SLLessOrEqualNodeGen.create(leftUnboxed, rightUnboxed))
            ">=" -> result = SLLogicalNotNodeGen.create(SLLessThanNodeGen.create(leftUnboxed, rightUnboxed))
            "==" -> result = SLEqualNodeGen.create(leftUnboxed, rightUnboxed)
            "!=" -> result = SLLogicalNotNodeGen.create(SLEqualNodeGen.create(leftUnboxed, rightUnboxed))
            "&&" -> result = SLLogicalAndNode(leftUnboxed, rightUnboxed)
            "||" -> result = SLLogicalOrNode(leftUnboxed, rightUnboxed)
            else -> throw RuntimeException("unexpected operation: " + opToken.text)
        }

        val start = leftNode.sourceCharIndex
        val length = rightNode.sourceEndIndex - start
        result.setSourceSection(start, length)
        result.addExpressionTag()

        return result
    }

    /**
     * Returns an [SLInvokeNode] for the given parameters.
     *
     * @param functionNode The function being called
     * @param parameterNodes The parameters of the function call
     * @param finalToken A token used to determine the end of the sourceSelection for this call
     * @return An SLInvokeNode for the given parameters. null if functionNode or any of the
     * parameterNodes are null.
     */
    fun createCall(functionNode: SLExpressionNode?, parameterNodes: List<SLExpressionNode>, finalToken: Token): SLExpressionNode? {
        if (functionNode == null || containsNull(parameterNodes)) {
            return null
        }

        val result = SLInvokeNode(functionNode, parameterNodes.toTypedArray<SLExpressionNode>())

        val startPos = functionNode.sourceCharIndex
        val endPos = finalToken.startIndex + finalToken.text.length
        result.setSourceSection(startPos, endPos - startPos)
        result.addExpressionTag()

        return result
    }

    /**
     * Returns an [SLWriteLocalVariableNode] for the given parameters.
     *
     * @param nameNode The name of the variable being assigned
     * @param valueNode The value to be assigned
     * @param argumentIndex null or index of the argument the assignment is assigning
     * @return An SLExpressionNode for the given parameters. null if nameNode or valueNode is null.
     */
    @JvmOverloads
    fun createAssignment(nameNode: SLExpressionNode?, valueNode: SLExpressionNode?, argumentIndex: Int? = null): SLExpressionNode? {
        if (nameNode == null || valueNode == null) {
            return null
        }

        val name = (nameNode as SLStringLiteralNode).executeGeneric(null)
        val frameSlot = frameDescriptor!!.findOrAddFrameSlot(
                name,
                argumentIndex,
                FrameSlotKind.Illegal)
        lexicalScope!!.locals[name] = frameSlot
        val result = SLWriteLocalVariableNodeGen.create(valueNode, frameSlot)

        if (valueNode.hasSource()) {
            val start = nameNode.sourceCharIndex
            val length = valueNode.sourceEndIndex - start
            result.setSourceSection(start, length)
        }
        result.addExpressionTag()

        return result
    }

    /**
     * Returns a [SLReadLocalVariableNode] if this read is a local variable or a
     * [SLFunctionLiteralNode] if this read is global. In SL, the only global names are
     * functions.
     *
     * @param nameNode The name of the variable/function being read
     * @return either:
     *
     *  * A SLReadLocalVariableNode representing the local variable being read.
     *  * A SLFunctionLiteralNode representing the function definition.
     *  * null if nameNode is null.
     *
     */
    fun createRead(nameNode: SLExpressionNode?): SLExpressionNode? {
        if (nameNode == null) {
            return null
        }

        val name = (nameNode as SLStringLiteralNode).executeGeneric(null)
        val result: SLExpressionNode
        val frameSlot = lexicalScope!!.locals[name]
        if (frameSlot != null) {
            /* Read of a local variable. */
            result = SLReadLocalVariableNodeGen.create(frameSlot)
        } else {
            /* Read of a global name. In our language, the only global names are functions. */
            result = SLFunctionLiteralNode(language, name)
        }
        result.setSourceSection(nameNode.sourceCharIndex, nameNode.sourceLength)
        result.addExpressionTag()
        return result
    }

    fun createStringLiteral(literalToken: Token, removeQuotes: Boolean): SLExpressionNode {
        /* Remove the trailing and ending " */
        var literal = literalToken.text
        if (removeQuotes) {
            assert(literal.length >= 2 && literal.startsWith("\"") && literal.endsWith("\""))
            literal = literal.substring(1, literal.length - 1)
        }

        val result = SLStringLiteralNode(literal.intern())
        srcFromToken(result, literalToken)
        result.addExpressionTag()
        return result
    }

    fun createNumericLiteral(literalToken: Token): SLExpressionNode {
        var result: SLExpressionNode
        try {
            /* Try if the literal is small enough to fit into a long value. */
            result = SLLongLiteralNode(java.lang.Long.parseLong(literalToken.text))
        } catch (ex: NumberFormatException) {
            /* Overflow of long value, so fall back to BigInteger. */
            result = SLBigIntegerLiteralNode(BigInteger(literalToken.text))
        }

        srcFromToken(result, literalToken)
        result.addExpressionTag()
        return result
    }

    fun createParenExpression(expressionNode: SLExpressionNode?, start: Int, length: Int): SLExpressionNode? {
        if (expressionNode == null) {
            return null
        }

        val result = SLParenExpressionNode(expressionNode)
        result.setSourceSection(start, length)
        return result
    }

    /**
     * Returns an [SLReadPropertyNode] for the given parameters.
     *
     * @param receiverNode The receiver of the property access
     * @param nameNode The name of the property being accessed
     * @return An SLExpressionNode for the given parameters. null if receiverNode or nameNode is
     * null.
     */
    fun createReadProperty(receiverNode: SLExpressionNode?, nameNode: SLExpressionNode?): SLExpressionNode? {
        if (receiverNode == null || nameNode == null) {
            return null
        }

        val result = SLReadPropertyNodeGen.create(receiverNode, nameNode)

        val startPos = receiverNode.sourceCharIndex
        val endPos = nameNode.sourceEndIndex
        result.setSourceSection(startPos, endPos - startPos)
        result.addExpressionTag()

        return result
    }

    /**
     * Returns an [SLWritePropertyNode] for the given parameters.
     *
     * @param receiverNode The receiver object of the property assignment
     * @param nameNode The name of the property being assigned
     * @param valueNode The value to be assigned
     * @return An SLExpressionNode for the given parameters. null if receiverNode, nameNode or
     * valueNode is null.
     */
    fun createWriteProperty(receiverNode: SLExpressionNode?, nameNode: SLExpressionNode?, valueNode: SLExpressionNode?): SLExpressionNode? {
        if (receiverNode == null || nameNode == null || valueNode == null) {
            return null
        }

        val result = SLWritePropertyNodeGen.create(receiverNode, nameNode, valueNode)

        val start = receiverNode.sourceCharIndex
        val length = valueNode.sourceEndIndex - start
        result.setSourceSection(start, length)
        result.addExpressionTag()

        return result
    }

    /**
     * Creates source description of a single token.
     */
    private fun srcFromToken(node: SLStatementNode, token: Token) {
        node.setSourceSection(token.startIndex, token.text.length)
    }

    /**
     * Checks whether a list contains a null.
     */
    private fun containsNull(list: List<*>): Boolean {
        for (e in list) {
            if (e == null) {
                return true
            }
        }
        return false
    }

}
/**
 * Returns an [SLWriteLocalVariableNode] for the given parameters.
 *
 * @param nameNode The name of the variable being assigned
 * @param valueNode The value to be assigned
 * @return An SLExpressionNode for the given parameters. null if nameNode or valueNode is null.
 */
