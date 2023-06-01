package com.zinan.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.FriendShipErrorCode;
import com.zinan.im.common.enums.FriendShipStatusEnum;
import com.zinan.im.service.friendship.dao.ImFriendShipEntity;
import com.zinan.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.zinan.im.service.friendship.model.req.*;
import com.zinan.im.service.friendship.model.resp.ImportFriendShipResp;
import com.zinan.im.service.friendship.service.ImFriendshipService;
import com.zinan.im.service.user.model.req.UserId;
import com.zinan.im.service.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Service
public class ImFriendshipServiceImpl implements ImFriendshipService {

    private final ImFriendShipMapper imFriendShipMapper;

    private final ImUserService imUserService;

    public ImFriendshipServiceImpl(ImFriendShipMapper imFriendShipMapper, ImUserService imUserService) {
        this.imFriendShipMapper = imFriendShipMapper;
        this.imUserService = imUserService;
    }

    @Override
    public ResponseVO<?> importFriendShip(ImportFriendShipReq req) {

        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImportFriendShipReq.ImportFriendDto dto : req.getFriendItem()) {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(dto.getToId());
                } else {
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(dto.getToId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> addFriend(AddFriendReq req) {
        UserId userId = new UserId();
        userId.setUserId(req.getFromId());
        userId.setAppId(req.getAppId());

        ResponseVO<?> fromInfo = imUserService.getSingleUserInfo(userId);
        if (fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<?> toInfo = imUserService.getSingleUserInfo(userId);
        if (toInfo.isOk()) {
            return toInfo;
        }

        return doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO<?> updateFriend(UpdateFriendReq req) {
        UserId userId = new UserId();
        userId.setUserId(req.getFromId());
        userId.setAppId(req.getAppId());

        ResponseVO<?> fromInfo = imUserService.getSingleUserInfo(userId);
        if (fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<?> toInfo = imUserService.getSingleUserInfo(userId);
        if (toInfo.isOk()) {
            return toInfo;
        }

        return doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO<?> deleteFriend(DeleteFriendReq req) {

        QueryWrapper<ImFriendShipEntity> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("app_id", req.getAppId());
        deleteWrapper.eq("from_id", req.getFromId());
        deleteWrapper.eq("to_id", req.getToId());

        // Checking if the current record exists
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(deleteWrapper);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }

        if (fromItem.getStatus() != FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
            // The friend has already been deleted
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        ImFriendShipEntity delete = new ImFriendShipEntity();
        delete.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(delete, deleteWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> deleteAllWrapper = new QueryWrapper<>();
        deleteAllWrapper.eq("app_id", req.getAppId());
        deleteAllWrapper.eq("from_id", req.getFromId());
        deleteAllWrapper.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity deleteAll = new ImFriendShipEntity();
        deleteAll.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(deleteAll, deleteAllWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getFriendRelationship(GetRelationReq req) {

        QueryWrapper<ImFriendShipEntity> friendWrapper = new QueryWrapper<>();
        friendWrapper.eq("app_id", req.getAppId());
        friendWrapper.eq("from_id", req.getFromId());
        friendWrapper.eq("to_id", req.getToId());

        ImFriendShipEntity friendEntity = imFriendShipMapper.selectOne(friendWrapper);
        if(friendEntity == null){
            return ResponseVO.errorResponse(FriendShipErrorCode.RELATIONSHIP_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(friendEntity);
    }

    @Override
    public ResponseVO<?> getAllFriendRelationship(GetAllFriendShipReq req) {

        QueryWrapper<ImFriendShipEntity> allFriendWrapper = new QueryWrapper<>();
        allFriendWrapper.eq("app_id", req.getAppId());
        allFriendWrapper.eq("from_id", req.getFromId());

        return ResponseVO.successResponse(imFriendShipMapper.selectList(allFriendWrapper));
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> doAddFriend(String fromId, FriendDto dto, Integer appId) {
        // A-B
        // Friend table adding two records, A-B and B-A
        QueryWrapper<ImFriendShipEntity> addWrapper = new QueryWrapper<>();
        addWrapper.eq("app_id", appId);
        addWrapper.eq("from_id", fromId);
        addWrapper.eq("to_id", dto.getToId());
        // Checking if the current record exists
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(addWrapper);
        if (fromItem == null) {
            // Adding this person to your friend
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insertRs = imFriendShipMapper.insert(fromItem);
            if (insertRs != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // The status of your relationship is normal, return directly
            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
            }

            // The status of your relationship is not normal, add necessary information and change status to "added"
            ImFriendShipEntity update = new ImFriendShipEntity();
            if (StringUtils.isNoneBlank(dto.getAddSource())) {
                update.setAddSource(dto.getAddSource());
            }
            if (StringUtils.isNoneBlank(dto.getRemark())) {
                update.setRemark(dto.getRemark());
            }
            if (StringUtils.isNoneBlank(dto.getExtra())) {
                update.setExtra(dto.getExtra());
            }

            // Set the status to added
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

            int updateRs = imFriendShipMapper.update(update, addWrapper);
            if (updateRs != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> doUpdateFriend(String fromId, FriendDto dto, Integer appId) {

        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendShipEntity::getRemark, dto.getRemark())
                .set(ImFriendShipEntity::getExtra, dto.getExtra())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getFromId, fromId)
                .eq(ImFriendShipEntity::getToId, dto.getToId());

        imFriendShipMapper.update(null, updateWrapper);

        return ResponseVO.successResponse();
    }
}
