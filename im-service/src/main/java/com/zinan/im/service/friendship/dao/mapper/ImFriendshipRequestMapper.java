package com.zinan.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zinan.im.service.friendship.dao.ImFriendshipRequestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author lzn
 * @date 2023/06/03 19:03
 * @description
 */
@Mapper
@Repository
public interface ImFriendshipRequestMapper extends BaseMapper<ImFriendshipRequestEntity> {
}
