package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrMapper attrMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 关联查询还是分步查询
     *      互联网项目应优先使用分步查询，避免因数据量大和分库分表导致的关联查询性能瓶颈。
     *          1. 性能优势：关联查询性能反而不高，例如两张表各1000 条数据，关联后可能关联出 100w 条数据，互联网项目数据本身表就比较大在关联性能较低。
     *              关联查询容易产生笛卡尔积问题，例如两张各1000条数据的表关联可能生成百万级结果集
     *              互联网项目数据量庞大，关联操作性能损耗显著
     *
     *          2. 架构适配性：如果关联查询的情况下，数据量较大会做分库分表，跨库 join 不能确保所需关联的数据都在一张表一张库中
     *              互联网项目常采用分库分表架构
     *              跨库关联查询（垮库join）难以保证关联数据位于同一库表，实现复杂且性能低下
     * @param cid
     * @return
     */
    @Override
    public List<AttrGroupEntity> queryGroupsWithAttrsByCid(Long cid) {

        // 1. 根据 cid 查询分组
        List<AttrGroupEntity> groupEntities = list(
                new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCategoryId, cid)
        );

        /**
         * 如果 groupEntities 为 null 直接返回
         *      1. 没有数据快速返回
         *      2. 防止 null. 空指针异常
         */
        if (CollectionUtils.isEmpty(groupEntities)) {
            return groupEntities;
        }

        // 2. 遍历分组查询组下的规格参数
        groupEntities.forEach(attrGroupEntity ->
            attrGroupEntity.setAttrEntities(
                    attrMapper.selectList(
                            new LambdaQueryWrapper<AttrEntity>()
                                    .eq(AttrEntity::getGroupId, attrGroupEntity.getId())
                                    // spu 只需要查询 基本属性, 不需要查询销售属性
                                    .eq(AttrEntity::getType, 1)
                    )
            )
        );

        return  groupEntities;
    }

}