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

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.apache.solr.core.SolrResourceLoader;

/**
 * 문서 유사도 측정용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다. (실험적인)
 *   - mecabDicDir: mecab-ko-dic 사전 경로. 디폴트 경로는 /usr/local/lib/mecab/dic/mecab-ko-dic 이다.
 *   - compoundNounMinLength: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 9999이다. (복합명사 분해 안함)
 *
 * <pre>
 * {@code
 * <fieldType name="text_ko" class="solr.TextField" positionIncrementGap="100">
 *   <analyzer type="index">
 *     <tokenizer class="org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.SimilarityMeasureTokenizerFactory"
 *                mecabDicDir="/usr/local/lib/mecab/dic/mecab-ko-dic"
 *                compoundNounMinLength="9999"/>
 *   </analyzer>
 * </fieldType>
 * }
 * </pre>
 *
 * @author bibreen <bibreen@gmail.com>
 */
public class SimilarityMeasureTokenizerFactory extends TokenizerFactory {
  public static final String DEFAULT_MECAB_DIC_DIR =
      "/usr/local/lib/mecab/dic/mecab-ko-dic";
  private String mecabDicDir;
  private int compoundNounMinLength;

  public SimilarityMeasureTokenizerFactory(Map<String,String> args) {
    super(args);
    setMeCabDicDir(args);
    setCompoundNounMinLength(args);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  private void setMeCabDicDir(Map<String,String> args) {
    String path = get(
        args,
        "mecabDicDir",
        SimilarityMeasureTokenizerFactory.DEFAULT_MECAB_DIC_DIR);
    if (path != null) {
      if (path.startsWith("/")) {
        mecabDicDir = path;
      } else {
        mecabDicDir = SolrResourceLoader.locateSolrHome() + path;
      }
    }
  }

  private void setCompoundNounMinLength(Map<String,String> args) {
    compoundNounMinLength = getInt(
        args,
        "compoundNounMinLength",
        TokenGenerator.NO_DECOMPOUND);
  }

  @Override
  public Tokenizer create(AttributeFactory factory, Reader input) {
    return new MeCabKoTokenizer(
        factory,
        input,
        mecabDicDir,
        new SimilarityMeasurePosAppender(),
        compoundNounMinLength);
  }
}
