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

public class TokenGeneratorWithSimilarityMeasurePosAppenderTest
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
        new SimilarityMeasurePosAppender(option), TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/NNG/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃/NNG/null/1/1/4/5]", tokens.toString());
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
        new SimilarityMeasurePosAppender(option), TokenGenerator.NO_DECOMPOUND, node);

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
        new SimilarityMeasurePosAppender(option), TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어/NNG/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english/SL/null/1/1/3/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study/SL/null/1/1/12/17]", tokens.toString());
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
        new SimilarityMeasurePosAppender(option), TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[아이폰/NNP/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[5s/EOJEOL/null/1/1/3/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[32GB/EOJEOL/null/1/1/6/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[구입/NNG/null/1/1/11/13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[elasticsearch1.4.3/EOJEOL/null/1/1/16/34]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[릴리스/NNG/null/1/1/34/37]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testLongSentence() {
    Node node = mockNodeListFactory(new String[] {
        "이\tMM,~명사,F,이,*,*,*,*",
        "위원장\tNNG,*,T,위원장,Compound,*,*,위원/NNG/*+장/NNG/*",
        "은\tJX,*,T,은,*,*,*,*",
        "\"\tSY,*,*,*,*,*,*,*",
        "세계\tNNG,*,F,세계,*,*,*,*",
        "반도핑\tNNP,*,T,반도핑,*,*,*,*",
        "기구\tNNG,*,F,기구,*,*,*,*",
        "(\tSSO,*,*,*,*,*,*,*",
        "WADA\tSL,*,*,*,*,*,*,*",
        ")\tSSC,*,*,*,*,*,*,*",
        "의\tJKG,*,F,의,*,*,*,*",
        "1\tSN,*,*,*,*,*,*,*",
        "호\tNNBC,*,F,호,*,*,*,*",
        "금지\tNNG,*,F,금지,*,*,*,*",
        "약물\tNNG,*,T,약물,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*",
        "근육\tNNG,*,T,근육,*,*,*,*",
        "을\tJKO,*,T,을,*,*,*,*",
        "강화\tNNG,*,F,강화,*,*,*,*",
        "시키\tXSV,*,F,시키,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*",
        "효과\tNNG,*,F,효과,*,*,*,*",
        "가\tJKS,*,F,가,*,*,*,*",
        "있\tVA,*,T,있,*,*,*,*",
        "어\tEC,*,F,어,*,*,*,*",
        "순간\tNNG,*,T,순간,*,*,*,*",
        "적\tXSN,*,T,적,*,*,*,*",
        "으로\tJKB,*,F,으로,*,*,*,*",
        "폭발\tNNG,*,T,폭발,*,*,*,*",
        "적\tXSN,*,T,적,*,*,*,*",
        "인\tVCP+ETM,*,T,인,Inflect,VCP,ETM,이/VCP/*+ᆫ/ETM/*",
        "힘\tNNG,*,T,힘,*,*,*,*",
        "을\tJKO,*,T,을,*,*,*,*",
        "필요\tNNG,*,F,필요,*,*,*,*",
        "로\tJKB,*,F,로,*,*,*,*",
        "하\tVV,*,F,하,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*",
        "육상\tNNG,*,T,육상,*,*,*,*",
        ",\tSC,*,*,*,*,*,*,*",
        "수영\tNNG,*,T,수영,*,*,*,*",
        ",\tSC,*,*,*,*,*,*,*",
        "사이클\tNNG,*,T,사이클,*,*,*,*",
        "종목\tNNG,*,T,종목,*,*,*,*",
        "에서\tJKB,*,F,에서,*,*,*,*",
        "많이\tMAG,성분부사/정도부사,F,많이,*,*,*,*",
        "이용\tNNG,*,T,이용,*,*,*,*",
        "하\tXSV,*,F,하,*,*,*,*",
        "다\tEC,*,F,다,*,*,*,*",
        "적발\tNNG,*,T,적발,*,*,*,*",
        "된다\tXSV+EC,*,F,된다,Inflect,XSV,EC,되/XSV/*+ᆫ다/EC/*",
        "\"\tSY,*,*,*,*,*,*,*",
        "고\tJKQ,*,F,고,*,*,*,*",
        "했\tVV+EP,*,T,했,Inflect,VV,EP,하/VV/*+았/EP/*",
        "다\tEF,*,F,다,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*"
    });

    TokenGenerator generator = new TokenGenerator(
        new SimilarityMeasurePosAppender(option),
        TokenGenerator.NO_DECOMPOUND, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[위원장/COMPOUND/null/1/2/1/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[세계/NNG/null/1/1/6/8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[반도핑/NNP/null/1/1/8/11]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[기구/NNG/null/1/1/11/13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[WADA/SL/null/1/1/14/18]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[1/SN/null/1/1/20/21]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[호/NNBC/null/1/1/21/22]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[금지/NNG/null/1/1/22/24]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[약물/NNG/null/1/1/24/26]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[근육/NNG/null/1/1/29/31]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[강화/NNG/null/1/1/32/34]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[효과/NNG/null/1/1/37/39]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[순간적/EOJEOL/null/1/1/42/45]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[폭발적/EOJEOL/null/1/1/47/50]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[힘/NNG/null/1/1/51/52]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[필요/NNG/null/1/1/53/55]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[육상/NNG/null/1/1/58/60]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[수영/NNG/null/1/1/61/63]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[사이클/NNG/null/1/1/64/67]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[종목/NNG/null/1/1/67/69]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이용/NNG/null/1/1/73/75]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[적발/NNG/null/1/1/77/79]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}
