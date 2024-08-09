package com.ebay.magellan.tumbler.core.infra.repo;

import com.ebay.magellan.tumbler.core.domain.define.Define;
import com.ebay.magellan.tumbler.core.infra.repo.read.DefineReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefineRepo<T extends Define> {

    protected DefineReader<T> defineReader;

    protected Map<String, T> defines = new HashMap<>();

    public DefineRepo(DefineReader<T> reader, Class<T> clazz) {
        defineReader = reader;
        List<T> list = reader.readDefines(clazz);
        for (T d : list) {
            defines.put(d.name(), d);
        }
    }

    public T getDefine(String name) {
        return defines.get(name);
    }

}
