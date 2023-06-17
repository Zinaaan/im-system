package com.zinan.im.service.group.model.resp;

import com.zinan.im.service.group.dao.ImGroupEntity;
import lombok.Data;
import java.util.List;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;
}
