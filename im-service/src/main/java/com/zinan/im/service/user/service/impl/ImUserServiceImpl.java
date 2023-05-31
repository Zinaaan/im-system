package com.zinan.im.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.DelFlagEnum;
import com.zinan.im.common.enums.UserErrorCode;
import com.zinan.im.common.exception.ApplicationException;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import com.zinan.im.service.user.dao.mapper.ImUserDataMapper;
import com.zinan.im.service.user.model.req.*;
import com.zinan.im.service.user.model.resq.GetUserInfoResp;
import com.zinan.im.service.user.model.resq.ImportUserResp;
import com.zinan.im.service.user.service.ImUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lzn
 * @date 2023/05/30 16:55
 * @description
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    private static final Logger log = LoggerFactory.getLogger(ImUserServiceImpl.class);

    private final ImUserDataMapper imUserDataMapper;

    public ImUserServiceImpl(ImUserDataMapper imUserDataMapper) {
        this.imUserDataMapper = imUserDataMapper;
    }

    @Override
    public ResponseVO<?> importUser(ImportUserReq req) {

        if (req.getUserData().size() > 100) {
            // exceed capacity
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        List<String> successIdList = new ArrayList<>();
        List<String> errorIdList = new ArrayList<>();

        req.getUserData().forEach(e -> {
            try {
                e.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(e);
                // Successfully adding new data
                if (insert == 1) {
                    successIdList.add(e.getUserId());
                }
            } catch (Exception ex) {
                log.error("Error in importing user " + e.getUserId() + ": " + ex.getMessage());
                errorIdList.add(e.getUserId());
            }
        });

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIdList(successIdList);
        resp.setErrorIdList(errorIdList);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.in("user_id", req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        Map<String, ImUserDataEntity> map = userDataEntities.stream().collect(Collectors.toMap(ImUserDataEntity::getUserId, Function.identity()));
//        for (ImUserDataEntity data : userDataEntities) {
//            map.put(data.getUserId(), data);
//        }
        List<String> failUser = req.getUserIds().stream().filter(user -> !map.containsKey(user)).collect(Collectors.toList());
//        for (String uid: req.getUserIds()) {
//            if(!map.containsKey(uid)){
//                failUser.add(uid);
//            }
//        }
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> getSingleUserInfo(UserId req) {
        QueryWrapper<ImUserDataEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("app_id", req.getAppId());
        objectQueryWrapper.eq("user_id", req.getUserId());
        objectQueryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity entity = imUserDataMapper.selectOne(objectQueryWrapper);
        if (entity == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO<?> deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> successIdList = new ArrayList<>();
        List<String> errorIdList = new ArrayList<>();

        req.getUserId().forEach(userId -> {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            int update;
            try {
                update = imUserDataMapper.update(entity, wrapper);
                if (update > 0) {
                    successIdList.add(userId);
                } else {
                    errorIdList.add(userId);
                }
            } catch (Exception e) {
                log.error("Error in deleting user: " + e.getMessage());
                errorIdList.add(userId);
            }
        });

        ImportUserResp resp = new ImportUserResp();
        resp.setErrorIdList(successIdList);
        resp.setErrorIdList(errorIdList);
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("user_id", req.getUserId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        if (user == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity updateEntity = new ImUserDataEntity();
        BeanUtils.copyProperties(req, updateEntity);

        updateEntity.setAppId(null);
        updateEntity.setUserId(null);

        int update = imUserDataMapper.update(updateEntity, query);
//        if(update == 1){
//            UserModifyPack pack = new UserModifyPack();
//            BeanUtils.copyProperties(req,pack);
//        }

        return null;
    }

    @Override
    public ResponseVO<?> login(LoginReq req) {
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getUserSequence(GetUserSequenceReq req) {
        return null;
    }
}
