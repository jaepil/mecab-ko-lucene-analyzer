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

import java.util.Map;

/**
 * 문서 유사도 측정용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다. (실험적인)
 *   - mecabArgs: mecab 실행옵션. 디폴트 값은 "-d /usr/local/lib/mecab/dic/mecab-ko-dic/" 이다.
 *     mecab 실행 옵션은 다음의 URL을 참조. http://mecab.googlecode.com/svn/trunk/mecab/doc/mecab.html
 *   - compoundNounMinLength: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 9999이다. (복합명사 분해 안함)
 *
 * <pre>
 * {@code
 * <fieldType name="text_ko" class="solr.TextField" positionIncrementGap="100">
 *   <analyzer type="index">
 *     <tokenizer class="org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.SimilarityMeasureTokenizerFactory"
 *                mecabArgs="-d /usr/local/lib/mecab/dic/mecab-ko-dic"
 *                compoundNounMinLength="9999"/>
 *   </analyzer>
 * </fieldType>
 * }
 * </pre>
 *
 * @author bibreen <bibreen@gmail.com>
 */
public class SimilarityMeasureTokenizerFactory extends TokenizerFactoryBase {
  public SimilarityMeasureTokenizerFactory(Map<String,String> args) {
    super(args);
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
