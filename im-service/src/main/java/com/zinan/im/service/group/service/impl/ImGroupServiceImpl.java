package com.zinan.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.GroupErrorCode;
import com.zinan.im.common.enums.GroupStatusEnum;
import com.zinan.im.common.exception.ApplicationException;
import com.zinan.im.service.group.dao.ImGroupEntity;
import com.zinan.im.service.group.dao.mapper.ImGroupMapper;
import com.zinan.im.service.group.model.req.GetGroupReq;
import com.zinan.im.service.group.model.req.ImportGroupReq;
import com.zinan.im.service.group.service.ImGroupService;
import com.zinan.im.service.user.model.req.ImportUserReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author lzn
 * @date 2023/06/10 21:40
 * @description
 */
@Service
public class ImGroupServiceImpl implements ImGroupService {

    private final ImGroupMapper imGroupMapper;

    public ImGroupServiceImpl(ImGroupMapper imGroupMapper) {
        this.imGroupMapper = imGroupMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> importGroup(ImportGroupReq req) {

        if (StringUtils.isNotEmpty(req.getGroupId())) {
            LambdaQueryWrapper<ImGroupEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());
            wrapper.eq(ImGroupEntity::getAppId, req.getAppId());
            if (imGroupMapper.selectCount(wrapper) > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_EXIST);
            }

        } else {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        BeanUtils.copyProperties(req, imGroupEntity);
        if (req.getCreateTime() == null) {
            imGroupEntity.setCreateTime(System.currentTimeMillis());
        }

        if (req.getCreateTime() == null) {
            imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        }

        int insert = imGroupMapper.insert(imGroupEntity);
        if (insert != 1) {
            throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getGroup(GetGroupReq req) {
        LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImGroupEntity::getAppId, req.getAppId());
        query.eq(ImGroupEntity::getGroupId, req.getGroupId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(query);
        if (imGroupEntity == null) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imGroupEntity);
    }
}
