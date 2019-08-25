package com.hualala.cos;

import com.hualala.common.ResultCode;
import com.hualala.common.BusinessException;
import com.hualala.wechat.WXService;
import com.hualala.util.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author YuanChong
 * @create 2019-08-24 20:04
 * @desc
 */
@Controller
@RequestMapping("/cos")
public class CosController {

    @Autowired
    private WXService wxService;

    /**
     * 上传图片到COS服务器
     * @param mediaID
     * @return 完整的图片路径
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/upload")
    public Object upload(String mediaID) throws IOException {
        if(StringUtils.isEmpty(mediaID)) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(),"mediaID必传");
        }
        //上传图片
        byte[] images = wxService.downloadMedia(mediaID);
        String url = MediaUtils.uploadImage(images);
        return ResultUtils.success(url);
    }


}
