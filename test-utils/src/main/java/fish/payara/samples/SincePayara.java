/*
 *  Copyright 2018 Payara Services Ltd
 */
package fish.payara.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify since which versions these tests apply.
 * Use in combination with <code>@RunWith(PayaraTestRunner.class)</code> or <code>@RunWith(PayaraArquillianTestRunner.class)</code> annotation
 * 
 * @see fish.payara.samples.PayaraVersion
 * @author Mark Wareham
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SincePayara {
    
    PayaraVersion value();
    
}
