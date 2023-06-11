package com.zinan.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zinan.im.service.friendship.dao.ImFriendshipGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author lzn
 * @date 2023/06/04 12:54
 * @description
 */
@Mapper
@Repository
public interface ImFriendshipGroupMapper extends BaseMapper<ImFriendshipGroupEntity> {

}
