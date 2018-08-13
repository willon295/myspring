package cn.willon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应数据类
 *
 * @author willon
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse {
    /**
     * 响应的状态码
     */
    private String status;

    /**
     * 响应的数据
     */
    private Object data;
}
