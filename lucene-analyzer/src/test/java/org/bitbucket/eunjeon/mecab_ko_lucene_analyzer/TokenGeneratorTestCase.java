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
import org.bitbucket.eunjeon.mecab_ko_mecab_loader.MeCabLoader;
import org.chasen.mecab.Model;
import org.chasen.mecab.Node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenGeneratorTestCase {
  Model model;
  public TokenGeneratorTestCase() {
    /* TokenGenerator에서 MeCab의 JNI를 사용하는 부분이 있기때문에, MeCab를 미리
     * 적재한다.
     */
    model = MeCabLoader.getModel("-d /usr/local/lib/mecab/dic/mecab-ko-dic");
  }
  
  public static Node mockNodeListFactory(String[] posStrings) {
    Node nextNode = null;
    for (int i = posStrings.length - 1; i >= 0; --i) {
      Node node = mockNodeFactory(posStrings[i], nextNode);
      nextNode = node;
    }
    return mockBeginNode(nextNode);
  }
  
  public static Node mockNodeFactory(String posString, Node next) {
    String[] surfaceAndFeature = posString.split("\t");
    if (surfaceAndFeature.length != 2) {
      throw new IllegalArgumentException("Invalid POS string");
    }
    String surface = surfaceAndFeature[0].trim();
    String feature = surfaceAndFeature[1].trim();
    
    Node node = mock(Node.class);
    when(node.getSurface()).thenReturn(surface);
    when(node.getPosid()).thenReturn(getPosId(feature));
    when(node.getRlength()).thenReturn(surfaceAndFeature[0].length());
    when(node.getLength()).thenReturn(surface.length());
    when(node.getFeature()).thenReturn(feature);
    when(node.getNext()).thenReturn(next);
    return node;
  }
  
  private static Node mockBeginNode(Node next) {
    Node node = mock(Node.class);
    when(node.getSurface()).thenReturn("BOS");
    when(node.getPosid()).thenReturn(0);
    when(node.getRlength()).thenReturn(0);
    when(node.getLength()).thenReturn(0);
    when(node.getFeature()).thenReturn("");
    when(node.getNext()).thenReturn(next);
    return node;
  }
  
  private static int getPosId(String feature) {
    String[] features = feature.split(",");
    String pos = features[Pos.NodeIndex.POS];
    String type = features[Pos.NodeIndex.TYPE];
    if (type.equals("Compound")) {
      return PosId.COMPOUND.getNum();
    } else if (type.equals("Inflect")) {
      return PosId.INFLECT.getNum();
    } else if (type.equals("Preanalysis")) {
      return PosId.PREANALYSIS.getNum();
    } else {
      return PosId.convertFrom(pos).getNum();
    }
  }
}
