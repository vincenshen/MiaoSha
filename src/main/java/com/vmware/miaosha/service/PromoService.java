package com.vmware.miaosha.service;


import com.vmware.miaosha.service.model.PromoModel;

public interface PromoService {

    // 根据ItemID获取即将进行的或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
