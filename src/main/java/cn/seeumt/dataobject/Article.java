package cn.seeumt.dataobject;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Seeumt
 * @since 2019-12-21
 */
@Data
public class Article implements Serializable {


    private static final long serialVersionUID = 6694761945472397078L;
    /**
     * //文章id
     */
    @TableId
    private String articleId;

    /**
     * //标题
     */
    private String title;

    /**
     * //md内容
     */
    private String mdContent;

    /**
     * //html内容
     */
    private String htmlContent;

    /**
     * //创建时间
     */
    private LocalDateTime createTime;

    /**
     * //更新时间
     */
    private LocalDateTime updateTime;

    /**
     * //默认为1表示通过
     */
    private Boolean enabled;

    /**
     * //默认为0表示未删除
     */
    private Boolean deleted;

    /**
     * //封面图片
     */
    private String coverPicture;

    /**
     * //顶部图片
     */
    private String headPicture;

    /**
     * //用户id
     */
    private String userId;


}
