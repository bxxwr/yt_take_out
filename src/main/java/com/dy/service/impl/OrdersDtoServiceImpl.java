package com.dy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.dto.OrdersDto;
import com.dy.mapper.OrdersDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersDtoServiceImpl extends ServiceImpl<OrdersDtoMapper, OrdersDto> {
}
