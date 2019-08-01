/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that a test is not applicable to Payara Micro
 * Use in combination with <code>@RunWith(PayaraTestRunner.class)</code> or <code>@RunWith(PayaraArquillianTestRunner.class)</code> annotation
 * 
 * Can also be used in conjunction with <code>@SincePayara</code> to denote versioning
 * 
 * @see fish.payara.samples.*
 * 
 * @author Cuba Stanley
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotMicroCompatible {}
