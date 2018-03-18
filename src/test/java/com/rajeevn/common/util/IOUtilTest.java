package com.rajeevn.common.util;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IOUtilTest
{
    @Test
    public void test() throws IOException
    {
        Files.createFile(Paths.get("test"));
        IOUtil.moveQuietly("test", "test2");
        assert Files.exists(Paths.get("test2"));
    }
}
