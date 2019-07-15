/**
 * Copyright (C) 2019 OpenPEPPOL AISBL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.peppol.pubtools.publish.asciidoc;

import java.io.File;
import java.util.HashMap;

import javax.annotation.Nonnull;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.ast.Document;

public class PeppolAsciiDoc
{
  private static final Asciidoctor INSTANCE = Asciidoctor.Factory.create ();

  private PeppolAsciiDoc ()
  {}

  @Nonnull
  public static String parse (@Nonnull final File aFile)
  {
    final Document aDoc = INSTANCE.loadFile (aFile, new HashMap <> ());
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to parse " + aFile.getAbsolutePath () + " as AsciiDoc");
    return aDoc.getContent ().toString ();
  }
}
