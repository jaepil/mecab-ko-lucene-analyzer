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

import java.util.LinkedList;

/**
 * 품사 객체(Pos)를 받아서 어절을 구성하는 클래스.
 *
 * @author bibreen <bibreen@gmail.com>
 */
class Eojeol {
  private PosAppender appender;
  private int compoundNounMinLength;
  private boolean hasCompoundNoun;

  private LinkedList<Pos> posList = new LinkedList<Pos>();
  private String term = "";

  Eojeol(PosAppender appender, int compoundNounMinLength) {
    this.appender = appender;
    this.compoundNounMinLength = compoundNounMinLength;
    this.hasCompoundNoun = false;
  }

  public boolean append(Pos pos) {
    if (isAppendable(pos)) {
      if (pos.isPosIdOf(PosIdManager.PosId.COMPOUND) &&
              pos.getSurfaceLength() >= compoundNounMinLength) {
        this.hasCompoundNoun = true;
        posList.addAll(TokenGenerator.getAnalyzedPoses(pos));
      } else {
        posList.add(pos);
      }
      term += pos.getSurface();
      return true;
    } else {
      return false;
    }
  }

  private boolean isAppendable(Pos pos) {
    return posList.isEmpty() || appender.isAppendable(posList.getLast(), pos);
  }

  /**
   * Eojeol에 있는 Pos를 조합하여, Token이 되어야 하는 Pos를 생성한다.
   * @return token이 있을 경우 Pos의 리스트를 반환하고, 뽑아낼 token이 없을 경우
   * null을 반환한다.
   */
  public LinkedList<Pos> generateTokens() {
    if (isSkippable()) {
      return null;
    }
    return appender.getTokensFrom(this);
  }

  public boolean isSkippable() {
    return posList.isEmpty() ||
            (posList.size() == 1 && appender.isSkippablePos(posList.get(0)));
  }

  public int getNumPoses() {
    return posList.size();
  }

  public Pos getPos(int index) {
    return posList.get(index);
  }

  public LinkedList<Pos> getPosList() {
    return posList;
  }

  public String getTerm() {
    return term;
  }

  public int getStartOffset() {
    return posList.getFirst().getStartOffset();
  }

  public boolean hasCompoundNoun() {
    return hasCompoundNoun;
  }

  public void setToCompoundNoun() {
    hasCompoundNoun = true;
  }

  public void clear() {
    posList.clear();
    term = "";
  }

  @Override
  public String toString() {
    return posList.toString();
  }
}
