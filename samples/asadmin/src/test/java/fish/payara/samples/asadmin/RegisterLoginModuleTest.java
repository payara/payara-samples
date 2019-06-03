/*
 *    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *    Copyright (c) [2019] Payara Foundation and/or its affiliates. All rights reserved.
 *
 *    The contents of this file are subject to the terms of either the GNU
 *    General Public License Version 2 only ("GPL") or the Common Development
 *    and Distribution License("CDDL") (collectively, the "License").  You
 *    may not use this file except in compliance with the License.  You can
 *    obtain a copy of the License at
 *    https://github.com/payara/Payara/blob/master/LICENSE.txt
 *    See the License for the specific
 *    language governing permissions and limitations under the License.
 *
 *    When distributing the software, include this License Header Notice in each
 *    file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *    GPL Classpath Exception:
 *    The Payara Foundation designates this particular file as subject to the "Classpath"
 *    exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *    file that accompanied this code.
 *
 *    Modifications:
 *    If applicable, add the following below the License Header, with the fields
 *    enclosed by brackets [] replaced by your own identifying information:
 *    "Portions Copyright [year] [name of copyright owner]"
 *
 *    Contributor(s):
 *    If you wish your version of this file to be governed by only the CDDL or
 *    only the GPL Version 2, indicate your decision by adding "[Contributor]
 *    elects to include this software in this distribution under the [CDDL or GPL
 *    Version 2] license."  If you don't indicate a single choice of license, a
 *    recipient has the option to distribute your version of this file under
 *    either the CDDL, the GPL Version 2 or to extend the choice of license to
 *    its licensees as provided above.  However, if you add GPL Version 2 code
 *    and therefore, elected the GPL Version 2 license, then the option applies
 *    only if the new code is made subject to such option by the copyright
 *    holder.
 */

package fish.payara.samples.asadmin;

import com.google.common.io.CharStreams;
import java.io.BufferedWriter;
import org.glassfish.embeddable.CommandResult;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import org.junit.After;
import static org.junit.Assert.assertEquals;

public class RegisterLoginModuleTest extends AsadminTest {

    @Test
    public void successfulRegistrationUpdatesLoginConf() throws IOException {
        CommandResult result = asadmin("create-auth-realm",
                "--classname", "com.sun.enterprise.security.auth.realm.file.FileRealm",
                "--login-module", "com.sun.enterprise.security.auth.login.FileLoginModule",
                "--property", "jaas-context=test1:file=test1", "test1");
        System.out.println(result.getOutput());
        assertSuccess(result);
        String contents = loginConf();
        assertContains("test1 {", contents);
        
        result = asadmin("delete-auth-realm", "test1");
        System.out.println(result.getOutput());
        assertSuccess(result);
        
        if (contents.contains("test1 {")) {
            String newFileContents = contents.replace(contents.substring(contents.indexOf("test1")), "");
            System.out.print(newFileContents);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("java.security.auth.login.config")));
                writer.write(newFileContents);
                writer.close();
            } catch(IOException e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        }
        
        assertFalse(loginConf().contains("test1 {"));
    }

    @Test
    public void existingJaasContextGivesWarning()  {
        CommandResult result = asadmin("create-auth-realm",
                "--classname", "com.sun.enterprise.security.auth.realm.file.FileRealm",
                "--login-module", "com.sun.enterprise.security.auth.login.FileLoginModule",
                "--property", "jaas-context=fileRealm:file=test2", "test2");
        System.out.println(result.getOutput());
        assertEquals(CommandResult.ExitStatus.WARNING, result.getExitStatus());
        assertContains("fileRealm is already configured", result.getOutput());
        
        result = asadmin("delete-auth-realm", "test2");
        System.out.println(result.getOutput());
        assertSuccess(result);
    }

    @Test
    public void undefinedJaasContextGivesWarning() {
        CommandResult result = asadmin("create-auth-realm",
                "--classname", "com.sun.enterprise.security.auth.realm.certificate.CertificateRealm",
                "--login-module", "com.sun.enterprise.security.auth.login.FileLoginModule",
                "--property", "file=test3", "test3");
        System.out.println(result.getOutput());
        assertEquals(CommandResult.ExitStatus.WARNING, result.getExitStatus());
        assertContains("No JAAS context is defined", result.getOutput());
        
        result = asadmin("delete-auth-realm", "test3");
        System.out.println(result.getOutput());
        assertSuccess(result);
    }
    
//    @After
//    public void cleanupJAASContext() throws IOException {
//    
//        String originalFileContents = loginConf();
//        
//        if(originalFileContents.contains("test1 {")) {
//            String newFileContents = originalFileContents.replaceFirst("test1 { .* };", "");
//            try {
//                BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("java.security.auth.login.config")));
//                writer.write(newFileContents);
//                writer.close();
//            } catch(IOException e) {
//                System.out.print(e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        
//    }

    private String loginConf() throws IOException {
        File loginConf = new File(System.getProperty("java.security.auth.login.config"));
        try (
                Reader reader = new FileReader(loginConf);
        ) {
            return CharStreams.toString(reader);
        }
    }

}
