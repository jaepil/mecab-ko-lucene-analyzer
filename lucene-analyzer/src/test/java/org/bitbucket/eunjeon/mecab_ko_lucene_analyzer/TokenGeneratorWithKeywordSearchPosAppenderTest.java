/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.bitbucket.eunjeon.mecab_ko_lucene_analyzer;

import org.chasen.mecab.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenGeneratorWithKeywordSearchPosAppenderTest
  extends TokenGeneratorTestCase {
  private TokenizerOption option;

  @Before
  public void setUp() throws Exception {
    option = new TokenizerOption();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testBasicHangulSentence() {
    Node node = mockNodeListFactory(new String[] {
        "파란\tVA+ETM,*,T,파란,Inflect,VA,ETM,파랗/VA/*+ᆫ/ETM/*",
        "진달래\tNNG,*,F,진달래,*,*,*,*",
        " 꽃\tNNG,*,T,꽃,*,*,*,*",
        "이\tJKS,*,F,이,*,*,*,*",
        " 피\tVV,*,F,피,*,*,*,*",
        "었\tEP,*,T,었,*,*,*,*",
        "습니다\tEF,F,습니다,*,*,*,*",
        ".\t SF,*,*,*,*,*,*,*"
    });

    TokenGenerator generator = new TokenGenerator(
        new KeywordSearchPosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[파란/INFLECT/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/NNG/null/1/1/2/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃/NNG/null/1/1/6/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다/EOJEOL/null/1/1/9/14]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testBasicHangulSentence1() {
    Node node = mockNodeListFactory(new String[] {
        "훈민정음\tNNP,*,T,훈민정음,Compound,*,*,훈민/NNG/*+정음/NNG/*",
        "은\tJX,*,T,은,*,*,*,*",
        "경건\tXR,*,T,경건,*,*,*,*",
        "한\tXSA+ETM,*,T,한,Inflect,XSA,ETM,하/XSA/*+ᆫ/ETM/*",
        "글자\tNNG,*,F,글자,Compound,*,*,글/NNG/*자/NNG/*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*"
    });

    TokenGenerator generator = new TokenGenerator(
        new KeywordSearchPosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[훈민정음/COMPOUND/null/1/2/0/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[경건/XR/null/1/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[글자/COMPOUND/null/1/1/8/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithCompound() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성/NNG/*+전자/NNG/*",
        "는\tJX,*,T,는,*,*,*,*",
        " 대표\tNNG,*,F,대표,*,*,*,*",
        "적\tXSN,*,T,적,*,*,*,*",
        "인\tVCP+ETM,*,T,인,Inflect,VCP,ETM,이/VCP/*+ㄴ/ETM/*",
        " 복합\tNNG,*,T,복합,*,*,*,*",
        "명사\tNNG,*,F,명사,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
    });

    TokenGenerator generator = new TokenGenerator(
        new KeywordSearchPosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성전자/COMPOUND/null/1/2/0/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[대표적/EOJEOL/null/1/1/6/9]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[복합/NNG/null/1/1/11/13]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[명사/NNG/null/1/1/13/15]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithEnglishAndSymbols() {
    Node node = mockNodeListFactory(new String[] {
        "영어\tNNG,*,F,영어,*,*,*,*",
        "(\tSSO,*,*,*,*,*,*,*",
        "english\tSL,*,*,*,*,*,*,*",
        ")\tSSC,*,*,*,*,*,*,*",
        "를\tJKO,*,T,를,*,*,*,*",
        "study\tSL,*,*,*,*,*,*,*",
        "하\tXSV,*,F,하,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*",
        "것\tNNB,*,T,것,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*",
        "어렵\tVA,*,T,어렵,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
    });

    TokenGenerator generator = new TokenGenerator(
        new KeywordSearchPosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english/SL/null/1/1/3/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study/SL/null/1/1/12/17]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[것/NNB/null/1/1/19/20]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[어렵다/EOJEOL/null/1/1/21/24]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithEnglishAndNumbersAndSymbols() {
    Node node = mockNodeListFactory(new String[] {
        "아이폰\tNNP,*,T,아이폰,*,*,*,*",
        "5\tSN,*,*,*,*,*,*,*",
        "s\tSL,*,*,*,*,*,*,*",
        "를\tJKO,*,T,를,*,*,*,*",
        "32\tSN,*,*,*,*,*,*,*",
        "GB\tSL,*,*,*,*,*,*,*",
        "로\tJKB,*,F,로,*,*,*,*",
        "구입\tNNG,*,T,구입,*,*,*,*",
        "했\tXSV+EP,*,T,했,Inflect,XSV,EP,하/XSV/*+았/EP/*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
        "elasticsearch\tSL,*,*,*,*,*,*,*",
        "1\tSN,*,*,*,*,*,*,*",
        ".\tSY,*,*,*,*,*,*,*",
        "4\tSN,*,*,*,*,*,*,*",
        ".\tSY,*,*,*,*,*,*,*",
        "3\tSN,*,*,*,*,*,*,*",
        "릴리스\tNNG,*,F,릴리스,*,*,*,*",
        "되\tVV,*,F,되,*,*,*,*",
        "었\tEP,*,T,었,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
    });

    TokenGenerator generator = new TokenGenerator(
        new KeywordSearchPosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[아이폰/NNP/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[5/SN/null/1/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[s/SL/null/1/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[32/SN/null/1/1/6/8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[GB/SL/null/1/1/8/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[구입/NNG/null/1/1/11/13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[elasticsearch/SL/null/1/1/16/29]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[1/SN/null/1/1/29/30]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[4/SN/null/1/1/31/32]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[3/SN/null/1/1/33/34]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[릴리스/NNG/null/1/1/34/37]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[되었다/EOJEOL/null/1/1/37/40]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}
