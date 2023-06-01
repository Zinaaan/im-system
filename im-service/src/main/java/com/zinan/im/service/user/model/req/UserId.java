package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/05/31 17:02
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserId extends RequestBase {

    @NotNull(message = "The userId can not be null")
    @Length(min = 1, message = "The length of userId can not be 0")
    private String userId;
}