package cn.seeumt.controller;

import cn.seeumt.dataobject.Post;
import cn.seeumt.dto.PostDTO;
import cn.seeumt.dto.PostListDataItem;
import cn.seeumt.enums.TipsFlash;
import cn.seeumt.model.Comment;
import cn.seeumt.model.MyPageHelper;
import cn.seeumt.model.Thumber;
import cn.seeumt.service.CommentService;
import cn.seeumt.service.FollowService;
import cn.seeumt.service.PostService;
import cn.seeumt.utils.OnlineUtil;
import cn.seeumt.utils.ThumberUtil;
import cn.seeumt.utils.TreeUtil;
import cn.seeumt.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Seeumt
 */
@RestController
@RequestMapping("/posts")
@Slf4j
@CrossOrigin(origins = {"*"},allowCredentials = "true",allowedHeaders = {"*"})
public class PostController {


    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FollowService followService;

    @PostMapping("/")
    public ResultVO send(@RequestBody cn.seeumt.form.Post post) {
        OnlineUtil.setLastOperateTimeByUserId(post.getUserId());
        log.info("【发布动态】用户 {}发布动态",post.getUserId());
        return postService.send(post);
    }

    @GetMapping("/{postId}")
    public ResultVO getDto(@PathVariable("postId") String postId) {
        if ("".equals(postId)) {
            return ResultVO.error(TipsFlash.NULL_ARGUMENT);
        }
        return postService.getDto(postId);
    }

    @GetMapping("/one")
    public ResultVO get(String postId) {
        if ("".equals(postId)) {
            return ResultVO.error(TipsFlash.NULL_ARGUMENT);
        }
        return postService.get(postId);
    }
    @DeleteMapping("/{postId}")
    public ResultVO delete(@PathVariable("postId") String postId) {
        if ("".equals(postId)) {
            return ResultVO.error(TipsFlash.NULL_ARGUMENT);
        }
        return postService.delete(postId);
    }

    @PutMapping("/one")
    public ResultVO updateContent(String postId,String content) {
        if ("".equals(postId)) {
            return ResultVO.error(TipsFlash.NULL_ARGUMENT);
        }
        return postService.updateContent(postId,content);
    }



    @GetMapping("/")
    public ResultVO getRecommendPosts(@RequestParam(value = "userId", defaultValue = "\"\"", required = false) String userId,
                         @RequestParam(value = "currentNum") int currentNum,
                         @RequestParam(value = "size",required = false,defaultValue = "5") int size) {
        log.info("为用户 {}展示首页数据",userId);
        return postService.getRecommendPosts(userId, currentNum, size);
    }


    @GetMapping("/idols")
    public ResultVO getIdolsPosts(String userId,
                         @RequestParam(value = "currentNum") int currentNum,
                         @RequestParam(value = "size", required = false, defaultValue = "5") int size){
        log.info("为用户 {}展示关注数据",userId);
        return postService.getIdolsPosts(userId,currentNum,size);
    }




    @PostMapping("/detail")
    public ResultVO find(String userId) {
        Post post = postService.selectByUserId(userId);
        return ResultVO.success(post);
    }

    @GetMapping("/find")
    public List<Comment> findAllCommentsNew(String parentId) {
        List<Comment> levelCommentsList = commentService.findNextLevelCommentsByParentId(parentId);
        List<Comment> comments = TreeUtil.listToTree(levelCommentsList, parentId);
        return comments;
    }


    @PostMapping("/findPost")
    public ResultVO findPost(String postId) {
        PostDTO postDTO = postService.selectByPostId(postId);
        List<Comment> levelCommentsList = commentService.findNextLevelCommentsByParentId(postId);
        List<Comment> comments = TreeUtil.listToTree(levelCommentsList, postId);
        postDTO.setComments(comments);
        List<Thumber> thumbers = ThumberUtil.allThumbers(postId);
        postDTO.setThumbers(thumbers);
        return ResultVO.success(postDTO);
    }


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultVO search(@RequestParam(value = "keywords", required = false, defaultValue = "") String keywords) {
        log.info("通过{}搜索",keywords);
        return ResultVO.success( postService.search(keywords));
    }











}
