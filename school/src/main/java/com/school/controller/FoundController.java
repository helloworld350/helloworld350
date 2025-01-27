package com.school.controller;

import com.school.mapper.LostFoundMapper;
import com.school.entity.Found;
import com.school.entity.Lostfoundtype;
import com.school.services.interfaces.FoundDetail;
import com.school.services.interfaces.LostFoundType;
import com.school.utils.ServerResponse;
import com.school.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FoundController {
    @Autowired
    private FoundDetail foundDetail;
    @Autowired
    private LostFoundMapper lostFoundMapper;
    @Autowired
    private LostFoundType lostFoundType;

    @ResponseBody
    @RequestMapping("/getAllTypeByFound")
    public ServerResponse getAllType() {
        return lostFoundType.getAllType();
    }

    @ResponseBody
    //获取分类下的内容
    @RequestMapping("/DetailByTitleByFound")
    public ServerResponse getDetailByTitle(String title) {
        //获取标题id
        int id = 0;
        List<Lostfoundtype> lostfoundtypes = lostFoundMapper.GetAll();
        //中英文转换
        String[] en = {"Digital Devices", "Certificates", "Daily Necessities", "Clothing and Apparel", "Other"};
        String[] cn = {"数码设备", "证件", "日用品", "服饰", "其他"};
        Map<String, String> map = new HashMap<>();//建立关系
        for (int i = 0; i < en.length; i++) {
            map.put(en[i], cn[i]);
        }
        for (Lostfoundtype lostfoundtype : lostfoundtypes) {
            if (lostfoundtype.getName().equals(title)) {
                //中文
                id = lostfoundtype.getId();
            } else {
                //英文
                String cnValue = map.get(title);
                if (lostfoundtype.getName().equals(cnValue)) {
                    id = lostfoundtype.getId();
                }
            }
        }
        ServerResponse foundDetailList = foundDetail.getFoundDetailList(id);
        //更新图片地址
        List<Found> data = (List<Found>) foundDetailList.getData();
        String updatePic = "";
        for (Found found : data) {
            if (found.getImg() == null) {
                //如果没有图片地址则退出，防止空指针异常
                break;
            } else {
                updatePic = Util.updatePic(found.getImg());
                found.setImg(updatePic);
            }
        }
        //更新数据
        foundDetailList.setData(data);
        return foundDetailList;
    }

    @ResponseBody
    @RequestMapping("/getUnameByFound")
    public ServerResponse getUname(int id) {
        return foundDetail.getUserName(id);
    }

    //获取发布信息
    @ResponseBody
    @RequestMapping("/getAllFoundUserId")
    public ServerResponse getAllFoundUserId(int user_id) {
        return foundDetail.getAllByIdFoundList(user_id);
    }

    //更改状态
    @ResponseBody
    @RequestMapping("/updateFoundState")
    public ServerResponse updateState(int id, String state, int user_id) {
        return foundDetail.updateState(id, state, user_id);
    }

    //置顶信息
    @ResponseBody
    @RequestMapping("/showFoundList")
    public ServerResponse showLostList(int stick) {
        return foundDetail.showFoundList(stick);
    }
}
