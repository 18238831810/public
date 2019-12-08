package com.cf.crs.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cf.crs.mapper.OrcaleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author frank
 * 2019/12/8
 **/
@Slf4j
@Service
@DS("orcale")
public class OrcaleService {

    @Autowired
    OrcaleMapper orcaleMapper;

}
