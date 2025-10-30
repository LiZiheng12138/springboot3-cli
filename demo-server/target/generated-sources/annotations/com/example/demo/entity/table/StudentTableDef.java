package com.example.demo.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class StudentTableDef extends TableDef {

    public static final StudentTableDef STUDENT = new StudentTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn AGE = new QueryColumn(this, "age");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, AGE, NAME};

    public StudentTableDef() {
        super("", "student");
    }

    private StudentTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public StudentTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new StudentTableDef("", "student", alias));
    }

}
