package com.zinan.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.*;
import com.zinan.im.common.exception.ApplicationException;
import com.zinan.im.service.friendship.dao.ImFriendshipEntity;
import com.zinan.im.service.friendship.dao.ImFriendshipRequestEntity;
import com.zinan.im.service.friendship.dao.mapper.ImFriendshipMapper;
import com.zinan.im.service.friendship.dao.mapper.ImFriendshipRequestMapper;
import com.zinan.im.service.friendship.model.req.*;
import com.zinan.im.service.friendship.model.resp.CheckFriendshipResp;
import com.zinan.im.service.friendship.model.resp.ImportFriendShipResp;
import com.zinan.im.service.friendship.service.ImFriendshipService;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import com.zinan.im.service.user.model.req.UserId;
import com.zinan.im.service.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Service
public class ImFriendshipServiceImpl implements ImFriendshipService {

    private final ImFriendshipMapper imFriendshipMapper;

    private final ImFriendshipRequestMapper imFriendshipRequestMapper;

    private final ImUserService imUserService;

    public ImFriendshipServiceImpl(ImFriendshipMapper imFriendshipMapper, ImFriendshipRequestMapper imFriendshipRequestMapper,
                                   ImUserService imUserService) {
        this.imFriendshipMapper = imFriendshipMapper;
        this.imFriendshipRequestMapper = imFriendshipRequestMapper;
        this.imUserService = imUserService;
    }

