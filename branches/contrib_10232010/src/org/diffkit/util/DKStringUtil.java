/**
 * Copyright 2010 Joseph Panico
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
 */
package org.diffkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.diffkit.common.DKValidate;

/**
 * @author jpanico
 */
public class DKStringUtil {
   public enum Quote {
      SINGLE('\''), DOUBLE('"');

      public final char _character;

      private Quote(char character_) {
         _character = character_;
      }
   }

   private static final Logger LOG = LoggerFactory.getLogger(DKStringUtil.class);

   private DKStringUtil() {
   }

   public static Boolean parseBoolean(String target_) {
      if (target_ == null)
         return null;
      if ((target_.equalsIgnoreCase("true")) || (target_.equalsIgnoreCase("yes")))
         return Boolean.TRUE;
      if ((target_.equalsIgnoreCase("false")) || (target_.equalsIgnoreCase("no")))
         return Boolean.FALSE;
      return null;
   }

   /**
    * assumes comma separator
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static List<?> parseEnumList(String target_, Class enumClass_) {
      if (target_ == null)
         return null;
      String[] elements = target_.split(",");
      if (ArrayUtils.isEmpty(elements))
         return null;
      List<Enum> values = new ArrayList<Enum>(elements.length);
      for (String element : elements) {
         element = StringUtils.trimToNull(element);
         if (element == null)
            continue;
         values.add(Enum.valueOf(enumClass_, element));
      }
      return values;
   }

   /**
    * assumes comma separator
    */
   public static List<Integer> parseIntegerList(String target_) {
      if (target_ == null)
         return null;
      String[] elements = target_.split(",");
      if (ArrayUtils.isEmpty(elements))
         return null;
      List<Integer> values = new ArrayList<Integer>(elements.length);
      for (String element : elements)
         values.add(NumberUtils.createInteger(element));
      return values;
   }

   /**
    * null and Exception safe
    */
   public static URI createURI(String target_) {
      if (target_ == null)
         return null;
      try {
         return new URI(target_);
      }
      catch (Exception e_) {
         LOG.error(null, e_);
         return null;
      }
   }

   public static String packageNameToResourcePath(String packageName_) {
      if (packageName_ == null)
         return null;
      return packageName_.replace('.', '/') + "/";
   }

   public static String quote(String target_, Quote kind_) {
      if (target_ == null)
         return null;
      DKValidate.notNull(kind_);
      if (!(target_.length() == 0) && (target_.charAt(0) == kind_._character))
         return target_;
      return kind_._character + target_ + kind_._character;
   }

   /**
    * convenience that calls unquote(String, Quote) for each element <br/>
    * 
    * N.B. actually modifies target_
    */
   public static void unquote(String[] target_, Quote kind_) {
      DKValidate.notNull(kind_);
      if (ArrayUtils.isEmpty(target_))
         return;
      for (int i = 0; i < target_.length; i++)
         target_[i] = unquote(target_[i], kind_);
   }

   public static String unquote(String target_, Quote kind_) {
      DKValidate.notNull(kind_);
      if (target_ == null)
         return null;
      if (target_.length() < 2)
         return target_;
      char[] chars = target_.toCharArray();
      if (!((chars[0] == kind_._character) && (chars[chars.length - 1] == kind_._character)))
         return target_;
      return new String(ArrayUtils.subarray(chars, 1, chars.length - 1));
   }

   /**
    * parenthesis bracketed, comma separated
    */
   public static <T> String toSetString(List<T> target_) {
      if (target_ == null)
         return toSetString((Object[]) null);
      return toSetString(target_.toArray());
   }

   /**
    * parenthesis bracketed, comma separated
    */
   public static <T> String toSetString(T[] target_) {
      if (ArrayUtils.isEmpty(target_))
         return "()";

      StringBuilder builder = new StringBuilder();
      builder.append("(");
      for (int i = 0; i < target_.length; i++) {
         builder.append(target_[i].toString());
         if (i < (target_.length - 1))
            builder.append(", ");
      }
      builder.append(")");
      return builder.toString();
   }

   /**
    * @param startIndex_
    *           inclusive, as in java.lang.String subrange methods
    * @param endIndex_
    *           exclusive, as in java.lang.String subrange methods
    */
   public static int countMatches(String target_, char match_, int startIndex_,
                                  int endIndex_) {
      if (target_ == null)
         return 0;
      int targetLength = target_.length();
      if (startIndex_ < 0 || startIndex_ >= targetLength)
         throw new IllegalArgumentException(String.format(
            "startIndex_->%s out of bounds", startIndex_));
      if (endIndex_ < 0 || endIndex_ > targetLength)
         throw new IllegalArgumentException(String.format("endIndex_->%s out of bounds",
            startIndex_));
      if (startIndex_ >= endIndex_)
         return 0;

      char[] chars = target_.toCharArray();
      if (chars == null)
         return 0;

      int count = 0;
      for (int i = startIndex_; i < endIndex_; i++)
         if (chars[i] == match_)
            count++;
      return count;
   }

   @SuppressWarnings("unchecked")
   public static String replaceEach(String target_, Map<String, String> replacements_) {
      if (target_ == null)
         return null;
      if (MapUtils.isEmpty(replacements_))
         return target_;
      Object[] entries = replacements_.entrySet().toArray();
      String[] originals = new String[entries.length];
      String[] subs = new String[entries.length];
      for (int i = 0; i < entries.length; i++) {
         originals[i] = ((Map.Entry<String, String>) entries[i]).getKey();
         subs[i] = ((Map.Entry<String, String>) entries[i]).getValue();
      }
      return StringUtils.replaceEach(target_, originals, subs);
   }
}
