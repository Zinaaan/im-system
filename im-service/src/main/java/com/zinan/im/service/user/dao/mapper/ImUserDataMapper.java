package com.zinan.im.service.user.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author lzn
 * @date 2023/05/30 18:32
 * @description
 */
@Mapper
@Repository
public interface ImUserDataMapper extends BaseMapper<ImUserDataEntity> {
}
