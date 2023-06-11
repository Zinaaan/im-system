package com.zinan.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zinan.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author lzn
 * @date 2023/06/04 15:30
 * @description
 */
@Mapper
@Repository
public interface ImFriendshipGroupMemberMapper extends BaseMapper<ImFriendShipGroupMemberEntity> {
}
