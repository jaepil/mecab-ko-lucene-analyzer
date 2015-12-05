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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.util.Map;

/**
 * MeCabKo Tokenizer Factory 추상 클래스
 * @author bibreen <bibreen@gmail.com>
 */
public abstract class TokenizerFactoryBase extends TokenizerFactory {
  protected PosAppender posAppender;
  protected TokenizerOption option;

  public TokenizerFactoryBase(Map<String, String> args) {
    super(args);
    option = new TokenizerOption();
    setDefaultOption();
    setMeCabArgs(args);
    setCompoundNounMinLength(args);
    setUseAdjectiveAndVerbOriginalForm(args);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  protected void setDefaultOption() {
    return;
  }

  abstract protected void setPosAppender();

  protected void setMeCabArgs(Map<String, String> args) {
    option.mecabArgs = get(args, "mecabArgs", option.mecabArgs);
  }
  
  protected void setCompoundNounMinLength(Map<String,String> args) {
    option.compoundNounMinLength = getInt(
        args, "compoundNounMinLength", option.compoundNounMinLength);
  }

  protected void setUseAdjectiveAndVerbOriginalForm(Map<String,String> args) {
    option.useAdjectiveAndVerbOriginalForm = getBoolean(
        args,
        "useAdjectiveAndVerbOriginalForm",
        option.useAdjectiveAndVerbOriginalForm);
  }

  @Override
  public Tokenizer create(AttributeFactory factory) {
    return new MeCabKoTokenizer(
        factory,
        option,
        new StandardPosAppender(option));
  }
}