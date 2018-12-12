package com.qianfeng.web_crew.domian.Comment;

import com.qianfeng.web_crew.domian.Comment.CommentBean;
import lombok.Data;

import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-5
 * Time:下午7:02
 * Vision:1.1
 * Description:产品评论的实体类
 */
@Data
public class ProductComment {
    private List<CommentBean> CommentsCount;
}
