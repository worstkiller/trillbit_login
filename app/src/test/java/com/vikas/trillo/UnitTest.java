package com.vikas.trillo;

import com.vikas.trillo.network.ApiServiceNetwork;
import com.vikas.trillo.utils.WebConstants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_NetworkClass() throws Exception {
        ApiServiceNetwork apiNetwork = new ApiServiceNetwork();
        assertNotNull(apiNetwork.getNetworkService(null, WebConstants.GIT_API_END));
    }

    @Test
    public void checkClass_Exists() throws  Exception{
        Class clazz = Class.forName("com.vikas.trillo.view.MainActivity");
        assertNotNull(clazz);
    }
}