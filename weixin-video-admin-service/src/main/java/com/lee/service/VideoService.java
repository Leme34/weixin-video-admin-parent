package com.lee.service;

import com.lee.VO.PagedResult;
import com.lee.pojo.Bgm;

public interface VideoService {

	void addBgm(Bgm bgm);
	
	PagedResult queryBgmList(Integer page, Integer pageSize);

	void deleteBgm(String id);

	PagedResult queryReportList(Integer page, Integer pageSize);

	void updateVideoStatus(String videoId, Integer status);
}
