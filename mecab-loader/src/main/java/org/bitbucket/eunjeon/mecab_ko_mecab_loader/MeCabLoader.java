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

import org.chasen.mecab.Model;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;

public final class MeCabLoader {
  private static Map<String, Model> models = new WeakHashMap<>();
  static {
    AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
      try {
        System.loadLibrary("MeCab");
      } catch (UnsatisfiedLinkError e) {
//        System.err.println(
//                "Cannot load the native code.\n"
//                        + "Make sure your LD_LIBRARY_PATH contains MeCab.so path.\n" + e);
//        System.exit(1);
        throw new UnsatisfiedLinkError(
            "Cannot load the native code.\n"
                + "Make sure your LD_LIBRARY_PATH contains MeCab.so path.\n" + e);
      }
      return null;
    });
  }

  public static synchronized Model getModel(String args) throws RuntimeException {
    Model model = models.get(args);
    if (model == null) {
      model = new Model(args);
      models.put(args, model);
    }
    return model;
  }

  public static int getModelCount() {
    return models.size();
  }
}
