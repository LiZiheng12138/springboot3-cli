package com.example.demo.entity.table;


import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class AppUserTableDef extends TableDef {
    public static final AppUserTableDef APP_USER = new AppUserTableDef();

    public final QueryColumn Id = new QueryColumn(this, "id");
    public final QueryColumn AppUserName = new QueryColumn(this, "app_user_name");
    public final QueryColumn BusinessId = new QueryColumn(this, "business_id");
    public final QueryColumn BusinessName = new QueryColumn(this, "business_name");
    public final QueryColumn AppTypeId = new QueryColumn(this, "app_type_id");
    public final QueryColumn AppUserPhone = new QueryColumn(this, "app_user_phone");
    public final QueryColumn AppUserPassword = new QueryColumn(this, "app_user_password");
    public final QueryColumn CreatedAt = new QueryColumn(this, "created_at");
    public final QueryColumn UpdatedAt = new QueryColumn(this, "updated_at");
    public final QueryColumn Status = new QueryColumn(this, "status");
    public final QueryColumn AppUserCode = new QueryColumn(this, "app_user_code");
    public final QueryColumn OperationAreaId = new QueryColumn(this, "operation_area_id");
    public final QueryColumn WxNoticeType = new QueryColumn(this, "wx_notice_type");
    public final QueryColumn BrandType = new QueryColumn(this, "brand_type");
    public final QueryColumn ImageUrl = new QueryColumn(this, "image_url");
    public final QueryColumn Tag = new QueryColumn(this, "tag");
    public final QueryColumn CountryCode = new QueryColumn(this, "country_code");
    public final QueryColumn OpenId = new QueryColumn(this, "open_id");
    public final QueryColumn UnionId = new QueryColumn(this, "union_id");
    public final QueryColumn MeetingOperation = new QueryColumn(this, "meeting_operation");
    public final QueryColumn IsVipDistribute = new QueryColumn(this, "is_vip_distribute");

    private AppUserTableDef() {
        super("app_user", null);
    }
}
