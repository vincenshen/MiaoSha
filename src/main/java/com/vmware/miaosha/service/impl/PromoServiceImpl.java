package com.vmware.miaosha.service.impl;

import com.vmware.miaosha.dao.PromoDOMapper;
import com.vmware.miaosha.dataobject.PromoDO;
import com.vmware.miaosha.service.PromoService;
import com.vmware.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {

        // 获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        // DataObject -> Model
        PromoModel promoModel = convertModelFromDataObject(promoDO);
        if(promoModel == null){
            return null;
        }

        // 判断当前时间是否秒杀活动即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);    // 还未开始
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);    // 已结束
        }else {
            promoModel.setStatus(2);    // 进行中
        }
        return promoModel;
    }

    private PromoModel convertModelFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }

        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
