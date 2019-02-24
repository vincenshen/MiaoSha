package com.vmware.miaosha.dao;

import com.vmware.miaosha.dataobject.OrderInfoDO;
import org.springframework.stereotype.Component;

@Component
public interface OrderInfoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    int insert(OrderInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    int insertSelective(OrderInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    OrderInfoDO selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    int updateByPrimaryKeySelective(OrderInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_info
     *
     * @mbg.generated Fri Feb 22 18:12:12 CST 2019
     */
    int updateByPrimaryKey(OrderInfoDO record);
}