    @Override
    public ResponseVO<?> importFriendship(ImportFriendshipReq req) {

        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendshipErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImportFriendshipReq.ImportFriendDto dto : req.getFriendItem()) {
            ImFriendshipEntity entity = new ImFriendshipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendshipMapper.insert(entity);
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

        // Return directly if the current user does not exist
        ResponseVO<?> fromInfo = imUserService.getSingleUserInfo(userId);
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        // Return directly if the friend does not exist
        ResponseVO<?> toInfo = imUserService.getSingleUserInfo(userId);
        if (!toInfo.isOk()) {
            return toInfo;
        }

        ImUserDataEntity data = (ImUserDataEntity) toInfo.getData();

        // FriendAllowType 1: No verification required, 2: Verification required
        if (data.getFriendAllowType() != null && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            return doAddFriend(req);
        }

        // Verification
        LambdaQueryWrapper<ImFriendshipEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        queryWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        queryWrapper.eq(ImFriendshipEntity::getToId, req.getToItem().getToId());
        ImFriendshipEntity fromItem = imFriendshipMapper.selectOne(queryWrapper);
        if (fromItem != null && fromItem.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
            return ResponseVO.successResponse(FriendshipErrorCode.TO_IS_YOUR_FRIEND);
        }

        // Adding friendship request metadata to the database
        ResponseVO<?> responseVO = addFriendshipRequest(req);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> updateFriend(UpdateFriendReq req) {
        UserId userId = new UserId();
        userId.setUserId(req.getFromId());
        userId.setAppId(req.getAppId());

        // Return directly if the current user does not exist
        ResponseVO<?> fromInfo = imUserService.getSingleUserInfo(userId);
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        // Return directly if the friend does not exist
        ResponseVO<?> toInfo = imUserService.getSingleUserInfo(userId);
        if (!toInfo.isOk()) {
            return toInfo;
        }

        return doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO<?> deleteFriend(DeleteFriendReq req) {

        LambdaQueryWrapper<ImFriendshipEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        deleteWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        deleteWrapper.eq(ImFriendshipEntity::getToId, req.getToId());

        // Checking if the current record exists
        ImFriendshipEntity fromItem = imFriendshipMapper.selectOne(deleteWrapper);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }

        if (fromItem.getStatus() != FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
            // The friend has already been deleted
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_IS_DELETED);
        }

        ImFriendshipEntity delete = new ImFriendshipEntity();
        delete.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendshipMapper.update(delete, deleteWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteAllFriend(DeleteFriendReq req) {
        LambdaQueryWrapper<ImFriendshipEntity> deleteAllWrapper = new LambdaQueryWrapper<>();
        deleteAllWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        deleteAllWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        deleteAllWrapper.eq(ImFriendshipEntity::getStatus, FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendshipEntity deleteAll = new ImFriendshipEntity();
        deleteAll.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendshipMapper.update(deleteAll, deleteAllWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getFriendship(GetRelationReq req) {

        LambdaQueryWrapper<ImFriendshipEntity> friendWrapper = new LambdaQueryWrapper<>();
        friendWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        friendWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        friendWrapper.eq(ImFriendshipEntity::getToId, req.getToId());

        ImFriendshipEntity friendEntity = imFriendshipMapper.selectOne(friendWrapper);
        if (friendEntity == null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.RELATIONSHIP_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(friendEntity);
    }

    @Override
    public ResponseVO<?> getAllFriendship(GetAllFriendshipReq req) {

        LambdaQueryWrapper<ImFriendshipEntity> allFriendWrapper = new LambdaQueryWrapper<>();
        allFriendWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        allFriendWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());

        return ResponseVO.successResponse(imFriendshipMapper.selectList(allFriendWrapper));
    }

    @Override
    public ResponseVO<?> checkFriendship(CheckFriendshipReq req) {

        List<CheckFriendshipResp> respList;
        // one-side verification
        if (req.getCheckType() == CheckFriendshipTypeEnum.SINGLE.getType()) {
            respList = imFriendshipMapper.checkFriendship(req);
        } else {
            respList = imFriendshipMapper.checkFriendshipBoth(req);
        }

        // For those friends who don't have any relationship with current user should also return records (which status is '0')
        List<CheckFriendshipResp> notPresentInDatabase = req.getToIds().stream()
                .filter(toId -> respList.stream().noneMatch(resp -> resp.getToId().equals(toId)))
                .map(toId -> new CheckFriendshipResp(req.getFromId(), toId, 0)).collect(Collectors.toList());
        respList.addAll(notPresentInDatabase);

        // two-side or none relationship verification
        return ResponseVO.successResponse(respList);
    }

    @Override
    public ResponseVO<?> addToBlackList(AddFriendshipBlackReq req) {

        UserId userId = new UserId();
        userId.setUserId(req.getFromId());
        userId.setAppId(req.getAppId());

        // Return directly if the current user does not exist
        ResponseVO<?> fromInfo = imUserService.getSingleUserInfo(userId);
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        // Return directly if the friend does not exist
        ResponseVO<?> toInfo = imUserService.getSingleUserInfo(userId);
        if (!toInfo.isOk()) {
            return toInfo;
        }

        LambdaQueryWrapper<ImFriendshipEntity> addWrapper = new LambdaQueryWrapper<>();
        addWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        addWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        addWrapper.eq(ImFriendshipEntity::getToId, req.getToId());

        ImFriendshipEntity addEntity = imFriendshipMapper.selectOne(addWrapper);
        if (addEntity == null) {
            addEntity = new ImFriendshipEntity();
            addEntity.setAppId(req.getAppId());
            addEntity.setFromId(req.getFromId());
            addEntity.setToId(req.getToId());
            addEntity.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            addEntity.setCreateTime(System.currentTimeMillis());
            int insert = imFriendshipMapper.insert(addEntity);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // Return directly if friend is already been blacked out
            if (addEntity.getBlack() != null && addEntity.getBlack().equals(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode())) {
                return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_IS_BLACK);
            }

            addEntity.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            int update = imFriendshipMapper.update(addEntity, addWrapper);
            if (update != 1) {
                return ResponseVO.errorResponse(FriendshipErrorCode.ADD_BLACK_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteFromBlackList(DeleteBlackReq req) {

        LambdaQueryWrapper<ImFriendshipEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        deleteWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        deleteWrapper.eq(ImFriendshipEntity::getToId, req.getToId());

        // Checking if the current record exists
        ImFriendshipEntity fromItem = imFriendshipMapper.selectOne(deleteWrapper);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        // Set the black status to deleted
        ImFriendshipEntity delete = new ImFriendshipEntity();
        delete.setStatus(FriendshipStatusEnum.BLACK_STATUS_DELETE.getCode());
        imFriendshipMapper.update(delete, deleteWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> checkIfInBlackList(CheckFriendshipReq req) {

        List<CheckFriendshipResp> respList;
        // one-side verification
        if (req.getCheckType() == CheckFriendshipTypeEnum.SINGLE.getType()) {
            respList = imFriendshipMapper.checkFriendshipBlack(req);
        } else {
            respList = imFriendshipMapper.checkFriendshipBlackBoth(req);
        }

        // For those friends who are not in the blacklist for current user should also return records (which status is '0')
        List<CheckFriendshipResp> notPresentInDatabase = req.getToIds().stream()
                .filter(toId -> respList.stream().noneMatch(resp -> resp.getToId().equals(toId)))
                .map(toId -> new CheckFriendshipResp(req.getFromId(), toId, 0)).collect(Collectors.toList());
        respList.addAll(notPresentInDatabase);

        // two-side or none relationship verification
        return ResponseVO.successResponse(respList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addFriendshipRequest(AddFriendReq req) {

        LambdaQueryWrapper<ImFriendshipRequestEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendshipRequestEntity::getAppId, req.getAppId());
        queryWrapper.eq(ImFriendshipRequestEntity::getFromId, req.getFromId());
        queryWrapper.eq(ImFriendshipRequestEntity::getToId, req.getToItem().getToId());

        ImFriendshipRequestEntity entity = imFriendshipRequestMapper.selectOne(queryWrapper);
        if (entity == null) {
            entity = new ImFriendshipRequestEntity();
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            entity.setToId(req.getToItem().getToId());
            entity.setAddSource(req.getToItem().getAddSource());
            entity.setAddWording(req.getToItem().getAddWording());
            entity.setCreateTime(System.currentTimeMillis());
            entity.setReadStatus(0);
            entity.setApproveStatus(0);
            entity.setRemark(req.getToItem().getRemark());

            int insert = imFriendshipRequestMapper.insert(entity);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendshipErrorCode.ADD_FRIEND_REQUEST_ERROR);
            }
        } else {
            if (StringUtils.isNoneBlank(req.getToItem().getAddSource())) {
                entity.setAddSource(req.getToItem().getAddSource());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getRemark())) {
                entity.setRemark(req.getToItem().getRemark());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getAddWording())) {
                entity.setAddWording(req.getToItem().getAddWording());
            }

            entity.setReadStatus(0);
            entity.setApproveStatus(0);
            imFriendshipRequestMapper.update(entity, queryWrapper);
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> approveFriendRequest(ApproverFriendRequestReq req) {

        LambdaQueryWrapper<ImFriendshipRequestEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendshipRequestEntity::getAppId, req.getAppId());
        queryWrapper.eq(ImFriendshipRequestEntity::getId, req.getId());

        ImFriendshipRequestEntity entity = imFriendshipRequestMapper.selectOne(queryWrapper);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        // Each user only can approve the friendship request which send to themselves
        if (!entity.getToId().equals(req.getOperator())) {
            throw new ApplicationException(FriendshipErrorCode.NOT_APPROVER_OTHER_MAN_REQUEST);
        }

        // Update request status
        entity.setApproveStatus(req.getStatus());
        entity.setUpdateTime(System.currentTimeMillis());
        int update = imFriendshipRequestMapper.update(entity, queryWrapper);
        if (update != 1) {
            throw new ApplicationException(FriendshipErrorCode.APPROVE_FRIEND_REQUEST_ERROR);
        }

        if (ApproverFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {
            // Add this user (fromId) as your (toId) friend
            FriendDto friendDto = new FriendDto();
            friendDto.setToId(entity.getToId());
            friendDto.setRemark(entity.getRemark());
            friendDto.setAddSource(entity.getAddSource());
            friendDto.setAddWording(entity.getAddWording());

            AddFriendReq addFriendReq = new AddFriendReq();
            addFriendReq.setFromId(entity.getFromId());
            addFriendReq.setToItem(friendDto);
            addFriendReq.setAppId(entity.getAppId());
            addFriendReq.setOperator(entity.getToId());

            ResponseVO<?> responseVO = doAddFriend(addFriendReq);
            if (!responseVO.isOk()) {
                throw new ApplicationException(FriendshipErrorCode.APPROVE_FRIEND_REQUEST_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getFriendshipRequest(GetFriendshipRequestReq req) {

        LambdaQueryWrapper<ImFriendshipRequestEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendshipRequestEntity::getAppId, req.getAppId());
        queryWrapper.eq(ImFriendshipRequestEntity::getToId, req.getFromId());
        List<ImFriendshipRequestEntity> requestList = imFriendshipRequestMapper.selectList(queryWrapper);

        return ResponseVO.successResponse(requestList);
    }

    @Override
    public ResponseVO<?> readFriendshipRequest(ReadFriendshipRequestReq req) {

        LambdaQueryWrapper<ImFriendshipRequestEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendshipRequestEntity::getAppId, req.getAppId());
        queryWrapper.eq(ImFriendshipRequestEntity::getToId, req.getFromId());

        ImFriendshipRequestEntity update = new ImFriendshipRequestEntity();
        update.setReadStatus(1);
        imFriendshipRequestMapper.update(update, queryWrapper);

        return ResponseVO.successResponse();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> doAddFriend(AddFriendReq req) {
        // Friend table adding two records, A-B and B-A
        // A-B
        LambdaQueryWrapper<ImFriendshipEntity> fromWrapper = new LambdaQueryWrapper<>();
        fromWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        fromWrapper.eq(ImFriendshipEntity::getFromId, req.getFromId());
        fromWrapper.eq(ImFriendshipEntity::getToId, req.getToItem().getToId());
        // Checking if the current record exists
        ImFriendshipEntity fromItem = imFriendshipMapper.selectOne(fromWrapper);
        if (fromItem == null) {
            // Adding this person to your friend
            fromItem = new ImFriendshipEntity();
            fromItem.setAppId(req.getAppId());
            fromItem.setFromId(req.getFromId());
            BeanUtils.copyProperties(req.getToItem(), fromItem);
            fromItem.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insertRs = imFriendshipMapper.insert(fromItem);
            if (insertRs != 1) {
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // The status of your relationship is normal, return directly
            if (fromItem.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                throw new ApplicationException(FriendshipErrorCode.TO_IS_YOUR_FRIEND);
            }

            // The status of your relationship is not normal, add necessary information and change status to "added"
            ImFriendshipEntity update = new ImFriendshipEntity();
            if (StringUtils.isNoneBlank(req.getToItem().getAddSource())) {
                update.setAddSource(req.getToItem().getAddSource());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getRemark())) {
                update.setRemark(req.getToItem().getRemark());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getExtra())) {
                update.setExtra(req.getToItem().getExtra());
            }

            // Set the status to added
            update.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

            int updateRs = imFriendshipMapper.update(update, fromWrapper);
            if (updateRs != 1) {
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        }

        // B-A
        LambdaQueryWrapper<ImFriendshipEntity> toWrapper = new LambdaQueryWrapper<>();
        toWrapper.eq(ImFriendshipEntity::getAppId, req.getAppId());
        toWrapper.eq(ImFriendshipEntity::getFromId, req.getToItem().getToId());
        toWrapper.eq(ImFriendshipEntity::getToId, req.getFromId());
        ImFriendshipEntity toItem = imFriendshipMapper.selectOne(toWrapper);
        if (toItem == null) {
            // Adding this person to your friend
            toItem = new ImFriendshipEntity();
            toItem.setAppId(req.getAppId());
            toItem.setFromId(req.getFromId());
            BeanUtils.copyProperties(req.getToItem(), toItem);
            toItem.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem.setCreateTime(System.currentTimeMillis());
            int insertRs = imFriendshipMapper.insert(toItem);
            if (insertRs != 1) {
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // The status of your relationship is not normal, add necessary information and change status to "added"
            ImFriendshipEntity update = new ImFriendshipEntity();
            if (StringUtils.isNoneBlank(req.getToItem().getAddSource())) {
                update.setAddSource(req.getToItem().getAddSource());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getRemark())) {
                update.setRemark(req.getToItem().getRemark());
            }
            if (StringUtils.isNoneBlank(req.getToItem().getExtra())) {
                update.setExtra(req.getToItem().getExtra());
            }

            // Set the status to added
            update.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

            int updateRs = imFriendshipMapper.update(update, toWrapper);
            if (updateRs != 1) {
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> doUpdateFriend(String fromId, FriendDto dto, Integer appId) {

        UpdateWrapper<ImFriendshipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendshipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendshipEntity::getRemark, dto.getRemark())
                .set(ImFriendshipEntity::getExtra, dto.getExtra())
                .eq(ImFriendshipEntity::getAppId, appId)
                .eq(ImFriendshipEntity::getFromId, fromId)
                .eq(ImFriendshipEntity::getToId, dto.getToId());

        imFriendshipMapper.update(null, updateWrapper);

        return ResponseVO.successResponse();
    }
}
