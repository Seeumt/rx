package cn.seeumt.service.impl;
        import cn.seeumt.utils.WechatUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

import cn.seeumt.dao.PostMapper;
import cn.seeumt.dao.UserMapper;
import cn.seeumt.dataobject.*;
import cn.seeumt.dto.ImgDTO;
import cn.seeumt.dto.PostDTO;
import cn.seeumt.dto.PostListDataItem;
import cn.seeumt.enums.Tips;
import cn.seeumt.enums.TipsFlash;
import cn.seeumt.exception.TipsException;
import cn.seeumt.model.MyPageHelper;
import cn.seeumt.service.*;
import cn.seeumt.utils.UuidUtil;
import cn.seeumt.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Seeumt
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OssService ossService;
    @Autowired
    private FollowService followService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LoveService loveService;
    @Autowired
    private TagService tagService;
    @Autowired
    private MediaTagsService mediaTagsService;


    @Override
    public ResultVO getDto(String postId) {
        Post post = postMapper.selectById(postId);
        PostDTO postDTO = assemblePostDTO(post,null);
        return ResultVO.success(postDTO);
    }


    @Override
    public ResultVO getIdolsPosts(String userId, int currentNum, int size) {
        List<Follow> allIdol = followService.getAllIdol(userId);
        List<String> idolUserIds = allIdol.stream().map(Follow::getIdolId).collect(Collectors.toList());
        if (allIdol.size() == 0) {
            return ResultVO.success(Tips.NO_IDOL);
        }
        PageHelper.startPage(currentNum, size);
        List<Post> posts = postMapper.selectIdolPostsBatch(idolUserIds);
        PageInfo<Post> postPageInfo = new PageInfo<>(posts);
        MyPageHelper<PostDTO> myPageHelper = new MyPageHelper<>();
        BeanUtils.copyProperties(postPageInfo, myPageHelper);
        List<PostDTO> postDtos = postPageInfo.getList().stream().map(post -> {
            PostDTO postDTO = assemblePostDTO(post, userId);
            postDTO.setIsFollow(true);
            return postDTO;
        }).collect(Collectors.toList());
        myPageHelper.setId(0);
        myPageHelper.setList(postDtos);
        List<MyPageHelper> myPageHelpers = new ArrayList<>();
        MyPageHelper<PostDTO> myPageHelper1 = new MyPageHelper<>();
        myPageHelper1.setId(1);
        myPageHelper1.setPageNum(0);
        myPageHelper1.setPageSize(0);
        myPageHelper1.setSize(0);
        myPageHelper1.setOrderBy("");
        myPageHelper1.setStartRow(0);
        myPageHelper1.setEndRow(0);
        myPageHelper1.setTotal(0L);
        myPageHelper1.setPages(0);
        myPageHelper1.setList(Lists.newArrayList());
        myPageHelper1.setFirstPage(0);
        myPageHelper1.setPrePage(0);
        myPageHelper1.setNextPage(0);
        myPageHelper1.setLastPage(0);
        myPageHelper1.setFaPage(false);
        myPageHelper1.setLaPage(false);
        myPageHelper1.setHasPreviousPage(false);
        myPageHelper1.setHasNextPage(false);
        myPageHelper1.setNavigatePages(0);
        myPageHelper1.setNavigatepageNums(null);
        myPageHelpers.add(myPageHelper);
        myPageHelpers.add(myPageHelper1);
        return ResultVO.success(myPageHelpers);

    }

    @Override
    public ResultVO getRecommendPosts(String userId, int currentNum, int size) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        PageHelper.startPage(currentNum, size);
        List<Post> posts = postMapper.selectList(wrapper);
        PageInfo<Post> postPageInfo = new PageInfo<>(posts);
        MyPageHelper<PostDTO> myPageHelper = new MyPageHelper<>();
        BeanUtils.copyProperties(postPageInfo, myPageHelper);
        Set<PostDTO> followSet = new HashSet<>();
        Set<PostDTO> notFollowSet = new HashSet<>();
        Set<PostDTO> recommendSet = new HashSet<>();
        if (!"".equals(userId)) {
            List<Follow> allIdol = followService.getAllIdol(userId);
            Boolean isIdol = false;
            for (Post post : posts) {
                List<Boolean> trues = new ArrayList<>();
                List<Boolean> falses = new ArrayList<>();
                PostDTO postDTO = assemblePostDTO(post,userId);
                for (Follow idol : allIdol) {
                    if (idol.getIdolId().equals(post.getUserId())) {
                        trues.add(!isIdol);
                    }else {
                        falses.add(isIdol);
                    }
                }
                if (trues.size() > 0||post.getUserId().equals(userId)) {
                    postDTO.setIsFollow(true);
                    followSet.add(postDTO);
                }else {
                    postDTO.setIsFollow(false);
                    notFollowSet.add(postDTO);
                }

            }
        }else {
            for (Post post : posts) {
                PostDTO postDTO = assemblePostDTO(post,userId);
                postDTO.setIsFollow(false);
                notFollowSet.add(postDTO);
            }
        }
        recommendSet.addAll(followSet);
        recommendSet.addAll(notFollowSet);
        List<PostDTO> recommendList = new ArrayList<>(recommendSet);
        myPageHelper.setList(recommendList);
        myPageHelper.setId(1);
        List<MyPageHelper> myPageHelpers = new ArrayList<>();
        MyPageHelper<PostDTO> myPageHelper1 = new MyPageHelper<>();
        myPageHelper1.setId(0);
        myPageHelper1.setPageNum(0);
        myPageHelper1.setPageSize(0);
        myPageHelper1.setSize(0);
        myPageHelper1.setOrderBy("");
        myPageHelper1.setStartRow(0);
        myPageHelper1.setEndRow(0);
        myPageHelper1.setTotal(0L);
        myPageHelper1.setPages(0);
        myPageHelper1.setList(Lists.newArrayList());
        myPageHelper1.setFirstPage(0);
        myPageHelper1.setPrePage(0);
        myPageHelper1.setNextPage(0);
        myPageHelper1.setLastPage(0);
        myPageHelper1.setFaPage(false);
        myPageHelper1.setLaPage(false);
        myPageHelper1.setHasPreviousPage(false);
        myPageHelper1.setHasNextPage(false);
        myPageHelper1.setNavigatePages(0);
        myPageHelper1.setNavigatepageNums(null);
        myPageHelpers.add(myPageHelper1);
        myPageHelpers.add(myPageHelper);
        return ResultVO.success(myPageHelpers);
    }



    @Override
    public PostDTO selectByPostId(String postId) {
        Post post = postMapper.selectByPrimaryKey(postId);
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post,postDTO);
        User user = userMapper.selectById(post.getUserId());
        UserVO userVO= new UserVO();
        BeanUtils.copyProperties(user, userVO);
        ImgDTO imgDTO = ossService.queryByParentId(post.getUserId());
        String[] urls = imgDTO.getUrls();
        List<String> imgUrls = new ArrayList<>(Arrays.asList(urls));
        postDTO.setImgUrls(urls);
        postDTO.setUserVO(userVO);
        return postDTO;
    }

    @Override
    @Transactional(rollbackFor = TipsException.class)
    public ResultVO send(cn.seeumt.form.Post formPost) throws HttpException {
        String tagIds = formPost.getTagIds();
        String postId = UuidUtil.getUuid();
        List<String> tagIdList = Splitter.on(",").splitToList(tagIds);
        if (CollectionUtils.isEmpty(tagIdList)) {
            insertPost(formPost, postId);
        } else {
            insertPost(formPost,postId);
            mediaTagsService.insert(tagIdList,postId);
        }
        return ResultVO.success(postId);
    }

    public void insertPost(cn.seeumt.form.Post formPost,String postId) throws HttpException {
        String checkedContent = null;
        JSONObject jsonObject = WechatUtil.checkMsg(formPost.getContent());
        String errcode = jsonObject.getString("errcode");
        if (errcode.equals(Tips.RISK_CONTENT.getCode().toString())) {
            checkedContent = "*@#￥%&……*￥%%&*";
        }else {
            checkedContent = formPost.getContent();
        }
        Post post = new Post();
        post.setPostId(postId);
        post.setType(formPost.getType());
        post.setContent(checkedContent);
        post.setImgId("66");
        post.setUserId(formPost.getUserId());
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setDeleted(false);
        int insert = postMapper.insert(post);
        if (insert < 1) {
            throw new TipsException(TipsFlash.INSERT_POST_FAILED);
        }

    }

    @Override
    public Post selectByUserId(String userId) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return postMapper.selectOne(wrapper);

    }

    @Override
    public ResultVO delete(String postId) {
        int i = postMapper.deleteById(postId);
        if (i < 1) {
           throw new TipsException(TipsFlash.DELETED_FAILED);
        }
        return ResultVO.success(Tips.DELETED_SUCCESS);
    }

    @Override
    public ResultVO get(String postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return ResultVO.error("无结果");
        }
        return ResultVO.success(post);
    }

    @Override
    public ResultVO updateContent(String postId, String content) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return ResultVO.error("无结果");
        }
        if (content.equals(post.getContent())) {
            postMapper.updateById(post);
            return ResultVO.success("更新动态内容成功");
        }else {
            post.setContent(content);
            int i = postMapper.updateById(post);
            // TODO: 2020/2/16 更新同样的内容 影响行数
            if (i < 1) {
                throw new TipsException(TipsFlash.ADD_TO_ORDER_MASTER_FAILED);
            }
        }

        return ResultVO.success("更新动态内容成功！！！");
    }

    @Override
    public List<PostVO> search(String keywords) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        if ("".equals(keywords)) {
            wrapper.orderByDesc("create_time");
            List<Post> posts = postMapper.selectList(wrapper);
            return assemblePostVO(posts);
        }
        wrapper.orderByDesc("create_time").like("content", keywords);
        return assemblePostVO(postMapper.selectList(wrapper));
    }

    public PostDTO assemblePostDTO(Post post,String userId) {
        LoveVO loveVO = new LoveVO();
        if (userId != null) {
            Love love = loveService.selectByApiRootIdAndUserIdAndType(post.getPostId(), userId, (byte) 3);
            if (love == null) {
                loveVO.setType("");
            }else {
                loveVO.setType(love.getLoveType());
            }
        }
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO);
        User user = userMapper.selectById(post.getUserId());
        if (user == null) {
            throw new TipsException(456, "此动态用户不存在");
        }
        postDTO.setFaceIcon(user.getFaceIcon());
        postDTO.setUsername(user.getUsername());
        postDTO.setNickname(user.getNickname());
        ImgDTO imgDTO = ossService.queryByParentId(post.getPostId());
        if (imgDTO == null) {
            postDTO.setImgUrls(null);
        }else {
            String[] urls = imgDTO.getUrls();
            if ( urls.length==0) {
                postDTO.setImgUrls(null);
            }else {
                postDTO.setImgUrls(urls);
            }

        }
        postDTO.setImgUrls(imgDTO.getUrls());
        loveVO.setLikeCount(loveService.selectThumbCountByRootIdAndType(post.getPostId(), (byte) 3).size());
        loveVO.setHateCount(loveService.selectHateCountByRootIdAndType(post.getPostId(), (byte) 3).size());
        postDTO.setLove(loveVO);
        postDTO.setCommentCount(commentService.getAllCommentCount(post.getPostId())+commentService.selectCommentCountByRootIdAndType(post.getPostId(), (byte) 3).size());
        List<String> tagsIds = mediaTagsService.findTagIdsByParentId(post.getPostId());
        if (tagsIds.size() == 0) {
            postDTO.setTags(null);
        }else {
            postDTO.setTags(tagService.findTagVoByTagIds(tagsIds));
        }
        return postDTO;
    }

    public List<PostVO> assemblePostVO(List<Post> posts) {
            return  posts.stream().map(post -> {
            PostVO postVO = new PostVO();
            User user = userMapper.selectById(post.getUserId());
            if (user == null) {
                return null;
            }
            postVO.setUsername(user.getUsername());
            postVO.setFaceIcon(user.getFaceIcon());
            BeanUtils.copyProperties(post, postVO);
            postVO.setThumbCount(commentService.selectCommentCountByRootIdAndType(post.getPostId(), (byte) 3).size());
            ImgDTO imgDTO = ossService.queryByParentId(post.getPostId());
           if (imgDTO.getUrls().length == 0) {
                 postVO.setCover("http://seeumt.oss-cn-hangzhou.aliyuncs.com/5ebfed05dbd340a69cd288d75628986a.jpg");
           }
           else {
                String[] urls = imgDTO.getUrls();
                postVO.setCover(urls[0]);
            }
            return postVO;
        }).collect(Collectors.toList());

    }


}
