/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Objects

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.frame.Frame
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.interop.ForeignAccess
import com.oracle.truffle.api.interop.KeyInfo
import com.oracle.truffle.api.interop.Message
import com.oracle.truffle.api.interop.MessageResolution
import com.oracle.truffle.api.interop.Resolve
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.interop.UnknownIdentifierException
import com.oracle.truffle.api.interop.UnsupportedMessageException
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeUtil
import com.oracle.truffle.api.nodes.NodeVisitor
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.sl.nodes.SLEvalRootNode
import com.oracle.truffle.sl.nodes.SLStatementNode
import com.oracle.truffle.sl.nodes.controlflow.SLBlockNode
import com.oracle.truffle.sl.runtime.SLNull

/**
 * Simple language lexical scope. There can be a block scope, or function scope.
 */
class SLLexicalScope {

    private val current: Node?
    private val block: SLBlockNode?
    private val parentBlock: SLBlockNode?
    private val root: RootNode?
    private var parent: SLLexicalScope? = null
    private var varSlots: Map<String, FrameSlot>? = null

    /**
     * @return the function name for function scope, "block" otherwise.
     */
    val name: String
        get() = if (root != null) {
            root.name
        } else {
            "block"
        }

    /**
     * @return the node representing the scope, the block node for block scopes and the
     * [RootNode] for functional scope.
     */
    val node: Node?
        get() = root ?: block

    private// Provide the arguments only when the current node is above the block
    val vars: Map<String, FrameSlot>
        get() {
            if (varSlots == null) {
                if (current != null) {
                    varSlots = collectVars(block, current)
                } else if (block != null) {
                    varSlots = collectArgs(block)
                } else {
                    varSlots = emptyMap<String, FrameSlot>()
                }
            }
            return varSlots
        }

    /**
     * Create a new block SL lexical scope.
     *
     * @param current the current node
     * @param block a nearest block enclosing the current node
     * @param parentBlock a next parent block
     */
    private constructor(current: Node?, block: SLBlockNode?, parentBlock: SLBlockNode?) {
        this.current = current
        this.block = block
        this.parentBlock = parentBlock
        this.root = null
    }

    /**
     * Create a new functional SL lexical scope.
     *
     * @param current the current node, or `null` when it would be above the block
     * @param block a nearest block enclosing the current node
     * @param root a functional root node for top-most block
     */
    private constructor(current: Node, block: SLBlockNode, root: RootNode) {
        this.current = current
        this.block = block
        this.parentBlock = null
        this.root = root
    }

    fun findParent(): SLLexicalScope? {
        if (parentBlock == null) {
            // This was a root scope.
            return null
        }
        if (parent == null) {
            val node = block
            val newBlock = parentBlock
            // Test if there is a next parent block. If not, we're in the root scope.
            val newParentBlock = getParentBlock(newBlock)
            if (newParentBlock == null) {
                parent = SLLexicalScope(node!!, newBlock, newBlock.rootNode)
            } else {
                parent = SLLexicalScope(node!!, newBlock, newParentBlock)
            }
        }
        return parent
    }

    fun getVariables(frame: Frame?): Any {
        val vars = vars
        var args: Array<Any>? = null
        // Use arguments when the current node is above the block
        if (current == null) {
            args = frame?.arguments
        }
        return VariablesMapObject(vars, args, frame)
    }

    fun getArguments(frame: Frame?): Any? {
        if (root == null) {
            // No arguments for block scope
            return null
        }
        // The slots give us names of the arguments:
        val argSlots = collectArgs(block)
        // The frame's arguments array give us the argument values:
        val args = frame?.arguments
        // Create a TruffleObject having the arguments as properties:
        return VariablesMapObject(argSlots, args, frame)
    }

    private fun hasParentVar(name: String): Boolean {
        var p: SLLexicalScope? = this
        while ((p = p!!.findParent()) != null) {
            if (p!!.vars.containsKey(name)) {
                return true
            }
        }
        return false
    }

