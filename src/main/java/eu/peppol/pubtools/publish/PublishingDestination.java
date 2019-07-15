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
package eu.peppol.pubtools.publish;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperations;

public class PublishingDestination
{
  private final File m_aBaseDir;

  public PublishingDestination (@Nonnull final File aBaseDir)
  {
    m_aBaseDir = aBaseDir.getAbsoluteFile ();
    FileOperations.createDirRecursiveIfNotExisting (m_aBaseDir);
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aBaseDir;
  }

  @Nonnull
  public OutputStream getOutputStream (@Nonnull final String sOutputPath)
  {
    String sRealPath = sOutputPath;
    if (sRealPath.endsWith ("/"))
      sRealPath += "index.html";
    return FileHelper.getBufferedOutputStream (new File (m_aBaseDir, sRealPath));
  }

  @Nonnull
  public Writer getWriter (@Nonnull final String sOutputPath)
  {
    return new OutputStreamWriter (getOutputStream (sOutputPath), StandardCharsets.UTF_8);
  }
}
