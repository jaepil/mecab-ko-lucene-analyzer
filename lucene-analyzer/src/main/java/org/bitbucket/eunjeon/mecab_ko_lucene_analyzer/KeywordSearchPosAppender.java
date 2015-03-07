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

import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.PosIdManager.PosId;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 체언과 용언 위주의 인덱싱 tokenizer를 위한 PosAppender.
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public class KeywordSearchPosAppender extends PosAppender {
  static public Set<Appendable> appendableSet;
  
  static {
    appendableSet = new HashSet<Appendable>();
   
    // Appenable HashSet 구성
    // 사전에 없는 단어(UNKNOWN)은 체언이라고 가정한다.
    
    // 어미(E) + 어미(E)
    appendableSet.add(new Appendable(PosId.E, PosId.E));
    // 용언(V*) + E [+ E]*
    appendableSet.add(new Appendable(PosId.VV, PosId.E));
    appendableSet.add(new Appendable(PosId.VA, PosId.E));
    appendableSet.add(new Appendable(PosId.VX, PosId.E));

    // 체언(N*) + 명사 파생 접미사(XSN)
    appendableSet.add(new Appendable(PosId.N, PosId.XSN));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.XSN));
    appendableSet.add(new Appendable(PosId.UNKNOWN, PosId.XSN));

    // 체언 접두사(XPN) + 체언(N*)
    appendableSet.add(new Appendable(PosId.XPN, PosId.N));
    appendableSet.add(new Appendable(PosId.XPN, PosId.COMPOUND));
    appendableSet.add(new Appendable(PosId.XPN, PosId.UNKNOWN));
  }

  @Override
  public boolean isAppendable(Pos left, Pos right) {
    if (right.getNode() != null && right.hasSpace()) {
      return false;
    }
    if (left.getPosId() == PosId.INFLECT &&
        !(left.getStartPosId() == PosId.VA ||
            left.getStartPosId() == PosId.VV)) {
      return false;
    }
    return appendableSet.contains(
        new Appendable(left.getEndPosId(), right.getStartPosId()));
  }

  @Override
  public boolean isSkippablePos(Pos pos) {
    PosId posId = pos.getPosId();
    switch (posId) {
      case COMPOUND:
      case N:
      case SL:
      case SH:
      case SN:
      case XR:
        return false;
      case INFLECT:
        return !(pos.getStartPosId() == PosId.VA ||
            pos.getStartPosId() == PosId.VV);
      default:
        return true;
    }
  }

  @Override
  public LinkedList<Pos> extractAdditionalPoses(LinkedList<Pos> poses) {
    LinkedList<Pos> output = new LinkedList<Pos>();
    for (Pos pos: poses) {
      if (isAbsolutePos(pos)) {
        pos.setPositionIncr(0);
        output.add(pos);
      }
    }
    return output;
  }

  /**
   * 단독으로 쓰일 수 있는 형태소인지를 판단한다.
   *
   * @param pos 형태소 품사.
   */
  private boolean isAbsolutePos(Pos pos) {
    return false;
  }
}