    private fun collectVars(varsBlock: Node?, currentNode: Node): Map<String, FrameSlot> {
        // Variables are slot-based.
        // To collect declared variables, traverse the block's AST and find slots associated
        // with SLWriteLocalVariableNode. The traversal stops when we hit the current node.
        val slots = LinkedHashMap<String, FrameSlot>(4)
        NodeUtil.forEachChild(varsBlock!!, object : NodeVisitor {
            override fun visit(node: Node): Boolean {
                if (node === currentNode) {
                    return false
                }
                // Do not enter any nested blocks.
                if (node !is SLBlockNode) {
                    val all = NodeUtil.forEachChild(node, this)
                    if (!all) {
                        return false
                    }
                }
                // Write to a variable is a declaration unless it exists already in a parent scope.
                if (node is SLWriteLocalVariableNode) {
                    val wn = node
                    val name = Objects.toString(wn.slot.identifier)
                    if (!hasParentVar(name)) {
                        slots[name] = wn.slot
                    }
                }
                return true
            }
        })
        return slots
    }

    internal class VariablesMapObject constructor(val slots: Map<String, FrameSlot>, val args: Array<Any>?, val frame: Frame?) : TruffleObject {

        override fun getForeignAccess(): ForeignAccess {
            return VariablesMapMessageResolutionForeign.ACCESS
        }

        @MessageResolution(receiverType = VariablesMapObject::class)
        internal class VariablesMapMessageResolution {

            @Resolve(message = "HAS_KEYS")
            internal abstract class VarsMapHasKeysNode : Node() {

                fun access(varMap: VariablesMapObject?): Any {
                    assert(varMap != null)
                    return true
                }
            }

            @Resolve(message = "KEYS")
            internal abstract class VarsMapKeysNode : Node() {

                @TruffleBoundary
                fun access(varMap: VariablesMapObject): Any {
                    return VariableNamesObject(varMap.slots.keys)
                }
            }

            @Resolve(message = "KEY_INFO")
            internal abstract class KeyInfoNode : Node() {

                @TruffleBoundary
                fun access(varMap: VariablesMapObject, name: String): Int {
                    if (varMap.frame == null) {
                        return KeyInfo.READABLE
                    }
                    val slot = varMap.slots[name]
                    return if (slot != null) {
                        KeyInfo.READABLE or KeyInfo.MODIFIABLE
                    } else KeyInfo.NONE
                }
            }

            @Resolve(message = "READ")
            internal abstract class VarsMapReadNode : Node() {

                @TruffleBoundary
                fun access(varMap: VariablesMapObject, name: String): Any {
                    if (varMap.frame == null) {
                        return SLNull.SINGLETON
                    }
                    val slot = varMap.slots[name]
                    if (slot == null) {
                        throw UnknownIdentifierException.raise(name)
                    } else {
                        val value: Any
                        val info = slot.info
                        if (varMap.args != null && info != null) {
                            value = varMap.args[info as Int]
                        } else {
                            value = varMap.frame.getValue(slot)
                        }
                        return value
                    }
                }
            }

            @Resolve(message = "WRITE")
            internal abstract class VarsMapWriteNode : Node() {

                @TruffleBoundary
                fun access(varMap: VariablesMapObject, name: String, value: Any): Any {
                    if (varMap.frame == null) {
                        throw UnsupportedMessageException.raise(Message.WRITE)
                    }
                    val slot = varMap.slots[name]
                    if (slot == null) {
                        throw UnknownIdentifierException.raise(name)
                    } else {
                        val info = slot.info
                        if (varMap.args != null && info != null) {
                            varMap.args[info as Int] = value
                        } else {
                            varMap.frame.setObject(slot, value)
                        }
                        return value
                    }
                }
            }
        }

        companion object {

            fun isInstance(obj: TruffleObject): Boolean {
                return obj is VariablesMapObject
            }
        }
    }

    internal class VariableNamesObject private constructor(names: Set<String>) : TruffleObject {

