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
package net.java.dev.genesis.plugins.netbeans.buildsupport.release3_0_EA_3_dev;

import java.io.IOException;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.AbstractGenesisBuildSupport;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisBuildSupport;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisVersion;

public final class Genesis30EA3DevBuildSupport extends AbstractGenesisBuildSupport {
   public Genesis30EA3DevBuildSupport() {
      super(new GenesisVersion(3, 0, 
            GenesisVersion.GenesisReleaseType.EARLY_ACCESS, 3, true));
   }
}
