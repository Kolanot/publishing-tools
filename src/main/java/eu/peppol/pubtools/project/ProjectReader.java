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
package eu.peppol.pubtools.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.jaxb.GenericJAXBMarshaller;

import eu.peppol.pubtools.project.v1.ObjectFactory;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1ResourceType;

public class ProjectReader
{
  private static final ClassPathResource XSD = new ClassPathResource ("schemas/project-1.xsd",
                                                                      ProjectReader.class.getClassLoader ());

  private ProjectReader ()
  {}

  @Nullable
  public static P1ProjectType read (@Nonnull final IReadableResource aRes)
  {
    final GenericJAXBMarshaller <P1ProjectType> m = new GenericJAXBMarshaller <> (P1ProjectType.class,
                                                                                  new CommonsArrayList <> (XSD),
                                                                                  new ObjectFactory ()::createProject);
    return m.read (aRes);
  }

  @Nullable
  public static ResolvedProject createResolvedProject (@Nonnull final File aBaseDir)
  {
    final P1ProjectType aProject = read (new FileSystemResource (aBaseDir, "project.xml"));
    if (aProject == null)
      return null;
    return createResolvedProject (aBaseDir, aProject);
  }

  @Nonnull
  public static ResolvedProject createResolvedProject (@Nonnull final File aBaseDir,
                                                       @Nonnull final P1ProjectType aProject)
  {
    final ResolvedProject ret = new ResolvedProject (aProject);
    for (final P1ResourceType aRes : aProject.getResource ())
    {
      // Check that resource exists
      final String sPath = aRes.getPath ();
      final File aResFile = new File (aBaseDir, sPath);
      if (!aResFile.exists ())
        throw new IllegalArgumentException ("Referenced file is missing " + aResFile.getAbsolutePath ());

      // Check that it is readable
      switch (aRes.getType ())
      {
        case CODE_LIST_1:
        {
          ret.addResolvedResource (aRes, CodeListReader.read (new FileSystemResource (aResFile)));
          break;
        }
        case STRUCTURE_1:
        {
          ret.addResolvedResource (aRes, StructureReader.read (new FileSystemResource (aResFile)));
          break;
        }
        case NAMESPACE_1:
          // Can be ignored according to Erlend
          break;
        case NATIVE_FILE:
        case NATIVE_SCHEMATRON:
        {
          ret.addNativeResource (aRes);
          break;
        }
        default:
          throw new IllegalStateException ("Unsupported type " + aRes.getType ());
      }
    }
    return ret;
  }
}