        val names: List<String>

        init {
            this.names = ArrayList(names)
        }

        override fun getForeignAccess(): ForeignAccess {
            return VariableNamesMessageResolutionForeign.ACCESS
        }

        @MessageResolution(receiverType = VariableNamesObject::class)
        internal class VariableNamesMessageResolution {

            @Resolve(message = "HAS_SIZE")
            internal abstract class VarNamesHasSizeNode : Node() {

                fun access(varNames: VariableNamesObject): Any {
                    return true
                }
            }

            @Resolve(message = "GET_SIZE")
            internal abstract class VarNamesGetSizeNode : Node() {

                fun access(varNames: VariableNamesObject): Any {
                    return varNames.names.size
                }
            }

            @Resolve(message = "READ")
            internal abstract class VarNamesReadNode : Node() {

                @TruffleBoundary
                fun access(varNames: VariableNamesObject, index: Int): Any {
                    try {
                        return varNames.names[index]
                    } catch (ioob: IndexOutOfBoundsException) {
                        throw UnknownIdentifierException.raise(Integer.toString(index))
                    }

                }
            }

        }

        companion object {

            fun isInstance(obj: TruffleObject): Boolean {
                return obj is VariableNamesObject
            }
        }
    }

    companion object {

        // The parameter node should not be assigned
        fun createScope(node: Node?): SLLexicalScope {
            var node = node
            var block = getParentBlock(node!!)
            if (block == null) {
                // We're in the root.
                block = findChildrenBlock(node)
                if (block == null) {
                    // Corrupted SL AST, no block was found
                    assert(node.rootNode is SLEvalRootNode) { "Corrupted SL AST under " + node!! }
                    return SLLexicalScope(null, null, null as SLBlockNode?)
                }
                node = null // node is above the block
            }
            // Test if there is a parent block. If not, we're in the root scope.
            val parentBlock = getParentBlock(block)
            return if (parentBlock == null) {
                SLLexicalScope(node, block, block.rootNode)
            } else {
                SLLexicalScope(node, block, parentBlock)
            }
        }

        private fun getParentBlock(node: Node): SLBlockNode? {
            val block: SLBlockNode?
            var parent: Node? = node.parent
            // Find a nearest block node.
            while (parent != null && parent !is SLBlockNode) {
                parent = parent.parent
            }
            if (parent != null) {
                block = parent as SLBlockNode?
            } else {
                block = null
            }
            return block
        }

        private fun findChildrenBlock(node: Node): SLBlockNode {
            val blockPtr = arrayOfNulls<SLBlockNode>(1)
            node.accept { n ->
                if (n is SLBlockNode) {
                    blockPtr[0] = n
                    false
                } else {
                    true
                }
            }
            return blockPtr[0]
        }

        private fun collectArgs(block: Node?): Map<String, FrameSlot> {
            // Arguments are pushed to frame slots at the beginning of the function block.
            // To collect argument slots, search for SLReadArgumentNode inside of
            // SLWriteLocalVariableNode.
            val args = LinkedHashMap<String, FrameSlot>(4)
            NodeUtil.forEachChild(block!!, object : NodeVisitor {

                private var wn: SLWriteLocalVariableNode? = null // The current write node containing a slot

                override fun visit(node: Node): Boolean {
                    // When there is a write node, search for SLReadArgumentNode among its children:
                    if (node is SLWriteLocalVariableNode) {
                        wn = node
                        val all = NodeUtil.forEachChild(node, this)
                        wn = null
                        return all
                    } else if (wn != null && node is SLReadArgumentNode) {
                        val slot = wn!!.slot
                        val name = Objects.toString(slot.identifier)
                        assert(!args.containsKey(name)) { "$name argument exists already." }
                        args[name] = slot
                        return true
                    } else return if (wn == null && node is SLStatementNode) {
                        // A different SL node - we're done.
                        false
                    } else {
                        NodeUtil.forEachChild(node, this)
                    }
                }
            })
            return args
        }
    }

}
