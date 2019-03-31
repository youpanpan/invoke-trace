package com.chengxuunion.invoketrace.business.tracemethod.api;

import com.chengxuunion.invoketrace.business.tracemethod.model.request.TraceMethodPageParam;
import com.chengxuunion.invoketrace.business.tracemethod.service.TraceMethodService;
import com.chengxuunion.invoketrace.common.model.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 方法跟踪查看控制器
 *
 * @author youpanpan
 * @date: 2019-03-15 17:06
 * @since v1.0
 */
@Controller
@RequestMapping("/invoke/method")
public class TraceMethodController {

    @Autowired
    private TraceMethodService traceMethodService;

    @GetMapping
    public String index(Model model) {
        TraceMethodPageParam traceMethodPageParam = new TraceMethodPageParam();
        traceMethodPageParam.setPageNum(1);
        traceMethodPageParam.setPageSize(Integer.MAX_VALUE);
        model.addAttribute("pageResult", traceMethodService.listTraceMethod(traceMethodPageParam));
        return "invoketrace/index";
    }

    @GetMapping("/list-page")
    @ResponseBody
    public ResultCode listTraceMethodPage(TraceMethodPageParam traceMethodPageParam) {
        return ResultCode.success(traceMethodService.listTraceMethod(traceMethodPageParam));
    }

    @GetMapping("/get")
    @ResponseBody
    public ResultCode getTraceMethod(@RequestParam("fullName") String fullName) {
        return ResultCode.success(traceMethodService.getTraceMethod(fullName));
    }

    @GetMapping("/statistics")
    @ResponseBody
    public ResultCode getTraceMethodStatistics(@RequestParam("fullName") String fullName, @RequestParam("number") Integer number) {
        return ResultCode.success(traceMethodService.getTraceMethodStatistics(fullName, number));
    }

}
