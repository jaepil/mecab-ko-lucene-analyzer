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
package org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 독립적인 token으로 생성되는 체언 접두사인지 아닌지를 판별하는 클래스
 */
public class XpnDiscriminator {
  static public Set<String> xpnSet;
  static {
    xpnSet = new HashSet<>();
    xpnSet.add("건");
    xpnSet.add("고");
    xpnSet.add("급");
    xpnSet.add("날");
    xpnSet.add("냉");
    xpnSet.add("대");
    xpnSet.add("맞");
    xpnSet.add("맨");
    xpnSet.add("맹");
    xpnSet.add("명");
    xpnSet.add("민");
    xpnSet.add("반");
    xpnSet.add("범");
    xpnSet.add("본");
    xpnSet.add("생");
    xpnSet.add("성");
    xpnSet.add("신");
    xpnSet.add("온");
    xpnSet.add("왕");
    xpnSet.add("원");
    xpnSet.add("재");
    xpnSet.add("주");
    xpnSet.add("참");
    xpnSet.add("총");
    xpnSet.add("친");
    xpnSet.add("탈");
    xpnSet.add("통");
    xpnSet.add("폐");
    xpnSet.add("풋");
    xpnSet.add("한");
    xpnSet.add("항");
    xpnSet.add("헛");
  }

  public static boolean isIndependentXpn(String surface) {
    return XpnDiscriminator.xpnSet.contains(surface);
  }
}
