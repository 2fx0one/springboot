package com.tfx0one.modules.ec.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfx0one.common.utils.Pagination;
import com.tfx0one.common.utils.QueryPage;

import com.tfx0one.modules.ec.dao.BrandDao;
import com.tfx0one.modules.ec.entity.BrandEntity;
import com.tfx0one.modules.ec.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public Pagination queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new QueryPage<BrandEntity>().getPage(params),
                Wrappers.<BrandEntity>lambdaQuery()
//                new LambdaQueryWrapper<BrandEntity>()
        );

        return new Pagination<>(page);
    }

}