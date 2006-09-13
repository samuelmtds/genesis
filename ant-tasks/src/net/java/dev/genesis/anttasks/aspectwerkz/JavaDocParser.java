/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.anttasks.aspectwerkz;

import net.java.dev.genesis.anttasks.aspectwerkz.annotation.AnnotationPreprocessor;

import org.codehaus.backport175.com.thoughtworks.qdox.model.DocletTag;
import org.codehaus.backport175.compiler.CompilerException;
import org.codehaus.backport175.compiler.SourceLocation;
import org.codehaus.backport175.compiler.javadoc.RawAnnotation;

import java.util.Map;

public class JavaDocParser {
   public static RawAnnotation getRawAnnotation(final Class annotationClass,
         final String annotationName, final DocletTag tag,
         final String enclosingClassName, final String enclosingClassFileName,
         final Map annotationPreprocessors) {
      String rawAnnotationString = tag.getName() + " " + tag.getValue();
      rawAnnotationString = rawAnnotationString.trim();
      rawAnnotationString = removeFormattingCharacters(rawAnnotationString);

      if (rawAnnotationString.length() > annotationName.length()) {
         String rawValueString = rawAnnotationString.substring(annotationName
               .length());
         rawValueString = rawValueString.trim();

         char first = rawValueString.charAt(0);

         if (first == '(') {
            if (!rawValueString.endsWith(")")) {
               throw new CompilerException(
                     "annotation not well-formed, needs to end with a closing parenthesis ["
                           + rawAnnotationString + "]", SourceLocation.render(
                           annotationClass, tag, enclosingClassName,
                           enclosingClassFileName));
            }

            return new RawAnnotation(annotationClass, rawValueString.substring(
                  1, rawValueString.length() - 1), tag.getLineNumber(),
                  enclosingClassName, enclosingClassFileName);
         } else if (first == '"') {
            if (!rawValueString.endsWith("\"")) {
               throw new CompilerException(
                     "annotation not well-formed, needs to end with a closing \" ["
                           + rawAnnotationString + "]", SourceLocation.render(
                           annotationClass, tag, enclosingClassName,
                           enclosingClassFileName));
            }

            return new RawAnnotation(annotationClass, rawValueString, tag
                  .getLineNumber(), enclosingClassName, enclosingClassFileName);
         } else {
            // escape
            AnnotationPreprocessor preProcessor = (AnnotationPreprocessor) annotationPreprocessors
                  .get(annotationName);
            rawValueString = preProcessor.convert(rawValueString);

            /*
             * StringBuffer sb = new StringBuffer("\""); for (int i = 0; i <
             * rawValueString.length(); i++) { char c =
             * rawValueString.charAt(i); if (c == '\"') { sb.append("\\\""); }
             * else { sb.append(c); } } sb.append("\"");
             */
            return new RawAnnotation(annotationClass, rawValueString, tag
                  .getLineNumber(), enclosingClassName, enclosingClassFileName);
         }
      } else {
         return new RawAnnotation(annotationClass, "", tag.getLineNumber(),
               enclosingClassName, enclosingClassFileName);
      }
   }

   /**
    * Removes newline, carriage return and tab characters from a string.
    * 
    * @param toBeEscaped
    *           string to escape
    * 
    * @return the escaped string
    */
   private static String removeFormattingCharacters(final String toBeEscaped) {
      StringBuffer escapedBuffer = new StringBuffer();

      for (int i = 0; i < toBeEscaped.length(); i++) {
         if ((toBeEscaped.charAt(i) != '\n') && (toBeEscaped.charAt(i) != '\r')
               && (toBeEscaped.charAt(i) != '\t')) {
            escapedBuffer.append(toBeEscaped.charAt(i));
         } else {
            escapedBuffer.append(' ');
         }
      }

      return escapedBuffer.toString();
   }
}
