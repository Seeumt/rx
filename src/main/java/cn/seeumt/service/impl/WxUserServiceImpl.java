package cn.seeumt.service.impl;
import java.util.Date;

import cn.seeumt.dataobject.WxUser;
import cn.seeumt.dao.WxUserMapper;
import cn.seeumt.dto.MpWxUserInfoDTO;
import cn.seeumt.enums.Tips;
import cn.seeumt.form.MpWxUserInfo;
import cn.seeumt.service.WxUserService;
import cn.seeumt.utils.UuidUtil;
import cn.seeumt.vo.ResultVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Seeumt
 * @since 2020-01-10
 */
@Service
public class WxUserServiceImpl implements WxUserService {
    @Autowired
    private WxUserMapper wxUserMapper;
    @Override
    public WxUser insert(MpWxUserInfo mpwxUserInfo, String openId, String sessionKey, String skey) {
        WxUser wxUser = new WxUser();
        wxUser.setUserId(UuidUtil.getUuid());
        wxUser.setOpenId(openId);
        wxUser.setNickName(mpwxUserInfo.getNickName());
        wxUser.setGender(mpwxUserInfo.getGender());
        wxUser.setCity(mpwxUserInfo.getCity());
        wxUser.setCountry(mpwxUserInfo.getCountry());
        wxUser.setAvatarUrl(mpwxUserInfo.getAvatarUrl());
        wxUser.setLanguage(mpwxUserInfo.getLanguage());
        wxUser.setMobile("");
        wxUser.setTelephone(Tips.DEFAULT_TEL.getMsg());
        wxUser.setSessionKey(sessionKey);
//        设置会话skey
        wxUser.setSkey(skey);
        Date date = new Date();
        wxUser.setCreateTime(date);
        wxUser.setLastVisitTime(date);
        int insert = wxUserMapper.insert(wxUser);
        if (insert > 0) {
            return wxUser;
        }
        return wxUser;
    }

    @Override
    public WxUser selectByOpenId(String openId) {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        return wxUserMapper.selectOne(wrapper);
    }

    @Override
    public int update(WxUser wxUser) {
        return wxUserMapper.updateById(wxUser);
    }

    @Override
    public ResultVO modifyUserInfo(MpWxUserInfoDTO mpwxUserInfoDTO) {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", mpwxUserInfoDTO.getUserId());
        WxUser wxUser = wxUserMapper.selectOne(wrapper);
        BeanUtils.copyProperties(mpwxUserInfoDTO, wxUser);
        wxUserMapper.updateById(wxUser);
        return ResultVO.success(mpwxUserInfoDTO,"更新用户信息成功");
    }

}
