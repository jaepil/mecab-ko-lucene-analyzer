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
package org.bitbucket.eunjeon.mecab_ko_mecab_loader;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Tagger;

public final class MeCabLoader {
  private volatile static MeCabLoader uniqueInstance;
  private static Model model;
  private static Tagger tagger;
  static {
    try {
      System.loadLibrary("MeCab");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Cannot load the native code.\n"
          + "Make sure your LD_LIBRARY_PATH contains MeCab.so path.\n" + e);
      System.exit(1);
    }
  }
 
  public static MeCabLoader getInstance(String dicDir)
      throws NullPointerException, RuntimeException {
    // DCL(Double-checking Locking) using Volatile Singleton. thread-safe
    // http://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java 참조
    MeCabLoader result = uniqueInstance;
    if (result == null) {
      synchronized (MeCabLoader.class) {
        result = uniqueInstance;
        if (result == null) {
          uniqueInstance = result = new MeCabLoader(dicDir);
        }
      }
    }
    return result;
  }
  
  private MeCabLoader(String dicDir) {
    model = new Model("-d " + dicDir);
    tagger = model.createTagger();
  }

  public Tagger getTagger() {
    return tagger;
  }
  
  public Lattice createLattice() {
    return model.createLattice();
  }
}
