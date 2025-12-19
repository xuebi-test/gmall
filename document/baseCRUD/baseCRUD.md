# 基础的 CRUD 代码编写

## 1. 根据父分类的 id 查询相应的子分类

### 为什么页面设计的是树状结构 而 不是表格

```
存在很多分类，但表格展示不够清晰。如果有两个商品归属不同的分类，但分类名称相同，会让人混淆。例如，手机、电脑和家电等商品都有配件，但在展示表格中很难分清属于哪个商品的配件。因此，应该采用树形结构进行展示。

即表格展示在分类数据量大时存在天然局限：同名分类易混淆、层级关系不直观、整体结构难把握。而树形结构通过视觉化的层次呈现，让每个分类的归属清晰可见，整体架构一目了然，是展现复杂分类体系的最优解。
```

![](https://oss.yiki.tech/gmall/20251219131246961.png)

### 表结构

> **自关联表结构**（即`parent_id`指向同一张表的`id`）是数据库设计中处理**树形结构**或**层次数据**的经典模式。**通过“父引用子、子再引用孙”的链式设计，用同一张表的`parent_id`字段指向`id`字段，实现了用二维表格存储任意深度树形结构数据的能力，既避免了为每个层级单独建表的冗余，又保证了分类体系可以无限扩展的灵活性。**常用于 电商平台的商品分类、组织架构管理、地区行政区划、菜单权限系统、评论的多级回复 场景。通过 `parent_id = 0` 获取所有一级分类后，可**逐级深入**：将任一级别的分类ID作为下一级的 `parent_id` 查询条件，即可层层获取其完整的子分类体系。

```sql
# 核心字段是 id 与 pid
CREATE TABLE `pms_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `name` char(50) DEFAULT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类id',
  `status` tinyint DEFAULT NULL COMMENT '是否显示[0-不显示，1显示]',
  `sort` int DEFAULT NULL COMMENT '排序',
  `icon` char(255) DEFAULT NULL COMMENT '图标地址',
  `unit` char(50) DEFAULT NULL COMMENT '计量单位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1433 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品三级分类';
```

```sql
# 使用递归查询，查询 id 为 2 以及其下所属的所有子分类
WITH RECURSIVE category_tree AS (
    -- 基础查询：找到id=2的节点
    SELECT id, name, parent_id, status, sort, icon, unit
    FROM pms_category
    WHERE id = 2
    
    UNION ALL
    
    -- 递归查询：找到所有子节点
    SELECT c.id, c.name, c.parent_id, c.status, c.sort, c.icon, c.unit
    FROM pms_category c
    INNER JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree;
```

![](https://oss.yiki.tech/gmall/20251219133842213.png)

![](https://oss.yiki.tech/gmall/20251219140742282.png)

### 接口编写

> 思路：**一次性查询完整的分类树，在前端构建树状结构展示（需要查看完整的分类层级结构），并支持选中任意节点时获取其所有子分类（选中"手机"分类，要显示所有手机的子分类）**。

```java
    @Override
    public List<CategoryEntity> queryCategoriesByPid(Long pid) {

        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();

        // 根据参数判断是否需要拼接查询条件
        if (pid != -1) {
            wrapper.eq(CategoryEntity::getParentId, pid);
        }

        /**
         * 如果 pid 为 -1 则查询全部相当于此处 list(null), wrapper 没有拼接查询条件;
         *      select * from pms_category;
         * 如果 pid 不为 -1 则查询某一个分类下的子分类, 相当于 list(new QueryWrapper<CategoryEntity>().eq("parent_id", pid))
         *      select * from pms_category where parent_id = pid;
         */
        return list(wrapper);
    }
```

![](https://oss.yiki.tech/gmall/20251219141304317.png)

![](https://oss.yiki.tech/gmall/20251219141142685.png)