package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.response.CityRes;
import com.xinlian.common.response.ProvinceRedisRes;

import java.io.Serializable;

/**
 * <p>
 * 省市信息表
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-23
 */
@TableName("t_province_city")
public class TProvinceCity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 省市编码
     */
    @TableField("province_city_code")
    private String provinceCityCode;
    /**
     * 省市名称
     */
    @TableField("province_city_name")
    private String provinceCityName;
    /**
     * 上级编码
     */
    @TableField("parent_code")
    private String parentCode;
    /**
     * 节点id
     */
    @TableField("node_id")
    private String nodeId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceCityCode() {
        return provinceCityCode;
    }

    public void setProvinceCityCode(String provinceCityCode) {
        this.provinceCityCode = provinceCityCode;
    }

    public String getProvinceCityName() {
        return provinceCityName;
    }

    public void setProvinceCityName(String provinceCityName) {
        this.provinceCityName = provinceCityName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "TProvinceCity{" +
        ", id=" + id +
        ", provinceCityCode=" + provinceCityCode +
        ", provinceCityName=" + provinceCityName +
        ", parentCode=" + parentCode +
        ", nodeId=" + nodeId +
        "}";
    }

    public ProvinceRedisRes provinceRedisRes() {
        ProvinceRedisRes provinceRedisRes = new ProvinceRedisRes();
        provinceRedisRes.setProvinceCode(provinceCityCode);
        provinceRedisRes.setProvinceName(provinceCityName);
        provinceRedisRes.setNodeId(nodeId);
        return provinceRedisRes;
    }

    public CityRes cityRes() {
        CityRes cityRes = new CityRes();
        cityRes.setCityCode(provinceCityCode);
        cityRes.setCityName(provinceCityName);
        return cityRes;
    }
}
