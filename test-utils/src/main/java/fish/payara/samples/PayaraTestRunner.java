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

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * An extension of the BlockJUnit4ClassRunner. To be used when <code>@SincePayara</code> is used.
 * Decides based on the annotations of <code>@SincePayara</code> which tests should be run
 * 
 * See also PayaraArquillianTestRunner which is a copy of this class for Arquillian test cases
 * 
 * @see fish.payara.samples.PayaraArquillianTestRunner
 * 
 * @author Mark Wareham
 */
public class PayaraTestRunner extends BlockJUnit4ClassRunner {
    
    private static final String PAYARA_VERSION = "payara.version";
    
    private boolean skipEntireClass;

    public PayaraTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        
        if (klass.getAnnotation(SincePayara.class) == null) {
            return;
        }
        SincePayara sincePayara = klass.getAnnotation(SincePayara.class);

        if (System.getProperty(PAYARA_VERSION) != null) {

            try {

                PayaraVersion payaraVersionUnderTesting = PayaraVersion.fromString(System.getProperty(PAYARA_VERSION));

                if (payaraVersionUnderTesting.ordinal() < sincePayara.value().ordinal()) {
                    skipEntireClass = true;
                }

            } catch (IllegalArgumentException exception) {
                //no match, so still run tests
            }
        }

    }
    
    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        
        List<FrameworkMethod> result = new ArrayList<>();
        
        if (skipEntireClass) {
            return result;
        }
        
        for(FrameworkMethod testMethod : super.computeTestMethods()) {
            
            //if the annotation is there and the version exists then we'll eval whether to test. else the method will always test
            if (testMethod.getAnnotation(SincePayara.class) != null && System.getProperty(PAYARA_VERSION) != null) {
                
                try {
                    PayaraVersion serverVersion = PayaraVersion.fromString(System.getProperty(PAYARA_VERSION));

                    SincePayara sincePayara = testMethod.getAnnotation(SincePayara.class);
                    if (serverVersion.ordinal() >= sincePayara.value().ordinal()) {
                        result.add(testMethod);
                    }else{
                        System.out.println("Not running test " + testMethod.getName() 
                                + "due to the version of payara being tested ("
                                + serverVersion.name() + ") being earlier than that marked on the @SincePayara "
                                + sincePayara.value()+" annotation on the test method");
                    }
                } catch (IllegalArgumentException exception) {
                    // no match so test anyway
                    result.add(testMethod);
                }
                
            } else {
                result.add(testMethod);
            }
        }
        return result;
    }
    
    /**
     * Overrides parent to remove warning of no valid methods if none are applicable
     * @param errors 
     */
    @Override
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
    }
}
