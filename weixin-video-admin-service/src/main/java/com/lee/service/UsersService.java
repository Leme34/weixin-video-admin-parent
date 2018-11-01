package com.lee.service;


import com.lee.VO.PagedResult;
import com.lee.pojo.Users;

public interface UsersService {

	PagedResult queryUsers(Users user, Integer page, Integer pageSize);
	
}
