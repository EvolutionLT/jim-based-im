package cn.ideamake.components.im.common.constants;

public interface Constants {

    interface SERVER_URL {

        String GET_USER_INFO = "ws.server:8082/app/period/im-login";
        String USER_LOGIN = "ws.server:8082/im/app/period/im-login";
    }

    interface RESULT_STATUS {

        String RESULT_STATUS_SUCCESS = "success";
        String RESULT_STATUS_ERROR = "error";
    }

    interface ITEM_LABEL {
        String PERIOD = "PERIOD";
    }

    interface PEROID {
        String USER_TOKEN="USER_TOKEN";

    }

    interface FRIENDS {
        String LABEL = "FRIENDS_LABEL";
    }
    interface USER {
        String PREFIX = "user";
        String INFO = "info";
        String FRIENDS = "friends";
    }
}
