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
package com.oracle.truffle.sl.nodes

import java.io.File

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.dsl.ReportPolymorphism
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.instrumentation.GenerateWrapper
import com.oracle.truffle.api.instrumentation.InstrumentableNode
import com.oracle.truffle.api.instrumentation.ProbeNode
import com.oracle.truffle.api.instrumentation.StandardTags
import com.oracle.truffle.api.instrumentation.Tag
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.source.Source
import com.oracle.truffle.api.source.SourceSection

/**
 * The base class of all Truffle nodes for SL. All nodes (even expressions) can be used as
 * statements, i.e., without returning a value. The [VirtualFrame] provides access to the
 * local variables.
 */
@NodeInfo(language = "SL", description = "The abstract base node for all SL statements")
@GenerateWrapper
@ReportPolymorphism
abstract class SLStatementNode : Node(), InstrumentableNode {

    var sourceCharIndex = NO_SOURCE
        private set
    var sourceLength: Int = 0
        private set

    private var hasStatementTag: Boolean = false
    private var hasRootTag: Boolean = false

    val sourceEndIndex: Int
        get() = sourceCharIndex + sourceLength

    /*
     * The creation of source section can be implemented lazily by looking up the root node source
     * and then creating the source section object using the indices stored in the node. This avoids
     * the eager creation of source section objects during parsing and creates them only when they
     * are needed. Alternatively, if the language uses source sections to implement language
     * semantics, then it might be more efficient to eagerly create source sections and store it in
     * the AST.
     *
     * For more details see {@link InstrumentableNode}.
     */
    @TruffleBoundary
    override fun getSourceSection(): SourceSection? {
        if (sourceCharIndex == NO_SOURCE) {
            // AST node without source
            return null
        }
        val rootNode = rootNode
                ?: // not yet adopted yet
                return null
        val rootSourceSection = rootNode.sourceSection ?: return null
        val source = rootSourceSection.source
        return if (sourceCharIndex == UNAVAILABLE_SOURCE) {
            source.createUnavailableSection()
        } else {
            source.createSection(sourceCharIndex, sourceLength)
        }
    }

    fun hasSource(): Boolean {
        return sourceCharIndex != NO_SOURCE
    }

    override fun isInstrumentable(): Boolean {
        return hasSource()
    }

    // invoked by the parser to set the source
    fun setSourceSection(charIndex: Int, length: Int) {
        assert(sourceCharIndex == NO_SOURCE) { "source must only be set once" }
        if (charIndex < 0) {
            throw IllegalArgumentException("charIndex < 0")
        } else if (length < 0) {
            throw IllegalArgumentException("length < 0")
        }
        this.sourceCharIndex = charIndex
        this.sourceLength = length
    }

    fun setUnavailableSourceSection() {
        assert(sourceCharIndex == NO_SOURCE) { "source must only be set once" }
        this.sourceCharIndex = UNAVAILABLE_SOURCE
    }

    override fun hasTag(tag: Class<out Tag>?): Boolean {
        if (tag == StandardTags.StatementTag::class.java) {
            return hasStatementTag
        } else if (tag == StandardTags.RootTag::class.java) {
            return hasRootTag
        }
        return false
    }

    override fun createWrapper(probe: ProbeNode): InstrumentableNode.WrapperNode {
        return SLStatementNodeWrapper(this, probe)
    }

    /**
     * Execute this node as as statement, where no return value is necessary.
     */
    abstract fun executeVoid(frame: VirtualFrame)

    /**
     * Marks this node as being a [StandardTags.StatementTag] for instrumentation purposes.
     */
    fun addStatementTag() {
        hasStatementTag = true
    }

    /**
     * Marks this node as being a [StandardTags.RootTag] for instrumentation purposes.
     */
    fun addRootTag() {
        hasRootTag = true
    }

    override fun toString(): String {
        return formatSourceSection(this)
    }

    companion object {

        private val NO_SOURCE = -1
        private val UNAVAILABLE_SOURCE = -2

        /**
         * Formats a source section of a node in human readable form. If no source section could be
         * found it looks up the parent hierarchy until it finds a source section. Nodes where this was
         * required append a `'~'` at the end.
         *
         * @param node the node to format.
         * @return a formatted source section string
         */
        fun formatSourceSection(node: Node?): String {
            if (node == null) {
                return "<unknown>"
            }
            var section: SourceSection? = node.sourceSection
            var estimated = false
            if (section == null) {
                section = node.encapsulatingSourceSection
                estimated = true
            }

            if (section == null || section.source == null) {
                return "<unknown source>"
            } else {
                val sourceName = File(section.source.name).name
                val startLine = section.startLine
                return String.format("%s:%d%s", sourceName, startLine, if (estimated) "~" else "")
            }
        }
    }

}
