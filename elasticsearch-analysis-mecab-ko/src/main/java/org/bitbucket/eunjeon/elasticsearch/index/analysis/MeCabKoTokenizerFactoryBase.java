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

import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.*;
import org.bitbucket.eunjeon.mecab_ko_mecab_loader.MeCabLoader;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

/**
 * MeCabKo Tokenizer Factory 추상 클래스
 * @author bibreen <bibreen@gmail.com>
 */
public abstract class MeCabKoTokenizerFactoryBase
    extends AbstractTokenizerFactory {
  protected PosAppender posAppender;
  protected TokenizerOption option;

  private ESLogger logger = Loggers.getLogger(MeCabKoTokenizerFactoryBase.class, "mecab-ko");

  @Inject
  public MeCabKoTokenizerFactoryBase(
      Index index,
      IndexSettingsService indexSettingsService,
      @Assisted String name,
      @Assisted Settings settings) {
    super(index, indexSettingsService.getSettings(), name, settings);
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
