package com.zinan.im.common.constant;

/**
 * @author lzn
 * @date 2023/07/04 16:08
 * @description Constant pool for im-system
 */
public class Constants {

    /**
     * User id for channels
     */
    public static final String USER_ID = "userId";

    /**
     * App id for channels
     */
    public static final String APP_ID = "appId";

    public static final String CLIENT_TYPE = "clientType";

    public static final String READ_TIME = "readTime";

    public static class RedisConstants {

        /**
         * User session, format -> appId + USER_SESSION_CONSTANTS + userId
         * e.g. 10000:userSession:lld
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";
    }

    public static class RabbitConstants {

        /**
         * Message from Im system to User Service
         */
        public static final String IM_TO_USER_SERVICE = "pipelineToUserService";

        /**
         * Message from Im system to Message Service
         */
        public static final String IM_TO_MESSAGE_SERVICE = "pipelineToMessageService";

        /**
         * Message from Im system to Group Service
         */
        public static final String IM_TO_GROUP_SERVICE = "pipelineToGroupService";

        /**
         * Message from Friend Service to Im system
         */
        public static final String IM_TO_FRIENDSHIP_SERVICE = "pipelineToFriendshipService";

        /**
         * Message from Message Service to Im system
         */
        public static final String MESSAGE_SERVICE_TO_IM = "messageServiceToPipeline";

        /**
         * Message from Group Service to Im system
         */
        public static final String GROUP_SERVICE_TO_IM = "GroupServiceToPipeline";

        /**
         * Message from Friend Service to Im system
         */
        public static final String FRIENDSHIP_SERVICE_TO_IM = "friendShipToPipeline";

        public static final String STORE_P2P_MESSAGE = "storeP2PMessage";

        public static final String STORE_GROUP_MESSAGE = "storeGroupMessage";
    }
}
