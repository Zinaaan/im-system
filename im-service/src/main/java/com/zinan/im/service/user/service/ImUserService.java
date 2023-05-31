package com.zinan.im.service.user.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.user.model.req.*;

/**
 * @author lzn
 * @date 2023/05/30 16:54
 * @description
 */
public interface ImUserService {

    ResponseVO<?> importUser(ImportUserReq importUserReq);

    ResponseVO<?> getUserInfo(GetUserInfoReq req);

    ResponseVO<?> getSingleUserInfo(UserId req);

    ResponseVO<?> deleteUser(DeleteUserReq req);

    ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req);

    ResponseVO<?> login(LoginReq req);

    ResponseVO<?> getUserSequence(GetUserSequenceReq req);
}
