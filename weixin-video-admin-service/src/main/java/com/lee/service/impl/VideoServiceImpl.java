package com.lee.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.lee.VO.PagedResult;
import com.lee.enums.BGMOperatorTypeEnum;
import com.lee.mapper.BgmMapper;
import com.lee.mapper.UsersReportMapperCustom;
import com.lee.mapper.VideosMapper;
import com.lee.pojo.Bgm;
import com.lee.pojo.BgmExample;
import com.lee.pojo.Videos;
import com.lee.pojo.vo.Reports;
import com.lee.service.VideoService;
import com.lee.utils.ZKCurator;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videosMapper;
	@Autowired
	private BgmMapper bgmMapper;
	@Autowired
	private Sid sid;
	@Autowired
	private ZKCurator zkCurator;
	@Autowired
	private UsersReportMapperCustom usersReportMapperCustom;


	@Override
	public void addBgm(Bgm bgm) {
		String bgmId = sid.nextShort();
		bgm.setId(bgmId);
		bgmMapper.insert(bgm);
		Map<String, String> map = new HashMap<>();
		map.put("operType", BGMOperatorTypeEnum.ADD.type);
		map.put("path", bgm.getPath());
		//类似与MQ,在zk创建此bgm的节点,内容为(枚举类型的操作值,数据库中bgm的本地相对路径)
		Gson gson = new Gson();
		zkCurator.sendBgmOperator(bgmId,gson.toJson(map));
	}


	@Override
	public PagedResult queryBgmList(Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);

		BgmExample example = new BgmExample();
		List<Bgm> list = bgmMapper.selectByExample(example);

		PageInfo<Bgm> pageList = new PageInfo<>(list);

		PagedResult result = new PagedResult();
		result.setTotal(pageList.getPages());
		result.setRows(list);
		result.setPage(page);
		result.setRecords(pageList.getTotal());

		return result;
	}

	@Override
	public void deleteBgm(String id) {
		bgmMapper.deleteByPrimaryKey(id);
		//类似于MQ,在zk创建此bgm的节点,内容为(枚举类型的操作值,数据库中bgm的本地相对路径)
		zkCurator.sendBgmOperator(id, BGMOperatorTypeEnum.DELETE.type);
	}

	@Override
	public PagedResult queryReportList(Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);

		List<Reports> reportsList = usersReportMapperCustom.selectAllVideoReport();

		PageInfo<Reports> pageList = new PageInfo<Reports>(reportsList);

		PagedResult grid = new PagedResult();
		grid.setTotal(pageList.getPages());
		grid.setRows(reportsList);
		grid.setPage(page);
		grid.setRecords(pageList.getTotal());

		return grid;
	}

	@Override
	public void updateVideoStatus(String videoId, Integer status) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setStatus(status);
		videosMapper.updateByPrimaryKeySelective(video);
	}
}
