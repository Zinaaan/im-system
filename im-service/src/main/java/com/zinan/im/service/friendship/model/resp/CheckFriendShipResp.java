package com.zinan.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description Verify the friendship for two person according to `status`
 * <p>
 * Friendship verification
 * **One-way verification**
 * 1. '1' means that only checking if current user (fromId) has added the corresponding friend (toId)
 * 2. '0' means that only checking if current user (fromId) didn't add the corresponding friend (toId)
 * <p>
 * **Two-way verification**
 * 1. '1' means that two people (fromId and toId) are already friends
 * 2. '2' means that current user (fromId) has added the corresponding friend (toId), but the corresponding friend didn't add the current user as a friend
 * 3. '3' means that current user (fromId) didn't add the corresponding friend (toId), but this friend has added the current user as a friend
 * 4. '4' means that two people (fromId and toId) are not friends
 * 
 * Blacklist verification
 * 
 * **One-way verification**
 * 1. '1' means that only checking if current user (fromId) has blacked out the corresponding friend (toId)
 * 2. '0' means that only checking if current user (fromId) didn't black out the corresponding friend (toId)
 * <p>
 * **Two-way verification**
 * 1. '1' means that two people (fromId and toId) are not blacked out with each other
 * 2. '2' means that current user (fromId) has blacked out corresponding friend (toId), but the corresponding friend didn't black out current user as a friend
 * 3. '3' means that current user (fromId) didn't black out the corresponding friend (toId), but this friend has blacked out the current user as a friend
 * 4. '4' means that two people (fromId and toId) are both blacked out with each other
 */
@Data
@AllArgsConstructor
public class CheckFriendShipResp {

    private String fromId;

    private String toId;

    private Integer status;
}
