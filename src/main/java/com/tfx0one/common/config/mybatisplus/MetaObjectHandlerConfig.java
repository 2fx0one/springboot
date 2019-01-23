package com.tfx0one.common.config.mybatisplus;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    System.out.println("插入方法实体填充");
    setFieldValByName("create_date", new Date(), metaObject);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    System.out.println("更新方法实体填充");
    setFieldValByName("update_date", new Date(), metaObject);
//    setFieldValByName("email", LocalDateTime, metaObject);
  }
}
