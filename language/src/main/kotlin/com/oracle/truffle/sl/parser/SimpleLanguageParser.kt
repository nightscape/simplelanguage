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
// Checkstyle: stop
//@formatter:off
package com.oracle.truffle.sl.parser

// DO NOT MODIFY - generated from SimpleLanguage.g4 using "mx create-sl-parser"

import java.util.ArrayList

import com.oracle.truffle.api.source.Source
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.sl.SLLanguage
import com.oracle.truffle.sl.nodes.SLExpressionNode
import com.oracle.truffle.sl.nodes.SLRootNode
import com.oracle.truffle.sl.nodes.SLStatementNode
import com.oracle.truffle.sl.parser.SLParseError

import org.antlr.v4.runtime.atn.*
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.*
import org.antlr.v4.runtime.tree.*

class SimpleLanguageParser(input: TokenStream) : Parser(input) {


    private var factory: SLNodeFactory? = null
    private var source: Source? = null

    @Deprecated("")
    override fun getTokenNames(): Array<String> {
        return tokenNames
    }

    override fun getVocabulary(): Vocabulary {
        return VOCABULARY
    }

    override fun getGrammarFileName(): String {
        return "SimpleLanguage.g4"
    }

    override fun getRuleNames(): Array<String> {
        return ruleNames
    }

    override fun getSerializedATN(): String {
        return _serializedATN
    }

    override fun getATN(): ATN {
        return _ATN
    }

