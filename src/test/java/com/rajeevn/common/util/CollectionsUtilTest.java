package com.rajeevn.common.util;

import org.junit.Test;

public class CollectionsUtilTest
{
    @Test
    public void testListToString()
    {
        assert "".equals(CollectionsUtil.listToString(null, ","));
    }
}
