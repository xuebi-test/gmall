package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description: spu 属性值扩展对象
 * @Author: Guan FuQing
 * @Date: 2025/12/22 04:55
 * @Email: moumouguan@gmail.com
 */
public class SpuAttrValueVo extends SpuAttrValueEntity {

    public void setValueSelected(List<String> valueSelected) {

        // 如果接受的集合为空, 则不设置
        if (CollectionUtils.isEmpty(valueSelected)) {
            return;
        }

        // 将接受的集合根据 "," 分割为字符串 赋值给 AttrValue 属性
        setAttrValue(StringUtils.join(valueSelected, ","));
    }

}
