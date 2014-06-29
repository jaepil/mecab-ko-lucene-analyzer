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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Tagger;

public final class MeCabLoader {
  private Model model;

  private volatile static Map<String, MeCabLoader> loaders = 
          new HashMap<String, MeCabLoader>();

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
    if (!loaders.containsKey(dicDir)) {
      synchronized (MeCabLoader.class) {
        if (!loaders.containsKey(dicDir)) {
          loaders.put(dicDir, new MeCabLoader(dicDir));
          System.out.println("mecab analyzer is loaded from " + dicDir);
        }
      }
    }
    return loaders.get(dicDir);
  }
  
  private MeCabLoader(String dicDir) {
    model = new Model("-d " + dicDir);
  }

  public Tagger createTagger() {
    return model.createTagger();
  }
  
  public Lattice createLattice() {
    return model.createLattice();
  }
}
