package com.example.demo.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserEntityTableDef extends TableDef {

    public static final UserEntityTableDef USER_ENTITY = new UserEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ROLES = new QueryColumn(this, "roles");

    public final QueryColumn ENABLED = new QueryColumn(this, "enabled");

    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLES, ENABLED, PASSWORD, USERNAME};

    public UserEntityTableDef() {
        super("", "sys_user");
    }

    private UserEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserEntityTableDef("", "sys_user", alias));
    }

}
