package com.tfx0one.modules.ec.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfx0one.common.utils.Pagination;
import com.tfx0one.modules.ec.entity.CategoryEntity;

import java.util.Map;

/**
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系
 *
 * @author 2fx0one
 * @email 2fx0one@gmail.com
 * @date 2019-09-23 00:14:35
 */
public interface CategoryService extends IService<CategoryEntity> {

    Pagination<CategoryEntity> queryPage(Map<String, Object> params, CategoryEntity category);
}

