package eu.peppol.pubtools.publish.html;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.grouping.HCDD;
import com.helger.html.hc.html.grouping.HCDL;
import com.helger.html.hc.html.grouping.HCDT;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.grid.BootstrapGridSpec;

public class HCSimpleTable extends HCDL
{
  public static final BootstrapGridSpec GS_LEFT = BootstrapGridSpec.create (2);
  public static final BootstrapGridSpec GS_RIGHT = BootstrapGridSpec.create (10);

  public HCSimpleTable ()
  {
    addClass (CBootstrapCSS.ROW);
  }

  public void addRow (@Nonnull final String sTitle, @Nullable final String sValue)
  {
    if (sValue != null)
      addRow (sTitle, new HCTextNode (sValue));
  }

  public void addRow (@Nonnull final String sTitle, @Nonnull final IHCNode aChild)
  {
    addChild (GS_LEFT.applyTo (new HCDT ().addChild (sTitle)));
    addChild (GS_RIGHT.applyTo (new HCDD ().addChild (aChild)));
  }
}
