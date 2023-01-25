package github.oineh.monitoring.dept.web;

import github.oineh.monitoring.dept.service.DeptService;
import github.oineh.monitoring.dept.web.req.DeptAddReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class ApiDeptController {

    private final DeptService deptService;


    @PostMapping
    public void createGroupDept(@RequestBody DeptAddReq req, Principal principal) {
        deptService.makeDept(req, principal.getName());
    }
}