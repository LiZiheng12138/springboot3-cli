package com.example.demo.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * app_user 表对应的实体类
 * 
 * @author Lip
 * @since 2025-10-23
 */
@Data
@Table(value = "app_user")
public class AppUser {

    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 用户名称
     */
    private String appUserName;

    /**
     * 酒店ID
     */
    private Integer businessId;

    /**
     * 酒店名称
     */
    private String businessName;

    /**
     * 用户类型ID
     */
    private Integer appTypeId;

    /**
     * 用户手机号
     */
    private String appUserPhone;

    /**
     * 登录密码，默认123456
     */
    private String appUserPassword;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 状态
     */
    private String status;

    /**
     * 第三方用户编码
     */
    private String appUserCode;

    /**
     * 操作区域ID，默认0
     */
    private String operationAreaId;

    /**
     * 微信公众号订单通知类型：0全部区域，1指定区域
     */
    private Integer wxNoticeType;

    /**
     * 品牌权限类型：0为非品牌权限，1为有品牌权限
     */
    private Integer brandType;

    /**
     * 头像或图片地址
     */
    private String imageUrl;

    /**
     * 标签
     */
    private String tag;

    /**
     * 国家代码（默认86）
     */
    private String countryCode;

    /**
     * 微信 open_id
     */
    private String openId;

    /**
     * 微信 union_id
     */
    private String unionId;

    /**
     * 宴会操作内容
     */
    private String meetingOperation;

    /**
     * 是否接受客户分配：0-否 1-是
     */
    private Integer isVipDistribute;
}