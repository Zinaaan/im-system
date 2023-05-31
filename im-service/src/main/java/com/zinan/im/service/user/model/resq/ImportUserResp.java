package com.zinan.im.service.user.model.resq;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 18:59
 * @description
 */
@Data
public class ImportUserResp {

    private List<String> successIdList = new ArrayList<>();
    private List<String> errorIdList = new ArrayList<>();
}
