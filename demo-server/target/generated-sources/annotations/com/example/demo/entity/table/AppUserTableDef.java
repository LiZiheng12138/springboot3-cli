package com.example.demo.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class AppUserTableDef extends TableDef {

    /**
     * app_user 表对应的实体类
 
 @author Lip
 @since 2025-10-23
     */
    public static final AppUserTableDef APP_USER = new AppUserTableDef();

    /**
     * 主键ID，自增
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 标签
     */
    public final QueryColumn TAG = new QueryColumn(this, "tag");

    /**
     * 微信 open_id
     */
    public final QueryColumn OPEN_ID = new QueryColumn(this, "open_id");

    /**
     * 状态
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 微信 union_id
     */
    public final QueryColumn UNION_ID = new QueryColumn(this, "union_id");

    /**
     * 头像或图片地址
     */
    public final QueryColumn IMAGE_URL = new QueryColumn(this, "image_url");

    /**
     * 用户类型ID
     */
    public final QueryColumn APP_TYPE_ID = new QueryColumn(this, "app_type_id");

    /**
     * 品牌权限类型：0为非品牌权限，1为有品牌权限
     */
    public final QueryColumn BRAND_TYPE = new QueryColumn(this, "brand_type");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    /**
     * 酒店ID
     */
    public final QueryColumn BUSINESS_ID = new QueryColumn(this, "business_id");

    /**
     * 第三方用户编码
     */
    public final QueryColumn APP_USER_CODE = new QueryColumn(this, "app_user_code");

    /**
     * 用户名称
     */
    public final QueryColumn APP_USER_NAME = new QueryColumn(this, "app_user_name");

    /**
     * 国家代码（默认86）
     */
    public final QueryColumn COUNTRY_CODE = new QueryColumn(this, "country_code");

    /**
     * 用户手机号
     */
    public final QueryColumn APP_USER_PHONE = new QueryColumn(this, "app_user_phone");

    /**
     * 酒店名称
     */
    public final QueryColumn BUSINESS_NAME = new QueryColumn(this, "business_name");

    /**
     * 微信公众号订单通知类型：0全部区域，1指定区域
     */
    public final QueryColumn WX_NOTICE_TYPE = new QueryColumn(this, "wx_notice_type");

    /**
     * 登录密码，默认123456
     */
    public final QueryColumn APP_USER_PASSWORD = new QueryColumn(this, "app_user_password");

    /**
     * 是否接受客户分配：0-否 1-是
     */
    public final QueryColumn IS_VIP_DISTRIBUTE = new QueryColumn(this, "is_vip_distribute");

    /**
     * 操作区域ID，默认0
     */
    public final QueryColumn OPERATION_AREA_ID = new QueryColumn(this, "operation_area_id");

    /**
     * 宴会操作内容
     */
    public final QueryColumn MEETING_OPERATION = new QueryColumn(this, "meeting_operation");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TAG, OPEN_ID, STATUS, UNION_ID, IMAGE_URL, APP_TYPE_ID, BRAND_TYPE, CREATED_AT, UPDATED_AT, BUSINESS_ID, APP_USER_CODE, APP_USER_NAME, COUNTRY_CODE, APP_USER_PHONE, BUSINESS_NAME, WX_NOTICE_TYPE, APP_USER_PASSWORD, IS_VIP_DISTRIBUTE, OPERATION_AREA_ID, MEETING_OPERATION};

    public AppUserTableDef() {
        super("", "app_user");
    }

    private AppUserTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppUserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppUserTableDef("", "app_user", alias));
    }

}
