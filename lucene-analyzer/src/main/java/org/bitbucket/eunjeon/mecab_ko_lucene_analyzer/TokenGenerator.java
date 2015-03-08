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

import java.util.*;

import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.PosIdManager.PosId;
import org.chasen.mecab.Node;
import org.chasen.mecab.MeCab;

/**
 * MeCab의 node를 받아서 Lucene tokenizer에 사용될 Pos 리스트를 생성하는 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public class TokenGenerator {
  public static final int NO_DECOMPOUND = 9999;
  public static final int DEFAULT_COMPOUND_NOUN_MIN_LENGTH = 3;

  private final PosAppender appender;
  private LinkedList<Pos> posList = new LinkedList<Pos>();
  private ListIterator<Pos> posIter;
  private int compoundNounMinLength;
  
  /**
   * TokenGenerator 생성자
   * 
   * @param appender PosAppender
   * @param compoundNounMinLength 복합명사에서 분해할 명사의 최소길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   * @param beginNode 시작 노드
   */
  public TokenGenerator(
      PosAppender appender, int compoundNounMinLength, Node beginNode) {
    this.appender = appender;
    this.compoundNounMinLength = compoundNounMinLength;
    convertNodeListToPosList(beginNode);
    posIter = posList.listIterator();
  }
  
  private void convertNodeListToPosList(Node beginNode) {
    Node node = beginNode.getNext();
    Pos prevPos = new Pos("", PosId.UNKNOWN, 0, 0, 0);
    while (!isEosNode(node)) {
      Pos curPos = new Pos(node, prevPos.getEndOffset());
      if (curPos.getPosId() == PosId.PREANALYSIS) {
        posList.addAll(getAnalyzedPoses(curPos));
      } else {
        posList.add(curPos);
      }
      prevPos = curPos;
      node = node.getNext();
    }
  }
  
  static private boolean isEosNode(Node node) {
    return node == null || node.getStat() == MeCab.MECAB_EOS_NODE;
  }
 
  /**
   * mecab-ko-dic의 인덱스 표현 문자열을 해석하여 품사(Pos) 리스트를 반환한다.
   */
  static public LinkedList<Pos> getAnalyzedPoses(Pos pos) {
    LinkedList<Pos> output = new LinkedList<Pos>();
    String indexExp = pos.getExpression();
    if (indexExp == null) {
      output.add(pos);
      return output;
    }
    String[] posExps = indexExp.split("\\+");
    if (posExps.length == 1) {
      output.add(pos);
      return output;
    }
    
    for (String posExp: posExps) {
      output.add(new Pos(posExp, 0));
    }
    // 분해된 POS의 offset 재계산
    Pos prevPos = null;
    for (Pos curPos: output) {
      if (prevPos == null) {
        curPos.setStartOffset(pos.getStartOffset());
        prevPos = curPos;
      } else {
        if (curPos.getPositionIncr() == 0) {
          curPos.setStartOffset(prevPos.getStartOffset());
        } else {
          curPos.setStartOffset(prevPos.getEndOffset());
          prevPos = curPos;
        }
      }
    }
    return output;
  }
  
  /**
   * 다음 어절의 Pos들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<Pos> getNextEojeolTokens() {
    Eojeol eojeol = new Eojeol(appender, compoundNounMinLength);
    while (posIter.hasNext()) {
      Pos curPos = posIter.next();
      if (!eojeol.append(curPos)) {
        posIter.previous();
        LinkedList<Pos> poses = eojeol.generateTokens();
        if (poses != null) {
          return poses;
        } else {
          eojeol.clear();
        }
      }
    }
    // return last eojeol tokens
    return eojeol.generateTokens();
  }
}