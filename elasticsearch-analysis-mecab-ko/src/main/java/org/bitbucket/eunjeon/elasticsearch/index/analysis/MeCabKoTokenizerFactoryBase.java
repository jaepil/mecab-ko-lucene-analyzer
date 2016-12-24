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
package org.bitbucket.eunjeon.elasticsearch.index.analysis;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.*;
import org.bitbucket.eunjeon.mecab_ko_mecab_loader.MeCabLoader;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

/**
 * MeCabKo Tokenizer Factory 추상 클래스
 * @author bibreen <bibreen@gmail.com>
 */
public abstract class MeCabKoTokenizerFactoryBase extends AbstractTokenizerFactory {
  protected PosAppender posAppender;
  protected TokenizerOption option;

  private Logger logger = ESLoggerFactory.getLogger("mecab-ko", MeCabKoTokenizerFactoryBase.class);

  public MeCabKoTokenizerFactoryBase(IndexSettings indexSettings,
                                     Environment environment,
                                     String name,
                                     Settings settings) {
    super(indexSettings, name, settings);
    option = new TokenizerOption();
    setDefaultOption();
    setMeCabArgs(settings);
    setCompoundNounMinLength(settings);
    setUseAdjectiveAndVerbOriginalForm(settings);
    setPosAppender();
  }

  protected void setDefaultOption() {
    return;
  }

  abstract protected void setPosAppender();

  protected void setMeCabArgs(Settings settings) {
    option.mecabArgs = settings.get("mecab_args", option.mecabArgs);
  }

  protected void setCompoundNounMinLength(Settings settings) {
    option.compoundNounMinLength = settings.getAsInt(
        "compound_noun_min_length", option.compoundNounMinLength);
  }

  protected void setUseAdjectiveAndVerbOriginalForm(Settings settings) {
    option.useAdjectiveAndVerbOriginalForm = settings.getAsBoolean(
        "use_adjective_and_verb_original_form",
        option.useAdjectiveAndVerbOriginalForm);
  }

  @Override
  public Tokenizer create() {
    logger.debug("already allocated model's count is #" + MeCabLoader.getModelCount());
    logger.debug("creating tokenizer from model " + option.mecabArgs);
    return new MeCabKoTokenizer(
        option,
        posAppender);
  }
}
