package com.tfx0one.modules.ec.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfx0one.modules.ec.entity.OrderDetailEntity;
import com.tfx0one.modules.ec.service.OrderDetailService;
import com.tfx0one.common.utils.Pagination;;
import com.tfx0one.common.utils.R;



/**
 * 订单详情表
 *
 * @author 2fx0one
 * @email 2fx0one@gmail.com
 * @date 2019-07-15 21:31:30
 */
@RestController
@RequestMapping("ec/orderdetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ec:orderdetail:list")
    public R list(@RequestParam Map<String, Object> params){
        Pagination page = orderDetailService.queryPage(params);

        return R.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ec:orderdetail:info")
    public R info(@PathVariable("id") Long id){
		OrderDetailEntity orderDetail = orderDetailService.getById(id);

        return R.ok(orderDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ec:orderdetail:save")
    public R save(@RequestBody OrderDetailEntity orderDetail){
		orderDetailService.save(orderDetail);

        return R.ok("保存成功！");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ec:orderdetail:update")
    public R update(@RequestBody OrderDetailEntity orderDetail){
		orderDetailService.updateById(orderDetail);

        return R.ok("修改成功！");
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ec:orderdetail:delete")
    public R delete(@RequestBody Long[] ids){
		orderDetailService.removeByIds(Arrays.asList(ids));

        return R.ok("删除成功！");
    }

}
