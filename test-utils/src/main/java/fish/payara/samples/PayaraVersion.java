/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *  Copyright (c) [2019] Payara Foundation and/or its affiliates. All rights reserved.
 * 
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://github.com/payara/Payara/blob/master/LICENSE.txt
 *  See the License for the specific
 *  language governing permissions and limitations under the License.
 * 
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/legal/LICENSE.txt.
 * 
 *  GPL Classpath Exception:
 *  The Payara Foundation designates this particular file as subject to the "Classpath"
 *  exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *  file that accompanied this code.
 * 
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 * 
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */
package fish.payara.samples;

/**
 * Enum representing the versions of Payara. It does not represent all versions because it only needs to include those
 * used in the <code>@SincePayara</code> annotation
 * @author Mark Wareham
 */
public enum PayaraVersion {

    // order is important!
    // order is important!
    // order is important!
    
    PAYARA_5_181("5.181"),
    PAYARA_5_182("5.182"),
    PAYARA_5_183("5.183"),
    PAYARA_5_184("5.184"),
    PAYARA_5_191("5.191"),
    PAYARA_5_192("5.192"),
    PAYARA_5_193("5.193"),
    PAYARA_5_194("5.194"),
    PAYARA_5_201("5.201"),
    PAYARA_5_202("5.202");
    
    
    private final String version;    

    private PayaraVersion (String version) {
        this.version = version;
    }

    @Override
    public String toString(){
        return version;
    }
    
    public static PayaraVersion fromString(String version){
        String plainVersion = version.replaceAll("-.*", "");// remove everything after the hyphen (eg. -SNAPSHOT -RC1)
        
        if(plainVersion!=null && !plainVersion.isEmpty()) {
            for(PayaraVersion loopVersion : PayaraVersion.values()) {
                if(loopVersion.toString().equals(plainVersion)){
                    return loopVersion;
                }
            }
        }
        System.err.println("fish.payara.samples.PayaraVersion.fromString(). No known payara version of " + plainVersion);
        throw new IllegalArgumentException("No PayaraVersion of '"+plainVersion+"' listed in enum");
    }

}
