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

/**
 * Tokenizer 옵션
 */
public class TokenizerOption {
  public static final int NO_DECOMPOUND = 9999;

  /** mecab 실행옵션(ex: -d /usr/local/lib/mecab/dic/mecab-ko-dic/) */
  public String mecabArgs = "-d /usr/local/lib/mecab/dic/mecab-ko-dic/";
  /** 분해를 해야하는 복합명사의 최소 길이. */
  public int compoundNounMinLength = 3;
  /** 동사, 형용사 원형 검색 여부 */
  public boolean useAdjectiveAndVerbOriginalForm = true;
  // boolean useHanjaRead = false;
}
