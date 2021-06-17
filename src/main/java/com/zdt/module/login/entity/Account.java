package com.zdt.module.login.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
* 账户表 
* </p>
*
* @author xxxx
* @since 2021-06-17
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("account")
@ApiModel(value="Account对象", description="账户表 ")
public class Account implements Serializable {

    private static final long serialVersionUID=1L;

        /**
         * 序列
         */
        @ApiModelProperty(value = "序列")
        @TableId(value = "id", type = IdType.ID_WORKER)
            private String id;

        /**
         * 客户id
         */
        @ApiModelProperty(value = "客户id")
        private String custId;

        /**
         * 登录账户名
         */
        @ApiModelProperty(value = "登录账户名")
        private String acctLoginName;

        /**
         * 登录账户密码
         */
        @ApiModelProperty(value = "登录账户密码")
        private String loginPassword;

        /**
         * salt加密 每个用户开通手机登录时随机生成
         */
        @ApiModelProperty(value = "salt加密 每个用户开通手机登录时随机生成")
        private String salt;

        /**
         * 账户状态 0失效1有效2账户异常3锁定
         */
        @ApiModelProperty(value = "账户状态 0失效1有效2账户异常3锁定")
        private Integer statusCd;

        /**
         * 生效日期
         */
        @ApiModelProperty(value = "生效日期")
        private LocalDateTime effTime;

        /**
         * 失效日期
         */
        @ApiModelProperty(value = "失效日期")
        private LocalDateTime expTime;

        /**
         * 部门编码 部门编码
         */
        @ApiModelProperty(value = "部门编码 部门编码")
        private String sysOrgCode;

        /**
         * 创建人
         */
        @ApiModelProperty(value = "创建人")
        private String createBy;

        /**
         * 创建时间
         */
        @ApiModelProperty(value = "创建时间")
        private LocalDateTime createTime;

        /**
         * 更新人
         */
        @ApiModelProperty(value = "更新人")
        private String updateBy;

        /**
         * 更新时间
         */
        @ApiModelProperty(value = "更新时间")
        private LocalDateTime updateTime;


}
