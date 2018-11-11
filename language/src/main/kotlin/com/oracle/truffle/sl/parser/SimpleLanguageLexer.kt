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

import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.*
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.misc.*

class SimpleLanguageLexer(input: CharStream) : Lexer(input) {

    @Deprecated("")
    override fun getTokenNames(): Array<String> {
        return tokenNames
    }

    override fun getVocabulary(): Vocabulary {
        return VOCABULARY
    }


    init {
        _interp = LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache)
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

    override fun getChannelNames(): Array<String> {
        return channelNames
    }

    override fun getModeNames(): Array<String> {
        return modeNames
    }

    override fun getATN(): ATN {
        return _ATN
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
        var channelNames = arrayOf("DEFAULT_TOKEN_CHANNEL", "HIDDEN")

        var modeNames = arrayOf("DEFAULT_MODE")

        val ruleNames = arrayOf("T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", "T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", "T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", "T__25", "T__26", "T__27", "T__28", "T__29", "WS", "COMMENT", "LINE_COMMENT", "LETTER", "NON_ZERO_DIGIT", "DIGIT", "HEX_DIGIT", "OCT_DIGIT", "BINARY_DIGIT", "TAB", "STRING_CHAR", "IDENTIFIER", "STRING_LITERAL", "NUMERIC_LITERAL")

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

        val _serializedATN = "\u0003\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\u0002&\u0110\b\u0001\u0004\u0002\t" +
                "\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b" +
                "\t\u000b\u0004\u000C\t\u000C\u0004\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0004\u0010\t\u0010\u0004\u0011\t\u0011\u0004\u0012\t\u0012" +
                "\u0004\u0013\t\u0013\u0004\u0014\t\u0014\u0004\u0015\t\u0015\u0004\u0016\t\u0016\u0004\u0017\t\u0017\u0004\u0018\t\u0018\u0004\u0019\t\u0019" +
                "\u0004\u001a\t\u001a\u0004\u001b\t\u001b\u0004\u001c\t\u001c\u0004\u001d\t\u001d\u0004\u001e\t\u001e\u0004\u001f\t\u001f\u0004 \t \u0004!" +
                "\t!\u0004\"\t\"\u0004#\t#\u0004$\t$\u0004%\t%\u0004&\t&\u0004\'\t\'\u0004(\t(\u0004)\t)\u0004*\t*\u0004+\t+\u0004" +
                ",\t,\u0004-\t-\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005" +
                "\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003" +
                "\n\u0003\n\u0003\n\u0003\n\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000C\u0003\u000C\u0003" +
                "\u000C\u0003\u000C\u0003\u000C\u0003\u000C\u0003\r\u0003\r\u0003\r\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f" +
                "\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u0010\u0003\u0010\u0003\u0010\u0003\u0011\u0003\u0011\u0003\u0011\u0003\u0012\u0003\u0012\u0003\u0013\u0003\u0013\u0003\u0013" +
                "\u0003\u0014\u0003\u0014\u0003\u0015\u0003\u0015\u0003\u0015\u0003\u0016\u0003\u0016\u0003\u0016\u0003\u0017\u0003\u0017\u0003\u0017\u0003\u0018\u0003\u0018\u0003\u0019" +
                "\u0003\u0019\u0003\u001a\u0003\u001a\u0003\u001b\u0003\u001b\u0003\u001c\u0003\u001c\u0003\u001d\u0003\u001d\u0003\u001e\u0003\u001e\u0003\u001f\u0003\u001f\u0003 \u0006" +
                " \u00c5\n \r \u000e \u00c6\u0003 \u0003 \u0003!\u0003!\u0003!\u0003!\u0007!\u00cf\n!\u000C!\u000e!\u00d2\u000b" +
                "!\u0003!\u0003!\u0003!\u0003!\u0003!\u0003\"\u0003\"\u0003\"\u0003\"\u0007\"\u00dd\n\"\u000C\"\u000e\"\u00e0\u000b\"\u0003\"" +
                "\u0003\"\u0003#\u0005#\u00e5\n#\u0003$\u0003$\u0003%\u0003%\u0003&\u0005&\u00ec\n&\u0003\'\u0003\'\u0003(\u0003(\u0003)\u0003)\u0003*" +
                "\u0003*\u0003+\u0003+\u0003+\u0007+\u00f9\n+\u000C+\u000e+\u00fc\u000b+\u0003,\u0003,\u0007,\u0100\n,\u000C,\u000e,\u0103" +
                "\u000b,\u0003,\u0003,\u0003-\u0003-\u0003-\u0007-\u010a\n-\u000C-\u000e-\u010d\u000b-\u0005-\u010f\n-\u0003\u00d0\u0002" +
                ".\u0003\u0003\u0005\u0004\u0007\u0005\t\u0006\u000b\u0007\r\b\u000f\t\u0011\n\u0013\u000b\u0015\u000C\u0017\r\u0019\u000e\u001b\u000f\u001d\u0010" +
                "\u001f\u0011!\u0012#\u0013%\u0014\'\u0015)\u0016+\u0017-\u0018/\u0019\u0031\u001a\u0033\u001b\u0035\u001c\u0037\u001d9\u001e;\u001f" +
                "= ?!A\"C#E\u0002G\u0002I\u0002K\u0002M\u0002O\u0002Q\u0002S\u0002U\$W%Y&\u0003\u0002\n\u0005\u0002\u000b\u000C\u000e\u000f\"\"\u0004\u0002\u000C" +
                "\u000C\u000f\u000f\u0006\u0002&&C\\aac|\u0003\u0002\u0033;\u0003\u0002\u0032;\u0005\u0002\u0032;CHch\u0003\u0002\u00329\u0006\u0002\u000C\u000C\u000f\u000f" +
                "$$^^\u0002\u010f\u0002\u0003\u0003\u0002\u0002\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002" +
                "\u0002\u0002\u0002\r\u0003\u0002\u0002\u0002\u0002\u000f\u0003\u0002\u0002\u0002\u0002\u0011\u0003\u0002\u0002\u0002\u0002\u0013\u0003\u0002\u0002\u0002\u0002\u0015\u0003\u0002\u0002\u0002\u0002" +
                "\u0017\u0003\u0002\u0002\u0002\u0002\u0019\u0003\u0002\u0002\u0002\u0002\u001b\u0003\u0002\u0002\u0002\u0002\u001d\u0003\u0002\u0002\u0002\u0002\u001f\u0003\u0002\u0002\u0002\u0002!\u0003\u0002" +
                "\u0002\u0002\u0002#\u0003\u0002\u0002\u0002\u0002%\u0003\u0002\u0002\u0002\u0002\'\u0003\u0002\u0002\u0002\u0002)\u0003\u0002\u0002\u0002\u0002+\u0003\u0002\u0002\u0002\u0002-\u0003\u0002\u0002" +
                "\u0002\u0002/\u0003\u0002\u0002\u0002\u0002\u0031\u0003\u0002\u0002\u0002\u0002\u0033\u0003\u0002\u0002\u0002\u0002\u0035\u0003\u0002\u0002\u0002\u0002\u0037\u0003\u0002\u0002\u0002\u00029\u0003" +
                "\u0002\u0002\u0002\u0002;\u0003\u0002\u0002\u0002\u0002=\u0003\u0002\u0002\u0002\u0002?\u0003\u0002\u0002\u0002\u0002A\u0003\u0002\u0002\u0002\u0002C\u0003\u0002\u0002\u0002\u0002U\u0003\u0002\u0002" +
                "\u0002\u0002W\u0003\u0002\u0002\u0002\u0002Y\u0003\u0002\u0002\u0002\u0003[\u0003\u0002\u0002\u0002\u0005d\u0003\u0002\u0002\u0002\u0007f\u0003\u0002\u0002\u0002\th\u0003\u0002\u0002\u0002\u000b" +
                "j\u0003\u0002\u0002\u0002\rl\u0003\u0002\u0002\u0002\u000fn\u0003\u0002\u0002\u0002\u0011t\u0003\u0002\u0002\u0002\u0013v\u0003\u0002\u0002\u0002\u0015\u007f\u0003\u0002\u0002" +
                "\u0002\u0017\u0088\u0003\u0002\u0002\u0002\u0019\u008e\u0003\u0002\u0002\u0002\u001b\u0091\u0003\u0002\u0002\u0002\u001d\u0096\u0003\u0002\u0002\u0002" +
                "\u001f\u009d\u0003\u0002\u0002\u0002!\u00a0\u0003\u0002\u0002\u0002#\u00a3\u0003\u0002\u0002\u0002%\u00a5\u0003\u0002\u0002\u0002\'\u00a8" +
                "\u0003\u0002\u0002\u0002)\u00aa\u0003\u0002\u0002\u0002+\u00ad\u0003\u0002\u0002\u0002-\u00b0\u0003\u0002\u0002\u0002/\u00b3\u0003\u0002\u0002\u0002\u0031" +
                "\u00b5\u0003\u0002\u0002\u0002\u0033\u00b7\u0003\u0002\u0002\u0002\u0035\u00b9\u0003\u0002\u0002\u0002\u0037\u00bb\u0003\u0002\u0002\u00029\u00bd" +
                "\u0003\u0002\u0002\u0002;\u00bf\u0003\u0002\u0002\u0002=\u00c1\u0003\u0002\u0002\u0002?\u00c4\u0003\u0002\u0002\u0002A\u00ca\u0003\u0002\u0002\u0002C" +
                "\u00d8\u0003\u0002\u0002\u0002E\u00e4\u0003\u0002\u0002\u0002G\u00e6\u0003\u0002\u0002\u0002I\u00e8\u0003\u0002\u0002\u0002K\u00eb\u0003\u0002" +
                "\u0002\u0002M\u00ed\u0003\u0002\u0002\u0002O\u00ef\u0003\u0002\u0002\u0002Q\u00f1\u0003\u0002\u0002\u0002S\u00f3\u0003\u0002\u0002\u0002U\u00f5" +
                "\u0003\u0002\u0002\u0002W\u00fd\u0003\u0002\u0002\u0002Y\u010e\u0003\u0002\u0002\u0002[\\\u0007h\u0002\u0002\\]\u0007w\u0002\u0002]^\u0007p\u0002\u0002^_" +
                "\u0007e\u0002\u0002_`\u0007v\u0002\u0002`a\u0007k\u0002\u0002ab\u0007q\u0002\u0002bc\u0007p\u0002\u0002c\u0004\u0003\u0002\u0002\u0002de\u0007*\u0002\u0002e\u0006\u0003\u0002" +
                "\u0002\u0002fg\u0007.\u0002\u0002g\b\u0003\u0002\u0002\u0002hi\u0007+\u0002\u0002i\n\u0003\u0002\u0002\u0002jk\u0007}\u0002\u0002k\u000C\u0003\u0002\u0002\u0002lm\u0007\u007f" +
                "\u0002\u0002m\u000e\u0003\u0002\u0002\u0002no\u0007d\u0002\u0002op\u0007t\u0002\u0002pq\u0007g\u0002\u0002qr\u0007c\u0002\u0002rs\u0007m\u0002\u0002s\u0010\u0003\u0002" +
                "\u0002\u0002tu\u0007=\u0002\u0002u\u0012\u0003\u0002\u0002\u0002vw\u0007e\u0002\u0002wx\u0007q\u0002\u0002xy\u0007p\u0002\u0002yz\u0007v\u0002\u0002z{\u0007k\u0002\u0002" +
                "{|\u0007p\u0002\u0002|}\u0007w\u0002\u0002}~\u0007g\u0002\u0002~\u0014\u0003\u0002\u0002\u0002\u007f\u0080\u0007f\u0002\u0002\u0080\u0081\u0007" +
                "g\u0002\u0002\u0081\u0082\u0007d\u0002\u0002\u0082\u0083\u0007w\u0002\u0002\u0083\u0084\u0007i\u0002\u0002\u0084\u0085" +
                "\u0007i\u0002\u0002\u0085\u0086\u0007g\u0002\u0002\u0086\u0087\u0007t\u0002\u0002\u0087\u0016\u0003\u0002\u0002\u0002\u0088\u0089" +
                "\u0007y\u0002\u0002\u0089\u008a\u0007j\u0002\u0002\u008a\u008b\u0007k\u0002\u0002\u008b\u008c\u0007n\u0002\u0002\u008c" +
                "\u008d\u0007g\u0002\u0002\u008d\u0018\u0003\u0002\u0002\u0002\u008e\u008f\u0007k\u0002\u0002\u008f\u0090\u0007h\u0002\u0002\u0090" +
                "\u001a\u0003\u0002\u0002\u0002\u0091\u0092\u0007g\u0002\u0002\u0092\u0093\u0007n\u0002\u0002\u0093\u0094\u0007u\u0002\u0002\u0094" +
                "\u0095\u0007g\u0002\u0002\u0095\u001c\u0003\u0002\u0002\u0002\u0096\u0097\u0007t\u0002\u0002\u0097\u0098\u0007g\u0002\u0002\u0098" +
                "\u0099\u0007v\u0002\u0002\u0099\u009a\u0007w\u0002\u0002\u009a\u009b\u0007t\u0002\u0002\u009b\u009c\u0007p\u0002\u0002" +
                "\u009c\u001e\u0003\u0002\u0002\u0002\u009d\u009e\u0007~\u0002\u0002\u009e\u009f\u0007~\u0002\u0002\u009f \u0003\u0002\u0002\u0002" +
                "\u00a0\u00a1\u0007(\u0002\u0002\u00a1\u00a2\u0007(\u0002\u0002\u00a2\"\u0003\u0002\u0002\u0002\u00a3\u00a4\u0007>" +
                "\u0002\u0002\u00a4$\u0003\u0002\u0002\u0002\u00a5\u00a6\u0007>\u0002\u0002\u00a6\u00a7\u0007?\u0002\u0002\u00a7&\u0003\u0002\u0002" +
                "\u0002\u00a8\u00a9\u0007@\u0002\u0002\u00a9(\u0003\u0002\u0002\u0002\u00aa\u00ab\u0007@\u0002\u0002\u00ab\u00ac\u0007" +
                "?\u0002\u0002\u00ac*\u0003\u0002\u0002\u0002\u00ad\u00ae\u0007?\u0002\u0002\u00ae\u00af\u0007?\u0002\u0002\u00af,\u0003\u0002" +
                "\u0002\u0002\u00b0\u00b1\u0007#\u0002\u0002\u00b1\u00b2\u0007?\u0002\u0002\u00b2.\u0003\u0002\u0002\u0002\u00b3\u00b4" +
                "\u0007-\u0002\u0002\u00b4\u0030\u0003\u0002\u0002\u0002\u00b5\u00b6\u0007/\u0002\u0002\u00b6\u0032\u0003\u0002\u0002\u0002\u00b7\u00b8" +
                "\u0007,\u0002\u0002\u00b8\u0034\u0003\u0002\u0002\u0002\u00b9\u00ba\u0007\u0031\u0002\u0002\u00ba\u0036\u0003\u0002\u0002\u0002\u00bb\u00bc" +
                "\u0007?\u0002\u0002\u00bc8\u0003\u0002\u0002\u0002\u00bd\u00be\u0007\u0030\u0002\u0002\u00be:\u0003\u0002\u0002\u0002\u00bf\u00c0" +
                "\u0007]\u0002\u0002\u00c0<\u0003\u0002\u0002\u0002\u00c1\u00c2\u0007_\u0002\u0002\u00c2>\u0003\u0002\u0002\u0002\u00c3\u00c5\t" +
                "\u0002\u0002\u0002\u00c4\u00c3\u0003\u0002\u0002\u0002\u00c5\u00c6\u0003\u0002\u0002\u0002\u00c6\u00c4\u0003\u0002\u0002\u0002\u00c6" +
                "\u00c7\u0003\u0002\u0002\u0002\u00c7\u00c8\u0003\u0002\u0002\u0002\u00c8\u00c9\b \u0002\u0002\u00c9@\u0003\u0002\u0002\u0002\u00ca" +
                "\u00cb\u0007\u0031\u0002\u0002\u00cb\u00cc\u0007,\u0002\u0002\u00cc\u00d0\u0003\u0002\u0002\u0002\u00cd\u00cf\u000b" +
                "\u0002\u0002\u0002\u00ce\u00cd\u0003\u0002\u0002\u0002\u00cf\u00d2\u0003\u0002\u0002\u0002\u00d0\u00d1\u0003\u0002\u0002\u0002\u00d0" +
                "\u00ce\u0003\u0002\u0002\u0002\u00d1\u00d3\u0003\u0002\u0002\u0002\u00d2\u00d0\u0003\u0002\u0002\u0002\u00d3\u00d4\u0007," +
                "\u0002\u0002\u00d4\u00d5\u0007\u0031\u0002\u0002\u00d5\u00d6\u0003\u0002\u0002\u0002\u00d6\u00d7\b!\u0002\u0002\u00d7" +
                "B\u0003\u0002\u0002\u0002\u00d8\u00d9\u0007\u0031\u0002\u0002\u00d9\u00da\u0007\u0031\u0002\u0002\u00da\u00de\u0003\u0002\u0002" +
                "\u0002\u00db\u00dd\n\u0003\u0002\u0002\u00dc\u00db\u0003\u0002\u0002\u0002\u00dd\u00e0\u0003\u0002\u0002\u0002\u00de\u00dc" +
                "\u0003\u0002\u0002\u0002\u00de\u00df\u0003\u0002\u0002\u0002\u00df\u00e1\u0003\u0002\u0002\u0002\u00e0\u00de\u0003\u0002\u0002\u0002\u00e1" +
                "\u00e2\b\"\u0002\u0002\u00e2D\u0003\u0002\u0002\u0002\u00e3\u00e5\t\u0004\u0002\u0002\u00e4\u00e3\u0003\u0002\u0002\u0002" +
                "\u00e5F\u0003\u0002\u0002\u0002\u00e6\u00e7\t\u0005\u0002\u0002\u00e7H\u0003\u0002\u0002\u0002\u00e8\u00e9\t\u0006\u0002\u0002" +
                "\u00e9J\u0003\u0002\u0002\u0002\u00ea\u00ec\t\u0007\u0002\u0002\u00eb\u00ea\u0003\u0002\u0002\u0002\u00ecL\u0003\u0002\u0002\u0002" +
                "\u00ed\u00ee\t\b\u0002\u0002\u00eeN\u0003\u0002\u0002\u0002\u00ef\u00f0\u0004\u0032\u0033\u0002\u00f0P\u0003\u0002\u0002" +
                "\u0002\u00f1\u00f2\u0007\u000b\u0002\u0002\u00f2R\u0003\u0002\u0002\u0002\u00f3\u00f4\n\t\u0002\u0002\u00f4T\u0003\u0002" +
                "\u0002\u0002\u00f5\u00fa\u0005E#\u0002\u00f6\u00f9\u0005E#\u0002\u00f7\u00f9\u0005I%\u0002\u00f8\u00f6" +
                "\u0003\u0002\u0002\u0002\u00f8\u00f7\u0003\u0002\u0002\u0002\u00f9\u00fc\u0003\u0002\u0002\u0002\u00fa\u00f8\u0003\u0002\u0002\u0002\u00fa" +
                "\u00fb\u0003\u0002\u0002\u0002\u00fbV\u0003\u0002\u0002\u0002\u00fc\u00fa\u0003\u0002\u0002\u0002\u00fd\u0101\u0007$\u0002\u0002\u00fe" +
                "\u0100\u0005S*\u0002\u00ff\u00fe\u0003\u0002\u0002\u0002\u0100\u0103\u0003\u0002\u0002\u0002\u0101\u00ff\u0003\u0002\u0002" +
                "\u0002\u0101\u0102\u0003\u0002\u0002\u0002\u0102\u0104\u0003\u0002\u0002\u0002\u0103\u0101\u0003\u0002\u0002\u0002\u0104\u0105" +
                "\u0007$\u0002\u0002\u0105X\u0003\u0002\u0002\u0002\u0106\u010f\u0007\u0032\u0002\u0002\u0107\u010b\u0005G$\u0002\u0108\u010a" +
                "\u0005I%\u0002\u0109\u0108\u0003\u0002\u0002\u0002\u010a\u010d\u0003\u0002\u0002\u0002\u010b\u0109\u0003\u0002\u0002\u0002\u010b" +
                "\u010c\u0003\u0002\u0002\u0002\u010c\u010f\u0003\u0002\u0002\u0002\u010d\u010b\u0003\u0002\u0002\u0002\u010e\u0106\u0003\u0002" +
                "\u0002\u0002\u010e\u0107\u0003\u0002\u0002\u0002\u010fZ\u0003\u0002\u0002\u0002\r\u0002\u00c6\u00d0\u00de\u00e4\u00eb" +
                "\u00f8\u00fa\u0101\u010b\u010e\u0003\b\u0002\u0002"
        val _ATN = ATNDeserializer().deserialize(_serializedATN.toCharArray())

        init {
            _decisionToDFA = arrayOfNulls(_ATN.numberOfDecisions)
            for (i in 0 until _ATN.numberOfDecisions) {
                _decisionToDFA[i] = DFA(_ATN.getDecisionState(i), i)
            }
        }
    }
}
