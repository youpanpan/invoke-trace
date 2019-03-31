package com.chengxuunion.test.demo.controller;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.business.tracemethod.model.request.TraceMethodPageParam;
import com.chengxuunion.invoketrace.business.tracemethod.service.TraceMethodService;
import com.chengxuunion.invoketrace.common.model.PageResult;
import com.chengxuunion.invoketrace.common.model.ResultCode;
import com.chengxuunion.test.demo.service.DemoService;
import com.chengxuunion.invoketrace.storage.MemoryStorageContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * @author youpanpan
 * @date: 2019-03-13 15:17
 * @since v1.0
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

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

    @GetMapping("/list")
    @ResponseBody
    public Object findAll() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return demoService.findAll("123", 12);
    }

    @GetMapping("/invoke-trace")
    @ResponseBody
    public Object findInvokeTrace() {
        return MemoryStorageContainer.getInstance().getTraceMethodMap();
    }

}
