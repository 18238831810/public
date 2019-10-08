/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cf.crs.log.service.SysLogLoginService;
import com.cf.crs.common.constant.Constant;
import com.cf.crs.common.page.PageData;
import com.cf.crs.common.service.impl.BaseServiceImpl;
import com.cf.crs.common.utils.ConvertUtils;
import com.cf.crs.log.mapper.SysLogLoginDao;
import com.cf.crs.log.dto.SysLogLoginDTO;
import com.cf.crs.log.entity.SysLogLoginEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 登录日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
public class SysLogLoginServiceImpl extends BaseServiceImpl<SysLogLoginDao, SysLogLoginEntity> implements SysLogLoginService {

    @Override
    public PageData<SysLogLoginDTO> page(Map<String, Object> params) {
        IPage<SysLogLoginEntity> page = baseDao.selectPage(
            getPage(params, Constant.CREATE_DATE, false),
            getWrapper(params)
        );

        return getPageData(page, SysLogLoginDTO.class);
    }

    @Override
    public List<SysLogLoginDTO> list(Map<String, Object> params) {
        List<SysLogLoginEntity> entityList = baseDao.selectList(getWrapper(params));

        return ConvertUtils.sourceToTarget(entityList, SysLogLoginDTO.class);
    }

    private QueryWrapper<SysLogLoginEntity> getWrapper(Map<String, Object> params){
        String status = (String) params.get("status");
        String creatorName = (String) params.get("creatorName");

        QueryWrapper<SysLogLoginEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.like(StringUtils.isNotBlank(creatorName), "creator_name", creatorName);

        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysLogLoginEntity entity) {
        insert(entity);
    }

}