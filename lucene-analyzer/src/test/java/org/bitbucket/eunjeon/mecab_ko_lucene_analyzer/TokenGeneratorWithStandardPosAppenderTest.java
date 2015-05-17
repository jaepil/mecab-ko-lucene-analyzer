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

public class TokenGeneratorWithStandardPosAppenderTest
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
        "진달래\tNNG,*,F,진달래,*,*,*,*",
        " 꽃\tNNG,*,T,꽃,*,*,*,*",
        "이\tJKS,*,F,이,*,*,*,*",
        " 피\tVV,*,F,피,*,*,*,*",
        "었\tEP,*,T,었,*,*,*,*",
        "습니다\tEF,F,습니다,*,*,*,*",
        ".\t SF,*,*,*,*,*,*,*"
    });
  
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(option), TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/NNG/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[꽃이/EOJEOL/null/1/1/4/6, 꽃/NNG/null/0/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[피었습니다/EOJEOL/null/1/1/7/12, 피/VV/null/0/1/7/8]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testNoUsingVaVvOriginalForm() {
    Node node = mockNodeListFactory(new String[] {
        "진달래\tNNG,*,F,진달래,*,*,*,*",
        " 꽃\tNNG,*,T,꽃,*,*,*,*",
        "이\tJKS,*,F,이,*,*,*,*",
        " 피\tVV,*,F,피,*,*,*,*",
        "었\tEP,*,T,었,*,*,*,*",
        "습니다\tEF,F,습니다,*,*,*,*",
        ".\t SF,*,*,*,*,*,*,*"
    });

    TokenizerOption opt = new TokenizerOption();
    opt.useAdjectiveAndVerbOriginalForm = false;
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(opt), TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/NNG/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[꽃이/EOJEOL/null/1/1/4/6, 꽃/NNG/null/0/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다/EOJEOL/null/1/1/7/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성/NNG/*+전자/NNG/*",
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/NNG/null/1/1/0/2, 삼성전자/COMPOUND/null/0/2/0/4, 전자/NNG/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testComplexDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "아질산나트륨\tNNG,*,T,아질산나트륨,Compound,*,*," +
        "아/NNG/*+질산/NNG/*+나트륨/NNG/*"
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[아/NNG/null/1/1/0/1, 아질산나트륨/COMPOUND/null/0/3/0/6, " +
            "아질산/COMPOUND/null/0/2/0/3, 질산/NNG/null/1/1/1/3, " +
            "질산나트륨/COMPOUND/null/0/2/1/6, 나트륨/NNG/null/1/1/3/6]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testComplexDecompound1() {
    Node node = mockNodeListFactory(new String[] {
        "새절역\tNNP,지명,T,새절역,Compound,*,*,새/MM/~명사+절/NNG/*+역/NNG/*"
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[새/MM/~명사/1/1/0/1, 새절역/COMPOUND/null/0/3/0/3, " +
            "새절/COMPOUND/null/0/2/0/2, 절/NNG/null/1/1/1/2, 역/NNG/null/1/1/2/3]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testComplexWithNumberDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "을지로3가역\tNNG,*,T,을지로3가역,Compound,*,*," +
            "을지로/NNP/*+3/SN/*+가/NNG/*+역/NNG/*"
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[을지로/NNP/null/1/1/0/3, 을지로3가역/COMPOUND/null/0/4/0/6, " +
            "을지로3/COMPOUND/null/0/2/0/4, 3/SN/null/1/1/3/4, " +
            "3가/COMPOUND/null/0/2/3/5, 가/NNG/null/1/1/4/5, " +
            "역/NNG/null/1/1/5/6]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testNoDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성/NNG/*+전자/NNG/*",
    });
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(option), TokenGenerator.NO_DECOMPOUND, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자/COMPOUND/null/1/2/0/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testCompoundNounMinLength4() {
    Node node = mockNodeListFactory(new String[] {
        "무궁화\tNNG,*,F,무궁화,Compound,*,*,무궁/NNG/*+화/NNG/*"
    });
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(option), 4, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화/COMPOUND/null/1/2/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
    
    node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성/NNG/*+전자/NNG/*"
    });
    generator = new TokenGenerator(new StandardPosAppender(option), 4, node);
    
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/NNG/null/1/1/0/2, 삼성전자/COMPOUND/null/0/2/0/4, 전자/NNG/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSentenceWithDecompoundAll() {
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
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/NNG/null/1/1/0/2, 삼성전자는/EOJEOL/null/0/2/0/5, 삼성전자/COMPOUND/null/0/2/0/4, 전자/NNG/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[대표/NNG/null/1/1/6/8]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[적인/EOJEOL/null/1/1/8/10, 적/XSN/null/0/1/8/9]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[복합/NNG/null/1/1/11/13]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[명사이다/EOJEOL/null/1/1/13/17, 명사/NNG/null/0/1/13/15]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSentenceWithDecompoundComplexCompoundNoun() {
    Node node = mockNodeListFactory(new String[] {
        "아질산나트륨\tNNG,*,T,아질산나트륨,Compound,*,*,아/NNG/*+질산/NNG/*+나트륨/NNG/*",
        "이란\tJX,*,T,이란,*,*,*,*",
        "무엇\tNP,*,T,무엇,*,*,*,*",
        "인가요\tVCP+EF,*,F,인가요,Inflect,VCP,EF,이/VCP/*+ㄴ가요/EF/*",
        "?\tSF,*,*,*,*,*,*,*",
    });
    
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[아/NNG/null/1/1/0/1, 아질산나트륨이란/EOJEOL/null/0/3/0/8, " +
            "아질산나트륨/COMPOUND/null/0/3/0/6, 아질산/COMPOUND/null/0/2/0/3, " +
            "질산/NNG/null/1/1/1/3, 질산나트륨/COMPOUND/null/0/2/1/6, " +
            "나트륨/NNG/null/1/1/3/6]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[무엇인가요/EOJEOL/null/1/1/8/13, 무엇/NP/null/0/1/8/10]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithCompoundNounMinLength4() {
    Node node = mockNodeListFactory(new String[] {
        "나\tNP,*,F,나,*,*,*,*",
        "의\tJKG,*,F,의,*,*,*,*",
        "무궁화\tNNG,*,F,무궁화,Compound,*,*,무궁/NNG/*+화/NNG/*",
        "꽃\tNNG,*,T,꽃,*,*,*,*",
        "을\tJKO,*,T,을,*,*,*,*",
        "보\tVV,*,F,보,*,*,*,*",
        "아라\tEF,*,F,아라,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
    });
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[나의/EOJEOL/null/1/1/0/2, 나/NP/null/0/1/0/1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[무궁화/COMPOUND/null/1/2/2/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[꽃을/EOJEOL/null/1/1/5/7, 꽃/NNG/null/0/1/5/6]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[보아라/EOJEOL/null/1/1/7/10, 보/VV/null/0/1/7/8]", tokens.toString());
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
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 2, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english/SL/null/1/1/3/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[를/J/null/1/1/11/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study/SL/null/1/1/12/17]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[하는/EOJEOL/null/1/1/17/19]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[것은/EOJEOL/null/1/1/19/21, 것/NNB/null/0/1/19/20]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[어렵다/EOJEOL/null/1/1/21/24, 어렵/VA/null/0/1/21/23]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testPreanaysis() {
    Node node = mockNodeListFactory(new String[] {
        "은전한닢\tNNG+NR+NNG,*,T,은전한닢,Preanalysis,NNG,NR,은전/NNG/*+한/NR/*+닢/NNG/*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[은전/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한/NR/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[닢/NNG/null/1/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testPreanaysisSentence() {
    Node node = mockNodeListFactory(new String[] {
        "은전한닢\tNNG+NR+NNG,*,T,은전한닢,Preanalysis,NNG,NR,은전/NNG/*+한/NR/*+닢/NNG/*",
        "은\tJX,*,T,은,*,*,*,*",
        "오픈\tNNG,*,T,오픈,*,*,*,*",
        "소스\tNNG,*,F,소스,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[은전/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한/NR/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[닢은/EOJEOL/null/1/1/3/5, 닢/NNG/null/0/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[오픈/NNG/null/1/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[소스이다/EOJEOL/null/1/1/7/11, 소스/NNG/null/0/1/7/9]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testUnknownSurface() {
    Node node = mockNodeListFactory(new String[] {
        "걀꿀\tUNKNOWN,*,*,*,*,*,*,*",
        " 없\tVA,*,T,없,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*",
        " 단어\tNNG,*,F,단어,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[걀꿀/UNKNOWN/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[없는/EOJEOL/null/1/1/3/5, 없/VA/null/0/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[단어/NNG/null/1/1/6/8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSymbolOnlySentence() {
    Node node = mockNodeListFactory(new String[] {
        "!@#$%^&*()\tSY,*,*,*,*,*,*"
    });
  	
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(option),
            TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH,
            node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testXsn() {
    Node node = mockNodeListFactory(new String[] {
        "의대\tNNG,*,F,의대,*,*,*,*",
        "생\tXSN,*,T,생,*,*,*,*",

    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[의대/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[생/XSN/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithXsn() {
    Node node = mockNodeListFactory(new String[] {
        "공대\tNNG,*,F,공대,*,*,*,*",
        "생\tXSN,*,T,생,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*",
        " 바쁘\tVA,*,F,바쁘,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[공대/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[생은/EOJEOL/null/1/1/2/4, 생/XSN/null/0/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[바쁘다/EOJEOL/null/1/1/5/8, 바쁘/VA/null/0/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testIndependentXpn() {
    Node node = mockNodeListFactory(new String[] {
        "왕\tXPN,*,T,왕,*,*,*,*",
        "게임\tNNG,*,T,게임,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[왕/XPN/null/1/1/0/1, 왕게임/COMPOUND/null/0/2/0/3, 게임/NNG/null/1/1/1/3]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testXpn() {
    Node node = mockNodeListFactory(new String[] {
        "비\tXPN,*,F,비,*,*,*,*",
        "정상\tNNG,*,T,정상,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[비정상/NNG/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testXpn1() {
    Node node = mockNodeListFactory(new String[] {
        "너\tNP,*,F,너,*,*,*,*",
        "는\tJX,*,T,는,*,*,*,*",
        "비\tXPN,*,F,비,*,*,*,*",
        "정상\tNNG,*,T,정상,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*"
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[너는/EOJEOL/null/1/1/0/2, 너/NP/null/0/1/0/1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[비정상이다/EOJEOL/null/1/1/2/7, 비정상/NNG/null/0/1/2/5]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testInflect1() {
    Node node = mockNodeListFactory(new String[]{
        "그것\tNP,*,T,그것,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*",
        "어려운\tVA+ETM,*,T,어려운,Inflect,VA,ETM,어렵/VA/*+ᆫ/ETM/*",
        "문제\tNNG,*,F,문제,*,*,*,*",
        "다\tVCP+EF,*,F,다,Inflect,VCP,EF,이/VCP/*+다/EF/*",
        ".\tSF,*,*,*,*,*,*,*"
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[그것은/EOJEOL/null/1/1/0/3, 그것/NP/null/0/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[어려운/INFLECT/null/1/1/3/6, 어렵/VA/null/0/1/3/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[문제다/EOJEOL/null/1/1/6/9, 문제/NNG/null/0/1/6/8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testInflect2() {
    Node node = mockNodeListFactory(new String[]{
        "그것\tNP,*,T,그것,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*",
        "어려워\tVA+EF,*,F,어려워,Inflect,VA,EF,어렵/VA/*+어/EF/*",
        "란\tETM,*,T,란,*,*,*,*",
        "문제\tNNG,*,F,문제,*,*,*,*",
        "다\tVCP+EF,*,F,다,Inflect,VCP,EF,이/VCP/*+다/EF/*",
        ".\tSF,*,*,*,*,*,*,*"
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[그것은/EOJEOL/null/1/1/0/3, 그것/NP/null/0/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[어려워란/EOJEOL/null/1/1/3/7, 어렵/VA/null/0/1/3/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[문제다/EOJEOL/null/1/1/7/10, 문제/NNG/null/0/1/7/9]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testInflect3() {
    Node node = mockNodeListFactory(new String[]{
        "오빤\tNNG+VCP+JX,*,T,오빤,Inflect,NNG,JX,오빠/NNG/*+이/VCP/*+ㄴ/JX/*",
        "강남\tNNP,지명,T,강남,*,*,*,*",
        "스타일\tNNG,*,T,스타일,*,*,*,*"
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[오빤/INFLECT/null/1/1/0/2, 오빠/NNG/null/0/1/0/2]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[강남/NNP/지명/1/1/2/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[스타일/NNG/null/1/1/4/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSemanticClass() {
    Node node = mockNodeListFactory(new String[] {
        "남희석\tNNP,인명,T,남희석,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*",
        " 충남\tNNP,지명,T,충남,*,*,*,*",
        "사람\tNNG,*,T,사람,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(option), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[남희석은/EOJEOL/null/1/1/0/4, 남희석/NNP/인명/0/1/0/3]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[충남/NNP/지명/1/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[사람이다/EOJEOL/null/1/1/7/11, 사람/NNG/null/0/1/7/9]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}