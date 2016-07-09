package com.example.doma2.dao;

import com.example.doma2.AppConfig;
import org.seasar.doma.Dao;
import org.seasar.doma.Script;

@Dao(config = AppConfig.class)
public interface AppDao {

    @Script
    void create();

    @Script
    void drop();

}
