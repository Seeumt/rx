package cn.seeumt.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Seeumt
 * @version 1.0
 * @date 2020/2/17 17:03
 */
@Data
public class CommentVO implements Serializable {
    private static final long serialVersionUID = -8090542044732428414L;
    @JsonProperty("cid")
    private String commentId;

    private Byte type;

    private String userId;

    @JsonProperty("userName")
    private String username;

    @JsonProperty("headImgSrc")
    private String faceIcon;

    private String targetUserId;

    @JsonProperty("targetUserName")
    private String targetUsername;

    @JsonProperty("sendMsg")
    private String content;

    private Date createTime;

    private Boolean enabled;

    private Boolean expand = false;

    private String parentId;

    private String apiRootId;

    private Integer childrenCount;

}
