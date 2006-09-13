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
package net.java.dev.genesis.anttasks.jnlp;

import java.util.ArrayList;
import java.util.List;

public class OrangeJNLPTask extends com.orangevolt.tools.ant.JNLPTask {
   private static final String NEW_LINE = "\r\n";
   private String encoding = "UTF-8";
   private String codebase = null;
   private String href = null;
   private Security security = null;
   private String spec = "1.0+";
   private String version = null;
   private XDesc xDesc = null;
   private List informations = new ArrayList();
   protected List resources = new ArrayList();

   public Resources createResources() {
      Resources res = new Resources();
      resources.add(res);

      return res;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setCodebase(String string) {
      this.codebase = string;
   }

   public void setHref(String href) {
      this.href = href;
   }

   public Security createSecurity() {
      return security = new Security();
   }

   public void setSecurity(Security security) {
      this.security = security;
   }

   public void setSpec(String string) {
      this.spec = string;
   }

   public Information createInformation() {
      Information information = new Information();
      informations.add(information);

      return information;
   }

   public void setVersion(String string) {
      this.version = string;
   }

   public AppletDesc createApplet_Desc() {
      if (xDesc != null) {
         throw new RuntimeException(
            "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
      }

      xDesc = new AppletDesc();

      return (AppletDesc) xDesc;
   }

   public ApplicationDesc createApplication_Desc() {
      if (xDesc != null) {
         throw new RuntimeException(
            "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
      }

      xDesc = new ApplicationDesc();

      return (ApplicationDesc) xDesc;
   }

   public ComponentDesc createComponent_Desc() {
      if (xDesc != null) {
         throw new RuntimeException(
            "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
      }

      xDesc = new ComponentDesc();

      return (ComponentDesc) xDesc;
   }

   public InstallerDesc createInstaller_Desc() {
      if (xDesc != null) {
         throw new RuntimeException(
            "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
      }

      xDesc = new InstallerDesc();

      return (InstallerDesc) xDesc;
   }

   public void toString(StringBuffer sb, int depth) {
      sb.append("<?xml version=\"1.0\" encoding=\"").append(encoding)
              .append("\"?>").append(NEW_LINE);
      sb.append(
         "<!DOCTYPE jnlp PUBLIC \"-//Sun Microsystems, Inc//DTD JNLP Discriptor 1.1//EN\" \"http://java.sun.com/dtd/JNLP-1.5.dtd\">")
              .append(NEW_LINE);
      sb.append("<jnlp");

      if (spec != null) {
         sb.append(" spec=\"").append(spec).append("\"");
      }

      if (version != null) {
         sb.append(" version=\"").append(version).append("\"");
      }

      if (codebase != null) {
         sb.append(" codebase=\"").append(codebase).append("\"");
      }

      if (href != null) {
         sb.append(" href=\"").append(href).append("\"");
      }

      sb.append('>').append(NEW_LINE);

      if (informations.size() < 1) {
         throw new RuntimeException(
            "<jnlp> : At least one information expected");
      }

      for (int i = 0; i < informations.size(); i++) {
         Information information = (Information) informations.get(i);
         information.toString(sb, 1);
      }

      if (security != null) {
         security.toString(sb, depth + 1);
      }

      for (int i = 0; i < resources.size(); i++) {
         Resources resources = (Resources) this.resources.get(i);
         resources.toString(sb, 1);
      }

      if (xDesc == null) {
         throw new RuntimeException(
            "<jnlp> : Sub element missing ( application-desc | applet-desc | component-desc | installer-desc)");
      }

      xDesc.toString(sb, depth + 1);

      sb.append("</jnlp>").append(NEW_LINE);
   }
}
