package com.hotels.restassuredframework.core.handlers;

import java.util.Properties;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hotels.restassuredframework.core.utils.log.LoggingUtils;


/**
 * Unit Tests for {@link PropertyHandler}.
 * @author adebiase
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(PropertyHandler.class)
public class PropertyHandlerTest {

    @Mock
    private LoggingUtils logUtils;

    @Mock
    private PlaceholderHandler placeholderHandler;

    @InjectMocks
    private PropertyHandler underTest;

    @BeforeMethod
    public void setUp() {
        logUtils = new LoggingUtils();
    }

    private void clearSysProperties() {
        System.clearProperty("defaultEnvironment");
        System.clearProperty("environment");
        System.clearProperty("webappName");
    }

    @Test
    public void testNotCorrectFlow() {
        //GIVEN
        System.setProperty("defaultEnvironment", "DEFAULT");
        System.setProperty("environment", "ENV_UNDER_TEST");
        System.setProperty("webappName", "WEBAPP_UNDER_TEST");


        underTest = new PropertyHandler("file", placeholderHandler);
        underTest.loadEnvironmentProperties();
        Assert.assertFalse(underTest.isLoaded());

    }

    @Test
    public void testPropertiesSetterAndGetter() {

        underTest = new PropertyHandler("file", placeholderHandler);
        Properties ps = new Properties();
        underTest.setProperties(ps);
        Assert.assertEquals(ps, underTest.getProperties());

    }

}