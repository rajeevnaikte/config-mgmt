package com.rajeevn.common.util;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import static com.rajeevn.common.util.FileIOUtil.getFileExtention;
import static com.rajeevn.common.util.FileIOUtil.processInputFile;
import static com.rajeevn.common.util.FileIOUtil.processOutputFile;

public abstract class PropertiesUtil
{
    public static Properties load(Properties prop, File file)
    {
        if (prop == null)
            prop = new Properties();
        final Properties props = prop;
        processInputFile(file, in ->
        {
            switch (getFileExtention(file))
            {
                case "xml":
                    props.loadFromXML(in);
                    break;
                default:
                    props.load(in);
            }
        });
        return props;
    }

    public static void store(Properties prop, File file)
    {
        processOutputFile(file, out -> prop.store(out, null));
    }

    public static Properties load(Properties prop, String filePath)
    {
        return load(prop, new File(filePath));
    }

    public static void store(Properties prop, String filePath)
    {
        store(prop, new File(filePath));
    }

    public static void addOrUpdate(String filePath, Map<String, String> keyValMap)
    {
        addOrUpdate(new File(filePath), keyValMap);
    }

    public static void addOrUpdate(File file, Map<String, String> keyValMap)
    {
        addOrUpdate(file, props -> props.putAll(keyValMap));
    }

    public static void addOrUpdate(String filePath, Consumer<Properties> addProps)
    {
        addOrUpdate(new File(filePath), addProps);
    }

    public static void addOrUpdate(File file, Consumer<Properties> addProps)
    {
        Properties props = new Properties();
        load(props, file);
        addProps.accept(props);
        store(props, file);
    }
}
