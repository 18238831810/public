/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.service;

import com.cf.crs.sys.entity.SysDictEntity;
import com.cf.crs.common.page.PageData;
import com.cf.crs.common.service.BaseService;
import com.cf.crs.sys.dto.SysDictDTO;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public interface SysDictService extends BaseService<SysDictEntity> {

    PageData<SysDictDTO> page(Map<String, Object> params);

    List<SysDictDTO> list(Map<String, Object> params);

    SysDictDTO get(Long id);

    void save(SysDictDTO dto);

    void update(SysDictDTO dto);

    void delete(Long[] ids);

    List<SysDictDTO> listByType(String dictType);
}