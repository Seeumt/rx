package cn.seeumt.service.impl;
import java.time.LocalDateTime;

import cn.seeumt.dataobject.Collect;
import cn.seeumt.dao.CollectMapper;
import cn.seeumt.dataobject.Love;
import cn.seeumt.enums.Tips;
import cn.seeumt.enums.TipsFlash;
import cn.seeumt.exception.TipsException;
import cn.seeumt.service.CollectService;
import cn.seeumt.utils.UuidUtil;
import cn.seeumt.vo.ResultVO;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Seeumt
 * @since 2020-02-16
 */
@Service
@Slf4j
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
    @Autowired
    private CollectMapper collectMapper;

    @Override
    public ResultVO addOrCancelCollect(String apiRootId, String userId) {
        Collect aCollect = selectByApiRootIdAndUserId(apiRootId, userId);
        if (aCollect==null) {
            Collect collect = new Collect();
            collect.setCollectId(UuidUtil.getUuid());
            collect.setType(0);
            collect.setStatus(true);
            collect.setCreateTime(new Date());
            collect.setUpdateTime(new Date());
            collect.setEnabled(false);
            collect.setUserId(userId);
            collect.setApiRootId(apiRootId);
            int insert = collectMapper.insert(collect);
            if (insert < 1) {
                throw new TipsException(TipsFlash.TH_FAILED);
            }
            log.info("【收藏】用户 {}收藏 {}",userId,apiRootId);
            return ResultVO.success("收藏成功啦！");
        }
        //如果已经收藏，再点就是取消
        else if (aCollect.getStatus()) {
            aCollect.setStatus(false);
            aCollect.setUpdateTime(new Date());
            int i = collectMapper.updateById(aCollect);
            if (i == 1) {
                log.info("【取消收藏】用户{}取消收藏{}",userId,apiRootId);
                return ResultVO.success("取消收藏啦！");
            } else {
                throw new TipsException(TipsFlash.COLLECT_FAILED);
            }
        } else if (!aCollect.getStatus()) {
            aCollect.setStatus(true);
            aCollect.setUpdateTime(new Date());
            int i = collectMapper.updateById(aCollect);
            if (i == 1) {
                log.info("【再次收藏】用户 {}再次收藏 {}",userId,apiRootId);
                return ResultVO.success("收藏成功啦！");
            } else {
                throw new TipsException(TipsFlash.COLLECT_FAILED);
            }

        }
        throw new TipsException(TipsFlash.COLLECT_FAILED);
    }



    @Override
    public Collect selectByApiRootIdAndUserId(String apiRootId, String userId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("api_root_id", apiRootId).eq("user_id", userId).eq("status",true);
        return collectMapper.selectOne(queryWrapper);
    }
}
