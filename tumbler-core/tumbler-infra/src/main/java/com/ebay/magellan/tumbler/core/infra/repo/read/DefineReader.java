package com.ebay.magellan.tumbler.core.infra.repo.read;

import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class DefineReader<T> {

    protected abstract List<String> getDefineDirs();

    public List<T> readDefines(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            for (String dir : getDefineDirs()) {
                if (StringUtils.isBlank(dir)) continue;
                Resource[] resources = new PathMatchingResourcePatternResolver()
                        .getResources("classpath:/" + dir + "/*.json");
                for (Resource resource : resources) {
                    T define = readDefineFile(resource.getInputStream(), clazz);
                    if (define != null) {
                        list.add(define);
                    } else {
                        System.out.println(String.format("fails to read define from file %s", resource.getFilename()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public T readDefineFile(InputStream stream, Class<T> clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        T define = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            String str = new String(stringBuffer);
            ObjectReader reader = JsonParseUtil.getReader(clazz);
            if (StringUtils.isNotBlank(str)) {
                define = reader.readValue(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return define;
    }
}
