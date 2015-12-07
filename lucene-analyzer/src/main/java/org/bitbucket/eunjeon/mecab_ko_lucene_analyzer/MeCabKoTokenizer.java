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

import java.io.IOException;
import java.util.Queue;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.tokenattributes.PartOfSpeechAttribute;
import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.tokenattributes.SemanticClassAttribute;
import org.bitbucket.eunjeon.mecab_ko_mecab_loader.MeCabLoader;
import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Tagger;

/**
 * Lucene/Solr용 Tokenizer.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public final class MeCabKoTokenizer extends Tokenizer {
  private CharTermAttribute charTermAtt;
  private PositionIncrementAttribute posIncrAtt;
  private PositionLengthAttribute posLenAtt;
  private OffsetAttribute offsetAtt;
  private TypeAttribute typeAtt;
  private PartOfSpeechAttribute posAtt;
  private SemanticClassAttribute semanticClassAtt;
 
  private String document;
  private TokenizerOption option;
  private Model model;
  private Lattice lattice;
  private Tagger tagger;
  private PosAppender posAppender;
  private TokenGenerator generator;
  private Queue<Pos> tokensQueue;
  
  /**
   * MeCabKoTokenizer 생성자.
   * Default AttributeFactory 사용.
   * 
   * @param option Tokenizer 옵션
   * @param appender PosAppender
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   */
  public MeCabKoTokenizer(
      TokenizerOption option,
      PosAppender appender) {
    this(
        AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
        option,
        appender);
  }

  /**
   * MeCabKoTokenizer 생성자.
   * 
   * @param factory the AttributeFactory to use
   * @param option MeCabTokenizer 옵션
   * @param appender PosAppender
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   */
  public MeCabKoTokenizer(
      AttributeFactory factory,
      TokenizerOption option,
      PosAppender appender) {
    super(factory);
    posAppender = appender;
    this.option = option;
    setMeCab();
    setAttributes();
  }

  private void setMeCab() {
    model = MeCabLoader.getModel(option.mecabArgs);
    lattice = model.createLattice();
    tagger = model.createTagger();
  }
  
  private void setAttributes() {
    charTermAtt = addAttribute(CharTermAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    posLenAtt = addAttribute(PositionLengthAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
    typeAtt = addAttribute(TypeAttribute.class);
    posAtt = addAttribute(PartOfSpeechAttribute.class);
    semanticClassAtt = addAttribute(SemanticClassAttribute.class);
  }

  @Override
  public boolean incrementToken() throws IOException {
    clearAttributes();
    if (isBegin()) {
      document = getDocument();
      createTokenGenerator();
    }
    
    if (tokensQueue == null || tokensQueue.isEmpty()) {
      tokensQueue = generator.getNextEojeolTokens();
      if (tokensQueue == null) {
        return false;
      }
    }
    Pos token = tokensQueue.poll();
    setAttributes(token);
    return true;
  }

  private boolean isBegin() {
    return generator == null;
  }

  private void createTokenGenerator() {
    lattice.set_sentence(document);
    tagger.parse(lattice);
    this.generator = new TokenGenerator(
        posAppender, option.compoundNounMinLength, lattice.bos_node());
  }
  
  private void setAttributes(Pos token) {
    posIncrAtt.setPositionIncrement(token.getPositionIncr());
    posLenAtt.setPositionLength(token.getPositionLength());
    offsetAtt.setOffset(
        correctOffset(token.getStartOffset()),
        correctOffset(token.getEndOffset()));
    String term = token.getTokenString();
    charTermAtt.copyBuffer(term.toCharArray(), 0, term.length());
    typeAtt.setType(token.getPosId().toString());
    posAtt.setPartOfSpeech(token.getMophemes());
    semanticClassAtt.setSemanticClass(token.getSemanticClass());
  }
  
  @Override
  public final void end() throws IOException {
    super.end();
    // set final offset
    offsetAtt.setOffset(
        correctOffset(document.length()), correctOffset(document.length()));
    document = null;
    lattice.clear();
  }
  
  @Override
  public final void reset() throws IOException {
    super.reset();
    generator = null;
    tokensQueue = null;
  }

  private String getDocument() throws IOException {
    StringBuilder document = new StringBuilder();
    char[] tmp = new char[1024];
    int len;
    while ((len = input.read(tmp)) != -1) {
      document.append(new String(tmp, 0, len));
    }
    return document.toString().toLowerCase();
  }
}