    private class BailoutErrorListener internal constructor(private val source: Source) : BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            val location = "-- line " + line + " col " + (charPositionInLine + 1) + ": "
            throw SLParseError(source, line, charPositionInLine + 1, if (offendingSymbol == null) 1 else (offendingSymbol as Token).text.length, String.format("Error(s) parsing script:%n$location$msg"))
        }
    }

    fun SemErr(token: Token?, message: String) {
        val col = token!!.charPositionInLine + 1
        val location = "-- line " + token.line + " col " + col + ": "
        throw SLParseError(source, token.line, col, token.text.length, String.format("Error(s) parsing script:%n$location$message"))
    }

    init {
        _interp = ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache)
    }

    class SimplelanguageContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun function(): List<FunctionContext> {
            return getRuleContexts(FunctionContext::class.java)
        }

        fun function(i: Int): FunctionContext {
            return getRuleContext(FunctionContext::class.java, i)
        }

        fun EOF(): TerminalNode {
            return getToken(SimpleLanguageParser.EOF, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_simplelanguage
        }
    }

    @Throws(RecognitionException::class)
    fun simplelanguage(): SimplelanguageContext {
        val _localctx = SimplelanguageContext(_ctx, state)
        enterRule(_localctx, 0, RULE_simplelanguage)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 28
                function()
                state = 32
                _errHandler.sync(this)
                _la = _input.LA(1)
                while (_la == T__0) {
                    run {
                        run {
                            state = 29
                            function()
                        }
                    }
                    state = 34
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                }
                state = 35
                match(Recognizer.EOF)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FunctionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var IDENTIFIER: Token? = null
        var s: Token? = null
        var body: BlockContext? = null
        fun IDENTIFIER(): List<TerminalNode> {
            return getTokens(SimpleLanguageParser.IDENTIFIER)
        }

        fun IDENTIFIER(i: Int): TerminalNode {
            return getToken(SimpleLanguageParser.IDENTIFIER, i)
        }

        fun block(): BlockContext {
            return getRuleContext(BlockContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_function
        }
    }

    @Throws(RecognitionException::class)
    fun function(): FunctionContext {
        val _localctx = FunctionContext(_ctx, state)
        enterRule(_localctx, 2, RULE_function)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 37
                match(T__0)
                state = 38
                _localctx.IDENTIFIER = match(IDENTIFIER)
                state = 39
                _localctx.s = match(T__1)
                factory!!.startFunction(_localctx.IDENTIFIER, _localctx.s)
                state = 51
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == IDENTIFIER) {
                    run {
                        state = 41
                        _localctx.IDENTIFIER = match(IDENTIFIER)
                        factory!!.addFormalParameter(_localctx.IDENTIFIER)
                        state = 48
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        while (_la == T__2) {
                            run {
                                run {
                                    state = 43
                                    match(T__2)
                                    state = 44
                                    _localctx.IDENTIFIER = match(IDENTIFIER)
                                    factory!!.addFormalParameter(_localctx.IDENTIFIER)
                                }
                            }
                            state = 50
                            _errHandler.sync(this)
                            _la = _input.LA(1)
                        }
                    }
                }

                state = 53
                match(T__3)
                state = 54
                _localctx.body = block(false)
                factory!!.finishFunction(_localctx.body!!.result)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class BlockContext : ParserRuleContext {
        var inLoop: Boolean = false
        var result: SLStatementNode? = null
        var s: Token? = null
        var statement: StatementContext? = null
        var e: Token? = null
        fun statement(): List<StatementContext> {
            return getRuleContexts(StatementContext::class.java)
        }

        fun statement(i: Int): StatementContext {
            return getRuleContext(StatementContext::class.java, i)
        }

        constructor(parent: ParserRuleContext, invokingState: Int) : super(parent, invokingState) {}
        constructor(parent: ParserRuleContext, invokingState: Int, inLoop: Boolean) : super(parent, invokingState) {
            this.inLoop = inLoop
        }

        override fun getRuleIndex(): Int {
            return RULE_block
        }
    }

    @Throws(RecognitionException::class)
    fun block(inLoop: Boolean): BlockContext {
        val _localctx = BlockContext(_ctx, state, inLoop)
        enterRule(_localctx, 4, RULE_block)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                factory!!.startBlock()
                val body = ArrayList<SLStatementNode>()
                state = 58
                _localctx.s = match(T__4)
                state = 64
                _errHandler.sync(this)
                _la = _input.LA(1)
                while (_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__1 or (1L shl T__6) or (1L shl T__8) or (1L shl T__9) or (1L shl T__10) or (1L shl T__11) or (1L shl T__13) or (1L shl IDENTIFIER) or (1L shl STRING_LITERAL) or (1L shl NUMERIC_LITERAL)) != 0L) {
                    run {
                        run {
                            state = 59
                            _localctx.statement = statement(inLoop)
                            body.add(_localctx.statement!!.result)
                        }
                    }
                    state = 66
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                }
                state = 67
                _localctx.e = match(T__5)
                _localctx.result = factory!!.finishBlock(body, _localctx.s!!.startIndex, _localctx.e!!.stopIndex - _localctx.s!!.startIndex + 1)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class StatementContext : ParserRuleContext {
        var inLoop: Boolean = false
        var result: SLStatementNode? = null
        var while_statement: While_statementContext? = null
        var b: Token? = null
        var c: Token? = null
        var if_statement: If_statementContext? = null
        var return_statement: Return_statementContext? = null
        var expression: ExpressionContext? = null
        var d: Token? = null
        fun while_statement(): While_statementContext {
            return getRuleContext(While_statementContext::class.java, 0)
        }

        fun if_statement(): If_statementContext {
            return getRuleContext(If_statementContext::class.java, 0)
        }

        fun return_statement(): Return_statementContext {
            return getRuleContext(Return_statementContext::class.java, 0)
        }

        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        constructor(parent: ParserRuleContext, invokingState: Int) : super(parent, invokingState) {}
        constructor(parent: ParserRuleContext, invokingState: Int, inLoop: Boolean) : super(parent, invokingState) {
            this.inLoop = inLoop
        }

        override fun getRuleIndex(): Int {
            return RULE_statement
        }
    }

    @Throws(RecognitionException::class)
    fun statement(inLoop: Boolean): StatementContext {
        val _localctx = StatementContext(_ctx, state, inLoop)
        enterRule(_localctx, 6, RULE_statement)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 92
                _errHandler.sync(this)
                when (_input.LA(1)) {
                    T__10 -> {
                        state = 70
                        _localctx.while_statement = while_statement()
                        _localctx.result = _localctx.while_statement!!.result
                    }
                    T__6 -> {
                        state = 73
                        _localctx.b = match(T__6)
                        if (inLoop) {
                            _localctx.result = factory!!.createBreak(_localctx.b)
                        } else {
                            SemErr(_localctx.b, "break used outside of loop")
                        }
                        state = 75
                        match(T__7)
                    }
                    T__8 -> {
                        state = 76
                        _localctx.c = match(T__8)
                        if (inLoop) {
                            _localctx.result = factory!!.createContinue(_localctx.c)
                        } else {
                            SemErr(_localctx.c, "continue used outside of loop")
                        }
                        state = 78
                        match(T__7)
                    }
                    T__11 -> {
                        state = 79
                        _localctx.if_statement = if_statement(inLoop)
                        _localctx.result = _localctx.if_statement!!.result
                    }
                    T__13 -> {
                        state = 82
                        _localctx.return_statement = return_statement()
                        _localctx.result = _localctx.return_statement!!.result
                    }
                    T__1, IDENTIFIER, STRING_LITERAL, NUMERIC_LITERAL -> {
                        state = 85
                        _localctx.expression = expression()
                        state = 86
                        match(T__7)
                        _localctx.result = _localctx.expression!!.result
                    }
                    T__9 -> {
                        state = 89
                        _localctx.d = match(T__9)
                        _localctx.result = factory!!.createDebugger(_localctx.d)
                        state = 91
                        match(T__7)
                    }
                    else -> throw NoViableAltException(this)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class While_statementContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLStatementNode? = null
        var w: Token? = null
        var condition: ExpressionContext? = null
        var body: BlockContext? = null
        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        fun block(): BlockContext {
            return getRuleContext(BlockContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_while_statement
        }
    }

    @Throws(RecognitionException::class)
    fun while_statement(): While_statementContext {
        val _localctx = While_statementContext(_ctx, state)
        enterRule(_localctx, 8, RULE_while_statement)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 94
                _localctx.w = match(T__10)
                state = 95
                match(T__1)
                state = 96
                _localctx.condition = expression()
                state = 97
                match(T__3)
                state = 98
                _localctx.body = block(true)
                _localctx.result = factory!!.createWhile(_localctx.w, _localctx.condition!!.result, _localctx.body!!.result)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class If_statementContext : ParserRuleContext {
        var inLoop: Boolean = false
        var result: SLStatementNode? = null
        var i: Token? = null
        var condition: ExpressionContext? = null
        var then: BlockContext? = null
        var block: BlockContext? = null
        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        fun block(): List<BlockContext> {
            return getRuleContexts(BlockContext::class.java)
        }

        fun block(i: Int): BlockContext {
            return getRuleContext(BlockContext::class.java, i)
        }

        constructor(parent: ParserRuleContext, invokingState: Int) : super(parent, invokingState) {}
        constructor(parent: ParserRuleContext, invokingState: Int, inLoop: Boolean) : super(parent, invokingState) {
            this.inLoop = inLoop
        }

        override fun getRuleIndex(): Int {
            return RULE_if_statement
        }
    }

    @Throws(RecognitionException::class)
    fun if_statement(inLoop: Boolean): If_statementContext {
        val _localctx = If_statementContext(_ctx, state, inLoop)
        enterRule(_localctx, 10, RULE_if_statement)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 101
                _localctx.i = match(T__11)
                state = 102
                match(T__1)
                state = 103
                _localctx.condition = expression()
                state = 104
                match(T__3)
                state = 105
                _localctx.block = block(inLoop)
                _localctx.then = _localctx.block
                var elsePart: SLStatementNode? = null
                state = 111
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__12) {
                    run {
                        state = 107
                        match(T__12)
                        state = 108
                        _localctx.block = block(inLoop)
                        elsePart = _localctx.block!!.result
                    }
                }

                _localctx.result = factory!!.createIf(_localctx.i, _localctx.condition!!.result, _localctx.then!!.result, elsePart)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class Return_statementContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLStatementNode? = null
        var r: Token? = null
        var expression: ExpressionContext? = null
        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_return_statement
        }
    }

    @Throws(RecognitionException::class)
    fun return_statement(): Return_statementContext {
        val _localctx = Return_statementContext(_ctx, state)
        enterRule(_localctx, 12, RULE_return_statement)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 115
                _localctx.r = match(T__13)
                var value: SLExpressionNode? = null
                state = 120
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__1 or (1L shl IDENTIFIER) or (1L shl STRING_LITERAL) or (1L shl NUMERIC_LITERAL)) != 0L) {
                    run {
                        state = 117
                        _localctx.expression = expression()
                        value = _localctx.expression!!.result
                    }
                }

                _localctx.result = factory!!.createReturn(_localctx.r, value)
                state = 123
                match(T__7)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ExpressionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var logic_term: Logic_termContext? = null
        var op: Token? = null
        fun logic_term(): List<Logic_termContext> {
            return getRuleContexts(Logic_termContext::class.java)
        }

        fun logic_term(i: Int): Logic_termContext {
            return getRuleContext(Logic_termContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_expression
        }
    }

    @Throws(RecognitionException::class)
    fun expression(): ExpressionContext {
        val _localctx = ExpressionContext(_ctx, state)
        enterRule(_localctx, 14, RULE_expression)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                state = 125
                _localctx.logic_term = logic_term()
                _localctx.result = _localctx.logic_term!!.result
                state = 133
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 7, _ctx)
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        run {
                            run {
                                state = 127
                                _localctx.op = match(T__14)
                                state = 128
                                _localctx.logic_term = logic_term()
                                _localctx.result = factory!!.createBinary(_localctx.op, _localctx.result, _localctx.logic_term!!.result)
                            }
                        }
                    }
                    state = 135
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 7, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class Logic_termContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var logic_factor: Logic_factorContext? = null
        var op: Token? = null
        fun logic_factor(): List<Logic_factorContext> {
            return getRuleContexts(Logic_factorContext::class.java)
        }

        fun logic_factor(i: Int): Logic_factorContext {
            return getRuleContext(Logic_factorContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_logic_term
        }
    }

    @Throws(RecognitionException::class)
    fun logic_term(): Logic_termContext {
        val _localctx = Logic_termContext(_ctx, state)
        enterRule(_localctx, 16, RULE_logic_term)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                state = 136
                _localctx.logic_factor = logic_factor()
                _localctx.result = _localctx.logic_factor!!.result
                state = 144
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 8, _ctx)
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        run {
                            run {
                                state = 138
                                _localctx.op = match(T__15)
                                state = 139
                                _localctx.logic_factor = logic_factor()
                                _localctx.result = factory!!.createBinary(_localctx.op, _localctx.result, _localctx.logic_factor!!.result)
                            }
                        }
                    }
                    state = 146
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 8, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class Logic_factorContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var arithmetic: ArithmeticContext? = null
        var op: Token? = null
        fun arithmetic(): List<ArithmeticContext> {
            return getRuleContexts(ArithmeticContext::class.java)
        }

        fun arithmetic(i: Int): ArithmeticContext {
            return getRuleContext(ArithmeticContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_logic_factor
        }
    }

    @Throws(RecognitionException::class)
    fun logic_factor(): Logic_factorContext {
        val _localctx = Logic_factorContext(_ctx, state)
        enterRule(_localctx, 18, RULE_logic_factor)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 147
                _localctx.arithmetic = arithmetic()
                _localctx.result = _localctx.arithmetic!!.result
                state = 153
                _errHandler.sync(this)
                when (interpreter.adaptivePredict(_input, 9, _ctx)) {
                    1 -> {
                        state = 149
                        _localctx.op = _input.LT(1)
                        _la = _input.LA(1)
                        if (!(_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__16 or (1L shl T__17) or (1L shl T__18) or (1L shl T__19) or (1L shl T__20) or (1L shl T__21)) != 0L)) {
                            _localctx.op = _errHandler.recoverInline(this)
                        } else {
                            if (_input.LA(1) == Token.EOF) matchedEOF = true
                            _errHandler.reportMatch(this)
                            consume()
                        }
                        state = 150
                        _localctx.arithmetic = arithmetic()
                        _localctx.result = factory!!.createBinary(_localctx.op, _localctx.result, _localctx.arithmetic!!.result)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArithmeticContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var term: TermContext? = null
        var op: Token? = null
        fun term(): List<TermContext> {
            return getRuleContexts(TermContext::class.java)
        }

        fun term(i: Int): TermContext {
            return getRuleContext(TermContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_arithmetic
        }
    }

    @Throws(RecognitionException::class)
    fun arithmetic(): ArithmeticContext {
        val _localctx = ArithmeticContext(_ctx, state)
        enterRule(_localctx, 20, RULE_arithmetic)
        var _la: Int
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                state = 155
                _localctx.term = term()
                _localctx.result = _localctx.term!!.result
                state = 163
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 10, _ctx)
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        run {
                            run {
                                state = 157
                                _localctx.op = _input.LT(1)
                                _la = _input.LA(1)
                                if (!(_la == T__22 || _la == T__23)) {
                                    _localctx.op = _errHandler.recoverInline(this)
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true
                                    _errHandler.reportMatch(this)
                                    consume()
                                }
                                state = 158
                                _localctx.term = term()
                                _localctx.result = factory!!.createBinary(_localctx.op, _localctx.result, _localctx.term!!.result)
                            }
                        }
                    }
                    state = 165
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 10, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class TermContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var factor: FactorContext? = null
        var op: Token? = null
        fun factor(): List<FactorContext> {
            return getRuleContexts(FactorContext::class.java)
        }

        fun factor(i: Int): FactorContext {
            return getRuleContext(FactorContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_term
        }
    }

    @Throws(RecognitionException::class)
    fun term(): TermContext {
        val _localctx = TermContext(_ctx, state)
        enterRule(_localctx, 22, RULE_term)
        var _la: Int
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                state = 166
                _localctx.factor = factor()
                _localctx.result = _localctx.factor!!.result
                state = 174
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 11, _ctx)
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        run {
                            run {
                                state = 168
                                _localctx.op = _input.LT(1)
                                _la = _input.LA(1)
                                if (!(_la == T__24 || _la == T__25)) {
                                    _localctx.op = _errHandler.recoverInline(this)
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true
                                    _errHandler.reportMatch(this)
                                    consume()
                                }
                                state = 169
                                _localctx.factor = factor()
                                _localctx.result = factory!!.createBinary(_localctx.op, _localctx.result, _localctx.factor!!.result)
                            }
                        }
                    }
                    state = 176
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 11, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FactorContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        var result: SLExpressionNode? = null
        var IDENTIFIER: Token? = null
        var member_expression: Member_expressionContext? = null
        var STRING_LITERAL: Token? = null
        var NUMERIC_LITERAL: Token? = null
        var s: Token? = null
        var expr: ExpressionContext? = null
        var e: Token? = null
        fun IDENTIFIER(): TerminalNode {
            return getToken(SimpleLanguageParser.IDENTIFIER, 0)
        }

        fun STRING_LITERAL(): TerminalNode {
            return getToken(SimpleLanguageParser.STRING_LITERAL, 0)
        }

        fun NUMERIC_LITERAL(): TerminalNode {
            return getToken(SimpleLanguageParser.NUMERIC_LITERAL, 0)
        }

        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        fun member_expression(): Member_expressionContext {
            return getRuleContext(Member_expressionContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_factor
        }
    }

    @Throws(RecognitionException::class)
    fun factor(): FactorContext {
        val _localctx = FactorContext(_ctx, state)
        enterRule(_localctx, 24, RULE_factor)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 194
                _errHandler.sync(this)
                when (_input.LA(1)) {
                    IDENTIFIER -> {
                        state = 177
                        _localctx.IDENTIFIER = match(IDENTIFIER)
                        val assignmentName = factory!!.createStringLiteral(_localctx.IDENTIFIER, false)
                        state = 183
                        _errHandler.sync(this)
                        when (interpreter.adaptivePredict(_input, 12, _ctx)) {
                            1 -> {
                                state = 179
                                _localctx.member_expression = member_expression(null, null, assignmentName)
                                _localctx.result = _localctx.member_expression!!.result
                            }
                            2 -> {
                                _localctx.result = factory!!.createRead(assignmentName)
                            }
                        }
                    }
                    STRING_LITERAL -> {
                        state = 185
                        _localctx.STRING_LITERAL = match(STRING_LITERAL)
                        _localctx.result = factory!!.createStringLiteral(_localctx.STRING_LITERAL, true)
                    }
                    NUMERIC_LITERAL -> {
                        state = 187
                        _localctx.NUMERIC_LITERAL = match(NUMERIC_LITERAL)
                        _localctx.result = factory!!.createNumericLiteral(_localctx.NUMERIC_LITERAL)
                    }
                    T__1 -> {
                        state = 189
                        _localctx.s = match(T__1)
                        state = 190
                        _localctx.expr = expression()
                        state = 191
                        _localctx.e = match(T__3)
                        _localctx.result = factory!!.createParenExpression(_localctx.expr!!.result, _localctx.s!!.startIndex, _localctx.e!!.stopIndex - _localctx.s!!.startIndex + 1)
                    }
                    else -> throw NoViableAltException(this)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class Member_expressionContext : ParserRuleContext {
        var r: SLExpressionNode
        var assignmentReceiver: SLExpressionNode
        var assignmentName: SLExpressionNode
        var result: SLExpressionNode? = null
        var expression: ExpressionContext? = null
        var e: Token? = null
        var IDENTIFIER: Token? = null
        var member_expression: Member_expressionContext? = null
        fun expression(): List<ExpressionContext> {
            return getRuleContexts(ExpressionContext::class.java)
        }

        fun expression(i: Int): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, i)
        }

        fun IDENTIFIER(): TerminalNode {
            return getToken(SimpleLanguageParser.IDENTIFIER, 0)
        }

        fun member_expression(): Member_expressionContext {
            return getRuleContext(Member_expressionContext::class.java, 0)
        }

        constructor(parent: ParserRuleContext, invokingState: Int) : super(parent, invokingState) {}
        constructor(parent: ParserRuleContext, invokingState: Int, r: SLExpressionNode, assignmentReceiver: SLExpressionNode, assignmentName: SLExpressionNode) : super(parent, invokingState) {
            this.r = r
            this.assignmentReceiver = assignmentReceiver
            this.assignmentName = assignmentName
        }

        override fun getRuleIndex(): Int {
            return RULE_member_expression
        }
    }

    @Throws(RecognitionException::class)
    fun member_expression(r: SLExpressionNode?, assignmentReceiver: SLExpressionNode?, assignmentName: SLExpressionNode?): Member_expressionContext {
        val _localctx = Member_expressionContext(_ctx, state, r, assignmentReceiver, assignmentName)
        enterRule(_localctx, 26, RULE_member_expression)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                var receiver = r
                var nestedAssignmentName: SLExpressionNode? = null
                state = 228
                _errHandler.sync(this)
                when (_input.LA(1)) {
                    T__1 -> {
                        state = 197
                        match(T__1)
                        val parameters = ArrayList<SLExpressionNode>()
                        if (receiver == null) {
                            receiver = factory!!.createRead(assignmentName)
                        }
                        state = 210
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__1 or (1L shl IDENTIFIER) or (1L shl STRING_LITERAL) or (1L shl NUMERIC_LITERAL)) != 0L) {
                            run {
                                state = 199
                                _localctx.expression = expression()
                                parameters.add(_localctx.expression!!.result)
                                state = 207
                                _errHandler.sync(this)
                                _la = _input.LA(1)
                                while (_la == T__2) {
                                    run {
                                        run {
                                            state = 201
                                            match(T__2)
                                            state = 202
                                            _localctx.expression = expression()
                                            parameters.add(_localctx.expression!!.result)
                                        }
                                    }
                                    state = 209
                                    _errHandler.sync(this)
                                    _la = _input.LA(1)
                                }
                            }
                        }

                        state = 212
                        _localctx.e = match(T__3)
                        _localctx.result = factory!!.createCall(receiver, parameters, _localctx.e)
                    }
                    T__26 -> {
                        state = 214
                        match(T__26)
                        state = 215
                        _localctx.expression = expression()
                        if (assignmentName == null) {
                            SemErr(if (_localctx.expression != null) _localctx.expression!!.start else null, "invalid assignment target")
                        } else if (assignmentReceiver == null) {
                            _localctx.result = factory!!.createAssignment(assignmentName, _localctx.expression!!.result)
                        } else {
                            _localctx.result = factory!!.createWriteProperty(assignmentReceiver, assignmentName, _localctx.expression!!.result)
                        }
                    }
                    T__27 -> {
                        state = 218
                        match(T__27)
                        if (receiver == null) {
                            receiver = factory!!.createRead(assignmentName)
                        }
                        state = 220
                        _localctx.IDENTIFIER = match(IDENTIFIER)
                        nestedAssignmentName = factory!!.createStringLiteral(_localctx.IDENTIFIER, false)
                        _localctx.result = factory!!.createReadProperty(receiver, nestedAssignmentName)
                    }
                    T__28 -> {
                        state = 222
                        match(T__28)
                        if (receiver == null) {
                            receiver = factory!!.createRead(assignmentName)
                        }
                        state = 224
                        _localctx.expression = expression()
                        nestedAssignmentName = _localctx.expression!!.result
                        _localctx.result = factory!!.createReadProperty(receiver, nestedAssignmentName)
                        state = 226
                        match(T__29)
                    }
                    else -> throw NoViableAltException(this)
                }
                state = 233
                _errHandler.sync(this)
                when (interpreter.adaptivePredict(_input, 17, _ctx)) {
                    1 -> {
                        state = 230
                        _localctx.member_expression = member_expression(_localctx.result, receiver, nestedAssignmentName)
                        _localctx.result = _localctx.member_expression!!.result
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    companion object {
        init {
            RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION)
        }

        protected val _decisionToDFA: Array<DFA>
        protected val _sharedContextCache = PredictionContextCache()
        val T__0 = 1
        val T__1 = 2
        val T__2 = 3
        val T__3 = 4
        val T__4 = 5
        val T__5 = 6
        val T__6 = 7
        val T__7 = 8
        val T__8 = 9
        val T__9 = 10
        val T__10 = 11
        val T__11 = 12
        val T__12 = 13
        val T__13 = 14
        val T__14 = 15
        val T__15 = 16
        val T__16 = 17
        val T__17 = 18
        val T__18 = 19
        val T__19 = 20
        val T__20 = 21
        val T__21 = 22
        val T__22 = 23
        val T__23 = 24
        val T__24 = 25
        val T__25 = 26
        val T__26 = 27
        val T__27 = 28
        val T__28 = 29
        val T__29 = 30
        val WS = 31
        val COMMENT = 32
        val LINE_COMMENT = 33
        val IDENTIFIER = 34
        val STRING_LITERAL = 35
        val NUMERIC_LITERAL = 36
        val RULE_simplelanguage = 0
        val RULE_function = 1
        val RULE_block = 2
        val RULE_statement = 3
        val RULE_while_statement = 4
        val RULE_if_statement = 5
        val RULE_return_statement = 6
        val RULE_expression = 7
        val RULE_logic_term = 8
        val RULE_logic_factor = 9
        val RULE_arithmetic = 10
        val RULE_term = 11
        val RULE_factor = 12
        val RULE_member_expression = 13
        val ruleNames = arrayOf("simplelanguage", "function", "block", "statement", "while_statement", "if_statement", "return_statement", "expression", "logic_term", "logic_factor", "arithmetic", "term", "factor", "member_expression")

        private val _LITERAL_NAMES = arrayOf<String>(null, "'function'", "'('", "','", "')'", "'{'", "'}'", "'break'", "';'", "'continue'", "'debugger'", "'while'", "'if'", "'else'", "'return'", "'||'", "'&&'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'+'", "'-'", "'*'", "'/'", "'='", "'.'", "'['", "']'")
        private val _SYMBOLIC_NAMES = arrayOf<String>(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "WS", "COMMENT", "LINE_COMMENT", "IDENTIFIER", "STRING_LITERAL", "NUMERIC_LITERAL")
        val VOCABULARY: Vocabulary = VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES)


        @Deprecated("Use {@link #VOCABULARY} instead.")
        val tokenNames: Array<String>

        init {
            tokenNames = arrayOfNulls(_SYMBOLIC_NAMES.size)
            for (i in tokenNames.indices) {
                tokenNames[i] = VOCABULARY.getLiteralName(i)
                if (tokenNames[i] == null) {
                    tokenNames[i] = VOCABULARY.getSymbolicName(i)
                }

                if (tokenNames[i] == null) {
                    tokenNames[i] = "<INVALID>"
                }
            }
        }

        fun parseSL(language: SLLanguage, source: Source): Map<String, RootCallTarget> {
            val lexer = SimpleLanguageLexer(CharStreams.fromString(source.characters.toString()))
            val parser = SimpleLanguageParser(CommonTokenStream(lexer))
            lexer.removeErrorListeners()
            parser.removeErrorListeners()
            val listener = BailoutErrorListener(source)
            lexer.addErrorListener(listener)
            parser.addErrorListener(listener)
            parser.factory = SLNodeFactory(language, source)
            parser.source = source
            parser.simplelanguage()
            return parser.factory!!.allFunctions
        }

        val _serializedATN = "\u0003\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\u0003&\u00ee\u0004\u0002\t\u0002\u0004" +
                "\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t" +
                "\u000b\u0004\f\t\f\u0004\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0003\u0002\u0003\u0002\u0007\u0002!\n\u0002\f\u0002\u000e\u0002$\u000b" +
                "\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0007\u0003\u0031\n\u0003\f\u0003\u000e\u0003\u0034" +
                "\u000b\u0003\u0005\u0003\u0036\n\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u0004A\n\u0004\f\u0004\u000e\u0004" +
                "D\u000b\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005" +
                "\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005_\n\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006" +
                "\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0005\u0007r\n\u0007\u0003\u0007\u0003\u0007\u0003\b\u0003\b" +
                "\u0003\b\u0003\b\u0003\b\u0005\b{\n\b\u0003\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0007\t\u0086\n" +
                "\t\f\t\u000e\t\u0089\u000b\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0007\n\u0091\n\n\f\n\u000e\n" +
                "\u0094\u000b\n\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b\u009c\n\u000b\u0003\f\u0003\f\u0003\f" +
                "\u0003\f\u0003\f\u0003\f\u0007\f\u00a4\n\f\f\f\u000e\f\u00a7\u000b\f\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r" +
                "\u0007\r\u00af\n\r\f\r\u000e\r\u00b2\u000b\r\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0005\u000e" +
                "\u00ba\n\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0005\u000e\u00c5\n" +
                "\u000e\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0007\u000f\u00d0\n\u000f\f\u000f" +
                "\u000e\u000f\u00d3\u000b\u000f\u0005\u000f\u00d5\n\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f" +
                "\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0005\u000f\u00e7\n\u000f\u0003\u000f\u0003\u000f" +
                "\u0003\u000f\u0005\u000f\u00ec\n\u000f\u0003\u000f\u0002\u0002\u0010\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c" +
                "\u0002\u0005\u0003\u0002\u0013\u0018\u0003\u0002\u0019\u001a\u0003\u0002\u001b\u001c\u0002\u00fa\u0002\u001e\u0003\u0002\u0002\u0002\u0004\'\u0003\u0002\u0002\u0002\u0006" +
                ";\u0003\u0002\u0002\u0002\b^\u0003\u0002\u0002\u0002\n`\u0003\u0002\u0002\u0002\fg\u0003\u0002\u0002\u0002\u000eu\u0003\u0002\u0002\u0002\u0010\u007f\u0003\u0002\u0002\u0002" +
                "\u0012\u008a\u0003\u0002\u0002\u0002\u0014\u0095\u0003\u0002\u0002\u0002\u0016\u009d\u0003\u0002\u0002\u0002\u0018\u00a8\u0003\u0002\u0002\u0002\u001a" +
                "\u00c4\u0003\u0002\u0002\u0002\u001c\u00c6\u0003\u0002\u0002\u0002\u001e\"\u0005\u0004\u0003\u0002\u001f!\u0005\u0004\u0003\u0002 \u001f\u0003\u0002\u0002\u0002!" +
                "$\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#%\u0003\u0002\u0002\u0002$\"\u0003\u0002\u0002\u0002%&\u0007\u0002\u0002\u0003&\u0003\u0003\u0002" +
                "\u0002\u0002\'(\u0007\u0003\u0002\u0002()\u0007$\u0002\u0002)*\u0007\u0004\u0002\u0002*\u0035\b\u0003\u0001\u0002+,\u0007$\u0002\u0002,\u0032\b\u0003\u0001\u0002-." +
                "\u0007\u0005\u0002\u0002./\u0007$\u0002\u0002/\u0031\b\u0003\u0001\u0002\u0030-\u0003\u0002\u0002\u0002\u0031\u0034\u0003\u0002\u0002\u0002\u0032\u0030\u0003\u0002\u0002\u0002" +
                "\u0032\u0033\u0003\u0002\u0002\u0002\u0033\u0036\u0003\u0002\u0002\u0002\u0034\u0032\u0003\u0002\u0002\u0002\u0035+\u0003\u0002\u0002\u0002\u0035\u0036\u0003\u0002\u0002\u0002\u0036" +
                "\u0037\u0003\u0002\u0002\u0002\u00378\u0007\u0006\u0002\u000289\u0005\u0006\u0004\u00029:\b\u0003\u0001\u0002:\u0005\u0003\u0002\u0002\u0002;<\b\u0004\u0001\u0002<B\u0007\u0007" +
                "\u0002\u0002=>\u0005\b\u0005\u0002>?\b\u0004\u0001\u0002?A\u0003\u0002\u0002\u0002@=\u0003\u0002\u0002\u0002AD\u0003\u0002\u0002\u0002B@\u0003\u0002\u0002\u0002BC\u0003\u0002" +
                "\u0002\u0002CE\u0003\u0002\u0002\u0002DB\u0003\u0002\u0002\u0002EF\u0007\b\u0002\u0002FG\b\u0004\u0001\u0002G\u0007\u0003\u0002\u0002\u0002HI\u0005\n\u0006\u0002IJ\b" +
                "\u0005\u0001\u0002J_\u0003\u0002\u0002\u0002KL\u0007\t\u0002\u0002LM\b\u0005\u0001\u0002M_\u0007\n\u0002\u0002NO\u0007\u000b\u0002\u0002OP\b\u0005\u0001\u0002P_" +
                "\u0007\n\u0002\u0002QR\u0005\f\u0007\u0002RS\b\u0005\u0001\u0002S_\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0005\u0001\u0002V_\u0003\u0002\u0002\u0002" +
                "WX\u0005\u0010\t\u0002XY\u0007\n\u0002\u0002YZ\b\u0005\u0001\u0002Z_\u0003\u0002\u0002\u0002[\\\u0007\f\u0002\u0002\\]\b\u0005\u0001\u0002]_\u0007\n" +
                "\u0002\u0002^H\u0003\u0002\u0002\u0002^K\u0003\u0002\u0002\u0002^N\u0003\u0002\u0002\u0002^Q\u0003\u0002\u0002\u0002^T\u0003\u0002\u0002\u0002^W\u0003\u0002\u0002\u0002^[\u0003\u0002" +
                "\u0002\u0002_\t\u0003\u0002\u0002\u0002`a\u0007\r\u0002\u0002ab\u0007\u0004\u0002\u0002bc\u0005\u0010\t\u0002cd\u0007\u0006\u0002\u0002de\u0005\u0006\u0004\u0002ef\b" +
                "\u0006\u0001\u0002f\u000b\u0003\u0002\u0002\u0002gh\u0007\u000e\u0002\u0002hi\u0007\u0004\u0002\u0002ij\u0005\u0010\t\u0002jk\u0007\u0006\u0002\u0002kl\u0005\u0006\u0004\u0002" +
                "lq\b\u0007\u0001\u0002mn\u0007\u000f\u0002\u0002no\u0005\u0006\u0004\u0002op\b\u0007\u0001\u0002pr\u0003\u0002\u0002\u0002qm\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002" +
                "\u0002rs\u0003\u0002\u0002\u0002st\b\u0007\u0001\u0002t\r\u0003\u0002\u0002\u0002uv\u0007\u0010\u0002\u0002vz\b\b\u0001\u0002wx\u0005\u0010\t\u0002xy\b" +
                "\b\u0001\u0002y{\u0003\u0002\u0002\u0002zw\u0003\u0002\u0002\u0002z{\u0003\u0002\u0002\u0002{|\u0003\u0002\u0002\u0002|}\b\b\u0001\u0002}~\u0007\n\u0002\u0002~\u000f" +
                "\u0003\u0002\u0002\u0002\u007f\u0080\u0005\u0012\n\u0002\u0080\u0087\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082" +
                "\u0083\u0005\u0012\n\u0002\u0083\u0084\b\t\u0001\u0002\u0084\u0086\u0003\u0002\u0002\u0002\u0085\u0081\u0003" +
                "\u0002\u0002\u0002\u0086\u0089\u0003\u0002\u0002\u0002\u0087\u0085\u0003\u0002\u0002\u0002\u0087\u0088\u0003\u0002\u0002\u0002\u0088" +
                "\u0011\u0003\u0002\u0002\u0002\u0089\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0005\u0014\u000b\u0002\u008b\u0092\b\n" +
                "\u0001\u0002\u008c\u008d\u0007\u0012\u0002\u0002\u008d\u008e\u0005\u0014\u000b\u0002\u008e\u008f\b\n\u0001\u0002\u008f" +
                "\u0091\u0003\u0002\u0002\u0002\u0090\u008c\u0003\u0002\u0002\u0002\u0091\u0094\u0003\u0002\u0002\u0002\u0092\u0090\u0003\u0002" +
                "\u0002\u0002\u0092\u0093\u0003\u0002\u0002\u0002\u0093\u0013\u0003\u0002\u0002\u0002\u0094\u0092\u0003\u0002\u0002\u0002\u0095\u0096" +
                "\u0005\u0016\f\u0002\u0096\u009b\b\u000b\u0001\u0002\u0097\u0098\t\u0002\u0002\u0002\u0098\u0099\u0005\u0016\f" +
                "\u0002\u0099\u009a\b\u000b\u0001\u0002\u009a\u009c\u0003\u0002\u0002\u0002\u009b\u0097\u0003\u0002\u0002\u0002\u009b" +
                "\u009c\u0003\u0002\u0002\u0002\u009c\u0015\u0003\u0002\u0002\u0002\u009d\u009e\u0005\u0018\r\u0002\u009e\u00a5\b\f\u0001" +
                "\u0002\u009f\u00a0\t\u0003\u0002\u0002\u00a0\u00a1\u0005\u0018\r\u0002\u00a1\u00a2\b\f\u0001\u0002\u00a2" +
                "\u00a4\u0003\u0002\u0002\u0002\u00a3\u009f\u0003\u0002\u0002\u0002\u00a4\u00a7\u0003\u0002\u0002\u0002\u00a5\u00a3\u0003\u0002" +
                "\u0002\u0002\u00a5\u00a6\u0003\u0002\u0002\u0002\u00a6\u0017\u0003\u0002\u0002\u0002\u00a7\u00a5\u0003\u0002\u0002\u0002\u00a8\u00a9" +
                "\u0005\u001a\u000e\u0002\u00a9\u00b0\b\r\u0001\u0002\u00aa\u00ab\t\u0004\u0002\u0002\u00ab\u00ac\u0005\u001a\u000e" +
                "\u0002\u00ac\u00ad\b\r\u0001\u0002\u00ad\u00af\u0003\u0002\u0002\u0002\u00ae\u00aa\u0003\u0002\u0002\u0002\u00af\u00b2" +
                "\u0003\u0002\u0002\u0002\u00b0\u00ae\u0003\u0002\u0002\u0002\u00b0\u00b1\u0003\u0002\u0002\u0002\u00b1\u0019\u0003\u0002\u0002\u0002\u00b2" +
                "\u00b0\u0003\u0002\u0002\u0002\u00b3\u00b4\u0007$\u0002\u0002\u00b4\u00b9\b\u000e\u0001\u0002\u00b5\u00b6\u0005\u001c" +
                "\u000f\u0002\u00b6\u00b7\b\u000e\u0001\u0002\u00b7\u00ba\u0003\u0002\u0002\u0002\u00b8\u00ba\b\u000e\u0001\u0002\u00b9" +
                "\u00b5\u0003\u0002\u0002\u0002\u00b9\u00b8\u0003\u0002\u0002\u0002\u00ba\u00c5\u0003\u0002\u0002\u0002\u00bb\u00bc\u0007%" +
                "\u0002\u0002\u00bc\u00c5\b\u000e\u0001\u0002\u00bd\u00be\u0007&\u0002\u0002\u00be\u00c5\b\u000e\u0001\u0002\u00bf" +
                "\u00c0\u0007\u0004\u0002\u0002\u00c0\u00c1\u0005\u0010\t\u0002\u00c1\u00c2\u0007\u0006\u0002\u0002\u00c2\u00c3\b" +
                "\u000e\u0001\u0002\u00c3\u00c5\u0003\u0002\u0002\u0002\u00c4\u00b3\u0003\u0002\u0002\u0002\u00c4\u00bb\u0003\u0002\u0002\u0002\u00c4" +
                "\u00bd\u0003\u0002\u0002\u0002\u00c4\u00bf\u0003\u0002\u0002\u0002\u00c5\u001b\u0003\u0002\u0002\u0002\u00c6\u00e6\b\u000f\u0001" +
                "\u0002\u00c7\u00c8\u0007\u0004\u0002\u0002\u00c8\u00d4\b\u000f\u0001\u0002\u00c9\u00ca\u0005\u0010\t\u0002\u00ca" +
                "\u00d1\b\u000f\u0001\u0002\u00cb\u00cc\u0007\u0005\u0002\u0002\u00cc\u00cd\u0005\u0010\t\u0002\u00cd\u00ce\b" +
                "\u000f\u0001\u0002\u00ce\u00d0\u0003\u0002\u0002\u0002\u00cf\u00cb\u0003\u0002\u0002\u0002\u00d0\u00d3\u0003\u0002\u0002\u0002\u00d1" +
                "\u00cf\u0003\u0002\u0002\u0002\u00d1\u00d2\u0003\u0002\u0002\u0002\u00d2\u00d5\u0003\u0002\u0002\u0002\u00d3\u00d1\u0003\u0002" +
                "\u0002\u0002\u00d4\u00c9\u0003\u0002\u0002\u0002\u00d4\u00d5\u0003\u0002\u0002\u0002\u00d5\u00d6\u0003\u0002\u0002\u0002\u00d6" +
                "\u00d7\u0007\u0006\u0002\u0002\u00d7\u00e7\b\u000f\u0001\u0002\u00d8\u00d9\u0007\u001d\u0002\u0002\u00d9\u00da\u0005" +
                "\u0010\t\u0002\u00da\u00db\b\u000f\u0001\u0002\u00db\u00e7\u0003\u0002\u0002\u0002\u00dc\u00dd\u0007\u001e\u0002\u0002" +
                "\u00dd\u00de\b\u000f\u0001\u0002\u00de\u00df\u0007$\u0002\u0002\u00df\u00e7\b\u000f\u0001\u0002\u00e0\u00e1" +
                "\u0007\u001f\u0002\u0002\u00e1\u00e2\b\u000f\u0001\u0002\u00e2\u00e3\u0005\u0010\t\u0002\u00e3\u00e4\b\u000f\u0001" +
                "\u0002\u00e4\u00e5\u0007 \u0002\u0002\u00e5\u00e7\u0003\u0002\u0002\u0002\u00e6\u00c7\u0003\u0002\u0002\u0002\u00e6\u00d8" +
                "\u0003\u0002\u0002\u0002\u00e6\u00dc\u0003\u0002\u0002\u0002\u00e6\u00e0\u0003\u0002\u0002\u0002\u00e7\u00eb\u0003\u0002\u0002\u0002\u00e8" +
                "\u00e9\u0005\u001c\u000f\u0002\u00e9\u00ea\b\u000f\u0001\u0002\u00ea\u00ec\u0003\u0002\u0002\u0002\u00eb\u00e8" +
                "\u0003\u0002\u0002\u0002\u00eb\u00ec\u0003\u0002\u0002\u0002\u00ec\u001d\u0003\u0002\u0002\u0002\u0014\"\u0032\u0035B^qz\u0087\u0092" +
                "\u009b\u00a5\u00b0\u00b9\u00c4\u00d1\u00d4\u00e6\u00eb"
        val _ATN = ATNDeserializer().deserialize(_serializedATN.toCharArray())

        init {
            _decisionToDFA = arrayOfNulls(_ATN.numberOfDecisions)
            for (i in 0 until _ATN.numberOfDecisions) {
                _decisionToDFA[i] = DFA(_ATN.getDecisionState(i), i)
            }
        }
    }
}
