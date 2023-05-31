package com.zinan.im.service.user.service.impl;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import com.zinan.im.service.user.dao.mapper.ImUserDataMapper;
import com.zinan.im.service.user.model.req.*;
import com.zinan.im.service.user.model.resq.GetUserInfoResp;
import com.zinan.im.service.user.model.resq.ImportUserResp;
import com.zinan.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 16:55
 * @description
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    private final ImUserDataMapper imUserDataMapper;

    public ImUserServiceImpl(ImUserDataMapper imUserDataMapper) {
        this.imUserDataMapper = imUserDataMapper;
    }

    @Override
    public ResponseVO<?> importUser(ImportUserReq req) {

        if (req.getUserData().size() > 100) {
            // Return exceed capacity
        }

        List<String> successIdList = new ArrayList<>();
        List<String> errorIdList = new ArrayList<>();
        req.getUserData().forEach(e -> {
            System.out.println("e: " + e);
            try {
                e.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(e);

                // Successfully add new data
                if (insert == 1) {
                    successIdList.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorIdList.add(e.getUserId());
            }
        });

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIdList(successIdList);
        resp.setErrorIdList(errorIdList);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        return null;
    }

    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        return null;
    }

    @Override
    public ResponseVO<?> deleteUser(DeleteUserReq req) {
        return null;
    }

    @Override
    public ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req) {
        return null;
    }

    @Override
    public ResponseVO<?> login(LoginReq req) {
        return null;
    }

    @Override
    public ResponseVO<?> getUserSequence(GetUserSequenceReq req) {
        return null;
    }
}
