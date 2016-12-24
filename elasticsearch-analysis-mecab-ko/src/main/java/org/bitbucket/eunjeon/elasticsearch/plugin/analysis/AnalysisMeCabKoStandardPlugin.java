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
package org.bitbucket.eunjeon.elasticsearch.plugin.analysis;

import org.bitbucket.eunjeon.elasticsearch.index.analysis.*;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AnalysisMeCabKoStandardPlugin extends Plugin implements AnalysisPlugin {
  @Override
  public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
    Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> tokenizers = new HashMap<>();

    tokenizers.put("mecab_ko_standard_tokenizer", MeCabKoStandardTokenizerFactory::new);
    tokenizers.put("mecab_ko_similarity_measure_tokenizer", MeCabKoSimilarityMeasureTokenizerFactory::new);
    tokenizers.put("mecab_ko_keyword_search_tokenizer", MeCabKoKeywordSearchTokenizerFactory::new);

    return tokenizers;
  }
}