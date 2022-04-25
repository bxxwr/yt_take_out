package com.dy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.common.R;
import com.dy.dto.DishDto;
import com.dy.entity.*;
import com.dy.mapper.CategoryMapper;
import com.dy.mapper.DishFlavorMapper;
import com.dy.mapper.DishMapper;
import com.dy.mapper.SetmealDishMapper;
import com.dy.service.DishService;
import com.dy.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Resource
    private RedisUtils redisUtils;


    @Override
    public R<Page> pageQuery(Page pageInfo, String name) {
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishMapper.selectPage(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long categoryId = record.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            dishDtoList.add(dishDto);
        }
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDto dishDto) {

        dishMapper.insert(dishDto);
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dishFlavorMapper.insert(flavor);
        }

        //flavors = flavors.stream().map((item) ->{
        //    item.setDishId(dishId);
        //    return item;
        //}).collect(Collectors.toList());
        //dishFlavorService.saveBatch(flavors);
        String image = dishDto.getImage();
        redisUtils.save2Db(image);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDto dishDto) {
        dishMapper.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorMapper.delete(queryWrapper);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
            dishFlavorMapper.insert(flavor);
        }
        String image = dishDto.getImage();
        redisUtils.save2Db(image);
    }

    @Override
    public void updateStatusById(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId,id);
        Dish dish = dishMapper.selectById(id);
        if (dish.getStatus() == 0){
                dish.setStatus(1);
            }else {
                dish.setStatus(0);
            }
            dishMapper.updateById(dish);
    }
    //@Override
    //public R<List<Dish>> findDishByCategoryId(Dish dish) {
    //
    //    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //    queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
    //    queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //    List<Dish> dishes = dishMapper.selectList(queryWrapper);
    //    return R.success(dishes);
    //}

    @Override
    public R<List<DishDto>> findDishByCategoryId(Dish dish) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish dish1 : dishes) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            Long categoryId = dish1.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if (category != null){
                String name = category.getName();
                dishDto.setCategoryName(name);
            }

            //当前菜品ID
            Long dish1Id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dish1Id);
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            dishDtoList.add(dishDto);

        }

        return R.success(dishDtoList);
    }


    @Override
    public DishDto getByWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        //dishFlavorMapper.selectById(id);
        return dishDto;
    }
}
