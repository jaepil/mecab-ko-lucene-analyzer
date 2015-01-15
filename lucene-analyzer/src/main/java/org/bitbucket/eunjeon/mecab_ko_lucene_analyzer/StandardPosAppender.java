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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.PosIdManager.PosId;

/**
 * 표준 tokenizer를 위한 PosAppender.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public class StandardPosAppender extends PosAppender {
  static public Set<Appendable> appendableSet;
  
  static {
    appendableSet = new HashSet<Appendable>();
   
    // Appenable HashSet 구성
    // 사전에 없는 단어(UNKNOWN)은 체언이라고 가정한다.
    
    // 어미(E) + 어미(E)
    appendableSet.add(new Appendable(PosId.E, PosId.E));
    // 어근(XR) + E [+ E]*
    appendableSet.add(new Appendable(PosId.XR, PosId.E));
    // 용언(V*)|동사 파생 접미사(XSV)|형용사 파생 접미사(XSA) + E [+ E]*
    appendableSet.add(new Appendable(PosId.VV, PosId.E));
    appendableSet.add(new Appendable(PosId.VA, PosId.E));
    appendableSet.add(new Appendable(PosId.VX, PosId.E));
    appendableSet.add(new Appendable(PosId.VCP, PosId.E));
    appendableSet.add(new Appendable(PosId.VCN, PosId.E));
    appendableSet.add(new Appendable(PosId.XSV, PosId.E));
    appendableSet.add(new Appendable(PosId.XSA, PosId.E));
    // 체언(N*)|일반부사(MAG)|어근(XR) + 동사 파생 접미사(XSV)
    appendableSet.add(new Appendable(PosId.N, PosId.XSV));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.XSV));
    appendableSet.add(new Appendable(PosId.MAG, PosId.XSV));
    appendableSet.add(new Appendable(PosId.XR, PosId.XSV));
    appendableSet.add(new Appendable(PosId.UNKNOWN, PosId.XSV));
    // 체언(N*)|일반부사(MAG)|어근(XR) + 형용사 파생 접미사(XSA)
    appendableSet.add(new Appendable(PosId.N, PosId.XSA));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.XSA));
    appendableSet.add(new Appendable(PosId.MAG, PosId.XSA));
    appendableSet.add(new Appendable(PosId.XR, PosId.XSA));
    appendableSet.add(new Appendable(PosId.UNKNOWN, PosId.XSA));
    // 체언(N*)|명사 파생 접미사(XSN) + 긍정지정사(VCP)
    appendableSet.add(new Appendable(PosId.N, PosId.VCP));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.VCP));
    appendableSet.add(new Appendable(PosId.XSN, PosId.VCP));
    appendableSet.add(new Appendable(PosId.UNKNOWN, PosId.VCP));
    // 체언(N*) + 조사 [+ 조사]*
    appendableSet.add(new Appendable(PosId.N, PosId.J));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.J));
    appendableSet.add(new Appendable(PosId.UNKNOWN, PosId.J));
    // 명사 파생 접미사(XSN) + 조사(J)
    appendableSet.add(new Appendable(PosId.XSN, PosId.J));
    // 어미(E) + 조사(J) - 어미가 명사형 전성 어미인 경우
    appendableSet.add(new Appendable(PosId.E, PosId.J));
    // 어미(E) + 보조 용언(VX) - 어미가 연결 어미인 경우
    // '보조 용언은 띄어 씀을 원칙으로 하되, 경우에 따라 붙여 씀도 허용한다.'
    // (http://www.korean.go.kr/09_new/dic/rule/rule01_0503.jsp)
    // 위와 같은 이유로 어미와 보조 용언는 붙이지 않는다.
    // appendableSet.add(new Appendable(PosId.E, PosId.VX));
    // 부사(MAG) + 조사(J)
    appendableSet.add(new Appendable(PosId.MAG, PosId.J));
    // 조사(J) + 조사(J)
    appendableSet.add(new Appendable(PosId.J, PosId.J));
    // 외국어(SL) + 조사(J)
    appendableSet.add(new Appendable(PosId.SL, PosId.J));
    // 한자(SH) + 조사(J)
    appendableSet.add(new Appendable(PosId.SH, PosId.J));
  }

  @Override
  public boolean isAppendable(Pos left, Pos right) {
    if (right.getNode() != null && right.hasSpace()) {
      return false;
    }
    return appendableSet.contains(
        new Appendable(left.getEndPosId(), right.getStartPosId()));
  }

  @Override
  public boolean isSkippablePos(Pos pos) {
    // 단독으로 쓰인 심볼은 token 생성 제외한다.
    PosId posId = pos.getPosId();
    return posId == PosId.SF ||
        posId.in(PosId.SP, PosId.SE);
  }

  @Override
  public LinkedList<Pos> getTokensFrom(Eojeol eojeol) {
    LinkedList<Pos> output = getAdditionalPosesFrom(eojeol);
    insertEojeolPosTo(eojeol, output);
    return output;
  }

  private LinkedList<Pos> getAdditionalPosesFrom(Eojeol eojeol) {
    LinkedList<Pos> poses = eojeol.getPosList();
    if (eojeol.hasCompoundNoun()) {
      LinkedList<Pos> output = new LinkedList<Pos>();
      // TODO: 이해하기 어려운 코드 리팩토링 해보자
      Pos prevPos = null;
      for (Pos pos: poses) {
        if (!isAbsolutePos(pos)) {
          break;
        }

        output.add(pos);
        if (prevPos == null) {
          prevPos = pos;
        } else {
          Pos compound = prevPos.append(pos, PosId.COMPOUND, 0);
          output.add(1, compound);
          prevPos = compound;
        }
      }
      return output;
    } else {
      LinkedList<Pos> output = new LinkedList<Pos>();
      for (Pos pos: poses) {
        if (isAbsolutePos(pos)) {
          pos.setPositionIncr(0);
          output.add(pos);
        }
        if (pos.isPosIdOf(PosId.INFLECT)) {
          Pos firstPos = extractFirstPos(pos);
          if (isAbsolutePos(firstPos) &&
                  firstPos.getSurfaceLength() <= pos.getSurfaceLength()) {
            output.add(firstPos);
          }
        }
      }
      return output;
    }
  }

  private Pos insertEojeolPosTo(Eojeol eojeol, LinkedList<Pos> eojeolTokens) {
    Pos eojeolPos;
    LinkedList<Pos> posList = eojeol.getPosList();
    if (posList.size() == 1) {
      if (eojeolTokens.isEmpty()) {
        eojeolTokens.add(posList.getFirst());
      }
      if (eojeolTokens.getFirst() != posList.getFirst()) {
        eojeolTokens.addFirst(posList.getFirst());
      }
      eojeolPos = eojeolTokens.getFirst();
      eojeolPos.setPositionIncr(1);
    } else {
      if (eojeol.hasCompoundNoun()) {
        int positionLength = recalcEojeolPositionLength(eojeolTokens);
        eojeolPos = new Pos(
            eojeol.getTerm(), PosId.EOJEOL,
            eojeol.getStartOffset(), 0, positionLength);
        eojeolPos.setPos(concatMophemes(posList));
        if (eojeolTokens.size() < 2 ||
            !eojeolPos.equalsOffset(eojeolTokens.get(1))) {
          eojeolTokens.add(1, eojeolPos);
        }
      } else {
        eojeolPos = new Pos(
            eojeol.getTerm(), PosId.EOJEOL, eojeol.getStartOffset(), 1, 1);
        eojeolPos.setPos(concatMophemes(posList));
        eojeolTokens.addFirst(eojeolPos);
      }
    }
    return eojeolPos;
  }

  private String concatMophemes(List<Pos> poses) {
    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < poses.size(); i++) {
      if (i != 0) {
        buff.append("+");
      }
      buff.append(poses.get(i).getMophemes());
    }
    return buff.toString();
  }

  private int recalcEojeolPositionLength(LinkedList<Pos> eojeolTokens) {
    int positionLength = 0;
    for (Pos pos: eojeolTokens) {
      positionLength += pos.getPositionIncr();
    }
    return positionLength;
  }

  /**
   * 단독으로 쓰일 수 있는 형태소인지를 판단한다.
   *
   * @param pos 형태소 품사.
   */
  private boolean isAbsolutePos(Pos pos) {
    return (pos.isPosIdOf(PosId.COMPOUND) ||
        pos.isPosIdOf(PosId.MAG) ||
        pos.isPosIdOf(PosId.N) ||
        pos.isPosIdOf(PosId.XR) ||
        pos.isPosIdOf(PosId.SH) ||
        pos.isPosIdOf(PosId.SL) ||
        pos.isPosIdOf(PosId.SN) ||
        pos.isPosIdOf(PosId.UNKNOWN) ||
        pos.isPosIdOf(PosId.VA) ||
        pos.isPosIdOf(PosId.VV) ||
        pos.isPosIdOf(PosId.XPN) ||
        pos.isPosIdOf(PosId.XSN)
    );
  }

  /**
   * Infelct Pos에서 첫번째 pos를 가져온다.
   *
   * @param inflectPos
   * @return pos 형태소 품사
   */
  private Pos extractFirstPos(Pos inflectPos) {
    if (!inflectPos.isPosIdOf(PosId.INFLECT)) {
      return null;
    }
    String first = inflectPos.getExpression().split("\\+")[0];
    String[] datas = first.split("/");
    if (datas.length != 2) {
      return null;
    }
    String surface = datas[0];
    PosId posId = PosId.convertFrom(datas[1]);
    int startOffset = inflectPos.getStartOffset();
    return new Pos(surface, posId, startOffset, 0, 1);
  }
}