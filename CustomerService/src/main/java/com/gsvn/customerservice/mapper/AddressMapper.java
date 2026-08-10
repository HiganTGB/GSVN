package com.gsvn.customerservice.mapper;


import com.gsvn.customerservice.model.entity.AddressCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AddressMapper {

    int insertAddress(AddressCard address);


    int updateAddress(AddressCard address);

    List<AddressCard> getAddressesByCustomerId(@Param("customerId") Long customerId);

    AddressCard getAddressById(@Param("addressId") Integer addressId);

    int deleteAddress(@Param("addressId") Integer addressId);

    AddressCard findDefaultAddressByCustomerId(@Param("customerId") Long customerId);
}