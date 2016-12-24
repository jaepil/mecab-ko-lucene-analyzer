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

import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * 문서 유사도 측적용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다. (실험적인)
 *   - mecab_args: mecab 실행옵션. 디폴트 값은 "-d /usr/local/lib/mecab/dic/mecab-ko-dic/" 이다.
 *     mecab 실행 옵션은 다음의 URL을 참조. http://mecab.googlecode.com/svn/trunk/mecab/doc/mecab.html
 *   - compound_noun_min_length: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 9999이다. (복합명사 분해 안함)
 *
 * @author bibreen <bibreen@gmail.com>
 */
public class MeCabKoSimilarityMeasureTokenizerFactory extends MeCabKoTokenizerFactoryBase {
  public MeCabKoSimilarityMeasureTokenizerFactory(IndexSettings indexSettings,
                                                  Environment environment,
                                                  String name,
                                                  Settings settings) {
    super(indexSettings, environment, name, settings);
  }

  protected void setDefaultOption() {
    option.compoundNounMinLength = TokenizerOption.NO_DECOMPOUND;
    option.useAdjectiveAndVerbOriginalForm = false;
  }

  @Override
  protected void setPosAppender() {
    posAppender = new SimilarityMeasurePosAppender(option);
  }
}
