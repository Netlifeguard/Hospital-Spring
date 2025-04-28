package com.nie.coupons.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author nie
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer limitNumber;

    private Integer isLimited;

    private Integer isStackable;

    private Integer couponStatus;

    private String expiryTime;

    private Integer sendType;

    private String sendTime;

    private Integer DiscountAmount;

    private String applicableArea;

    private Integer exchangePoint;

    private String exchangeCode;

    private String couponName;

    @TableId(value = "Coupon_ID", type = IdType.AUTO)
    private Integer couponId;

    private Integer userId;


}
