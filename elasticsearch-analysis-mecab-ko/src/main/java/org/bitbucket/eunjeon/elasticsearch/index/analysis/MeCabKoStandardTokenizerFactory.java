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
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.Reader;

/**
 * 표준 index용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다.
 *   - mecab_args: mecab 실행옵션
 *   - compound_noun_min_length: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 3이다.
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public class MeCabKoStandardTokenizerFactory extends AbstractTokenizerFactory {
  private static final String DEFAULT_MECAB_DIC_DIR =
      "/usr/local/lib/mecab/dic/mecab-ko-dic";
  private static final String DEFAULT_MECAB_RC_FILE =
      "/usr/local/etc/mecabrc";
  protected String mecabArgs;
  protected int compoundNounMinLength;

  @Inject
  public MeCabKoStandardTokenizerFactory(
      Index index,
      @IndexSettings Settings indexSettings,
      @Assisted String name,
      @Assisted Settings settings) {
    super(index, indexSettings, name, settings);
    setMeCabArgs(settings);
    setCompoundNounMinLength(settings);
  }
  
  private void setMeCabArgs(Settings settings) {
    mecabArgs = settings.get(
        "mecab_args", "-d " + MeCabKoStandardTokenizerFactory.DEFAULT_MECAB_DIC_DIR);
  }
  
  private void setCompoundNounMinLength(Settings settings) {
    compoundNounMinLength = settings.getAsInt(
        "compound_noun_min_length",
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
  }

  @Override
  public Tokenizer create(Reader reader) {
    return new MeCabKoTokenizer(
        reader,
        mecabArgs,
        new StandardPosAppender(),
        compoundNounMinLength);
  }
}
