package com.zinan.im.common.enums.command;

/**
 * @author lzn
 * @date 2023/07/04 14:03
 * @description System command for login, generally use hexadecimal instead of decimal
 */
public enum SystemCommand {

    //Heart beats -> decimal: 9999, hexadecimal: 0x270f
    PING(0x270f),

    /**
     * Log in -> decimal: 9000, hexadecimal: 0x2328
     */
    LOGIN(0x2328),

    //Log in acknowledgement -> decimal: 9001, hexadecimal: 0x2329
    LOGIN_ACK(0x2329),

    //Log out -> decimal: 9003, hexadecimal: 0x232b
    LOGOUT(0x232b),

    // Mutual exclusive for log out notification, decimal: 9002, hexadecimal: 0x232a
    MUTUAL_LOGIN(0x232a);

    private final int command;

    SystemCommand(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }
}
