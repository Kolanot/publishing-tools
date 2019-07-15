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

import javax.annotation.Nonnull;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

public class PeppolAsciiDoc
{
  private static final Asciidoctor INSTANCE = Asciidoctor.Factory.create ();

  private PeppolAsciiDoc ()
  {}

  private static OptionsBuilder _createBuilder (@Nonnull final File aDstDir)
  {
    return OptionsBuilder.options ().mkDirs (true).toDir (aDstDir).safe (SafeMode.UNSAFE);
  }

  public static void createHTML (@Nonnull final File aSrcFile, @Nonnull final File aDstDir)
  {
    INSTANCE.convertFile (aSrcFile, _createBuilder (aDstDir).backend ("html"));
  }

  public static void createPDF (@Nonnull final File aSrcFile, @Nonnull final File aDstDir)
  {
    INSTANCE.convertFile (aSrcFile, _createBuilder (aDstDir).backend ("pdf"));
  }
}
