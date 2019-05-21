/*
 *  Copyright 2018 Payara Services Ltd
 */
package fish.payara.samples;

import static junit.framework.Assert.assertEquals;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the PayaraVersion class' methods.
 * @author Mark Wareham
 */
@NotThreadSafe //due to changing of system property used by other tests
public class PayaraVersionTest {
    
    private static String originalPayaraVersion;
    
    @Before
    public void setUp(){
        originalPayaraVersion = System.getProperty("payara.version");//save value to restore later before overwriting 
        if (originalPayaraVersion == null){
            throw new RuntimeException ("Tests expect a system property value for payara.version");
        }
        System.setProperty("payara.version", "5.191-SNAPSHOT");
    }
    
    @After
    public void tearDown(){
        System.setProperty("payara.version", originalPayaraVersion);//restore
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fromString_withUnknownVersionTest(){
        PayaraVersion.fromString("99.191");
    }
    
    @Test
    public void isExcludedFromTestPriorTo_whenValueBeforeTest(){
        boolean result = PayaraVersion.isPayaraSystemPropertyVersionExcludedFromTestPriorTo(PayaraVersion.PAYARA_5_181);
        assertEquals(false, result);
    }
    
    @Test
    public void isExcludedFromTestPriorTo_whenValueSameTest(){
        boolean result = PayaraVersion.isPayaraSystemPropertyVersionExcludedFromTestPriorTo(PayaraVersion.PAYARA_5_191);
        assertEquals(false, result);
    }
    
    @Test
    public void isExcludedFromTestPriorTo_whenValueAfterTest(){
        boolean result = PayaraVersion.isPayaraSystemPropertyVersionExcludedFromTestPriorTo(PayaraVersion.PAYARA_5_192);
        assertEquals(true, result);
    }
    
    @Test
    public void isExcludedFromTestPriorTo_whenValueNullTest(){
        boolean result = PayaraVersion.isPayaraSystemPropertyVersionExcludedFromTestPriorTo(null);
        assertEquals(false, result);
    }
    
}
