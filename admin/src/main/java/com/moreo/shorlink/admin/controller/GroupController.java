package com.moreo.shorlink.admin.controller;

import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.common.convention.result.Results;
import com.moreo.shorlink.admin.dto.req.GroupReqDTO;
import com.moreo.shorlink.admin.dto.req.GroupUpdateDTO;
import com.moreo.shorlink.admin.dto.resp.GroupRespDTO;
import com.moreo.shorlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/api/shortlink/admin/v1/group/save")
    public Result<Void> save(@RequestBody GroupReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    @GetMapping("/api/shortlink/admin/v1/group/list")
    public Result<List<GroupRespDTO>> listGroup() {
        List<GroupRespDTO> result = groupService.listGroup();
        return Results.success(result);
    }

    @PutMapping("/api/shortlink/admin/v1/group/update")
    public Result<Void> updateGroup(@RequestBody GroupUpdateDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }
}
