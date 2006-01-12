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
package net.java.dev.genesis.plugins.netbeans.buildsupport.spi;

import net.java.dev.reusablecomponents.lang.Enum;

public final class GenesisVersion implements Comparable {
   public static class GenesisReleaseType extends Enum {
      public static final GenesisReleaseType EARLY_ACCESS = 
            new GenesisReleaseType("EA");
      public static final GenesisReleaseType BETA = 
            new GenesisReleaseType("beta");
      public static final GenesisReleaseType RELEASE_CANDIDATE = 
            new GenesisReleaseType("RC");
      public static final GenesisReleaseType FINAL_RELEASE = 
            new GenesisReleaseType("FCS");

      private GenesisReleaseType(String name) {
         super(name);
      }
   }

   private final int major;
   private final int minor;
   private final GenesisReleaseType type;
   private final int releaseNumber;
   private final boolean development;

   public GenesisVersion(final int major, final int minor, 
         final GenesisReleaseType type, int releaseNumber, 
         final boolean development) {
      if (type == null) {
         throw new IllegalArgumentException("type cannot be null");
      }

      this.major = major;
      this.minor = minor;
      this.type = type;
      this.releaseNumber = releaseNumber;
      this.development = development;
   }

   public int getMajor() {
      return major;
   }

   public int getMinor() {
      return minor;
   }

   public GenesisReleaseType getType() {
      return type;
   }

   public int getReleaseNumber() {
      return releaseNumber;
   }

   public boolean isDevelopment() {
      return development;
   }

   public boolean equals(Object o) {
      if (!(o instanceof GenesisVersion)) {
         return false;
      }

      GenesisVersion version = (GenesisVersion)o;

      return major == version.major && minor == version.minor && 
            type == version.type && releaseNumber == version.releaseNumber &&
            development == version.development;
   }

   public int hashCode() {
      return (major << 16) | ((minor << 8) & 0x00F0) | (releaseNumber & 0x000F);
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(major).append('.').append(minor);

      if (type != GenesisReleaseType.FINAL_RELEASE) {
         buffer.append('-').append(type).append(releaseNumber);
      }

      if (development) {
         buffer.append("-dev");
      }

      return buffer.toString();
   }

   public int compareTo(Object o) {
      GenesisVersion version = (GenesisVersion)o;

      if (major != version.major) {
         return major < version.major ? -1 : 1;
      }

      if (minor != version.minor) {
         return minor < version.minor ? -1 : 1;
      }

      if (type != version.type) {
         return type.compareTo(version.type);
      }

      if (releaseNumber != version.releaseNumber) {
         return releaseNumber < version.releaseNumber ? -1 : 1;
      }

      return development ? (version.development ? 0 : -1) : 
            (version.development ? 1 : 0);
   }
}