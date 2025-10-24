package com.example.demo.common;

import com.example.demo.handle.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 从上下文中取出当前线程使用的数据源标识
        return DataSourceContextHolder.get();
    }
}