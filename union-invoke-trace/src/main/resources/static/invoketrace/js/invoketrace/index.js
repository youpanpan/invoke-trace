$(document).ready(function() {
    layui.use(['form', 'table', 'layer','element'], function () {
        var element = layui.element;
        var layerIndex = -1;
        var table = layui.table;

        // 节点基础配置
        var exceptionColor = "#d9534f";

        /**
         * 折叠面板操作
         * */
        element.on('collapse(collapse-filter)', function(data){
            var contentObj = data.content;
            if (data.show && !$(contentObj).hasClass("has-show")) {
                layerIndex = layer.load(2);
                $(contentObj).addClass("has-show");

                var fullName = $(contentObj).attr("data");
                loadTraceMethod(fullName, contentObj);
            }
        });

        /**
         * 加载跟踪方法
         * */
        function loadTraceMethod(fullName, contentObj) {
            $.ajax({
                url: '/invoke/method/get?fullName=' + fullName,
                type: 'get',
                dataType: 'json',
                success: function(data) {
                    layer.close(layerIndex);
                    if (data.success) {
                        var traceMethod = data.data;
                        renderTraceMethod(traceMethod, contentObj);
                    } else {
                        layer.msg("加载数据出现异常", {icon: 5});
                    }
                },
                error: function() {
                    layer.close(layerIndex);
                    layer.alert("网络超时，请稍候重试!", {icon: 5});
                }
            });
        }

        var baseTree = [];
        /**
         * 渲染跟踪方法
         * */
        function renderTraceMethod(traceMethod, contentObj) {
            baseTree = [
                {
                    text: traceMethod.fullName,
                    color: (traceMethod.endTimestamp - traceMethod.startTimestamp) > 1000 ? exceptionColor : '',
                    state: {
                        expanded: true
                    },
                    tags: [(traceMethod.endTimestamp - traceMethod.startTimestamp)  + ' 毫秒'],
                    nodes: [

                    ]
                }
            ];
            initNode(baseTree[0], traceMethod.traceMethodList);
            // 渲染调用层次结构
            $(contentObj).find("#method_tree").treeview({data: baseTree, showTags: true});

            // 渲染方法详情表格
            renderMethodTable(traceMethod, contentObj);

            // 渲染方法调用时间统计
            loadTraceMethodTime(traceMethod, contentObj);
        }

        /**
         * 渲染方法详情表格
         * */
        function renderMethodTable(traceMethod, contentObj) {
            var traceMethodArr = getTraceMethodArr(traceMethod);

            //执行渲染
            table.render({
                elem: $(contentObj).find("#method_table") //指定原始表格元素选择器（推荐id选择器）
                ,height: traceMethodArr.length * (traceMethodArr.length > 2 ? 80 : 90) + 50 //容器高度
                ,cols: [[
                    {
                        field: 'returnType',
                        title: '方法返回',
                        width: '250'
                    },
                    {
                        field: 'modifier',
                        title: '方法修饰符',
                        width: '120'
                    },
                    {
                        field: 'fullName',
                        title: '方法名',
                        width: '250'
                    },
                    {
                        field: 'requestUrl',
                        title: '请求URL',
                        width: '150'
                    },
                    {
                        field: 'superClassName',
                        title: '父类',
                        width: '220'
                    },
                    {
                        field: 'interfaces',
                        title: '接口',
                        width: '220'
                    },
                ]] //设置表头
                ,
                data: traceMethodArr
                ,page: false
            });
        }

        /**
         * 递归获取方法及调用的方法列表
         * */
        function getTraceMethodArr(traceMethod) {
            var arr = [];
            arr.push(traceMethod);
            if (traceMethod.traceMethodList == null || traceMethod.traceMethodList == undefined || traceMethod.traceMethodList.length == 0) {
                return arr;
            }

            for (var i = 0; i < traceMethod.traceMethodList.length; i++) {
                arr = arr.concat(getTraceMethodArr(traceMethod.traceMethodList[i]));
            }

            return arr;
        }

        /**
         * 递归初始化节点信息
         * */
        function initNode(node, traceMethodList) {
            if (traceMethodList == null || traceMethodList == undefined || traceMethodList.length == 0) {
                return;
            }

            var nodes = [];
            for (var i = 0; i < traceMethodList.length; i++) {
                nodes[i] = {};
                nodes[i]['text'] = traceMethodList[i].fullName;
                if ((traceMethodList[i].endTimestamp - traceMethodList[i].startTimestamp) > 1000) {
                    nodes[i]['color'] = exceptionColor;
                }
                nodes[i]['state'] = {expanded: true};
                nodes[i]['tags'] = [(traceMethodList[i].endTimestamp - traceMethodList[i].startTimestamp) + ' 毫秒'];
                nodes[i]['data'] = traceMethodList[i];
                initNode(nodes[i], traceMethodList[i].traceMethodList);
            }
            node.nodes = nodes;
        }

        // 数组每一项为一个对象{chart: echart对象, id: 元素ID, option: echart option配置项}
        var changeCharts = [];
        var changeIndex = 0;
        /*----------------------方法调用时间统计--------------------*/
        var traceMethodTimeOption = {
            color: ['#3398DB'],
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                top: '6%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : ['12-20', '12-21', '12-22', '12-23', '12-24', '12-25', '12-26'],
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    minInterval: 1
                }
            ],
            series : [
                {
                    name:'方法所用时间',
                    type:'bar',
                    barWidth: '60%',
                    data:[10, 52, 200, 334, 390, 330, 220]
                }
            ]
        };

        /**
         * 加载方法调用所用时间统计
         * */
        function loadTraceMethodTime(traceMethod, contentObj) {
            $(contentObj).find('#method_statistics_chart').css("width", "1220px");
            var index = changeIndex;
            var traceMethodTimeChart = echarts.init($(contentObj).find('#method_statistics_chart').get(0), 'light');
            traceMethodTimeChart.setOption(traceMethodTimeOption);
            changeCharts[changeIndex++] = {chart: traceMethodTimeChart, option: traceMethodTimeOption};
            $.ajax({
                url: "/invoke/method/statistics?fullName=" + traceMethod.fullName + "&number=20",
                type: "GET",
                dataType: "json",
                success: function(data) {
                    if (data.success) {
                        renderBarChart(data.data, index);
                    } else {
                        layer.msg("加载方法调用所用时间统计数据发生异常", {icon: 5});
                    }
                },
                error: function() {
                    parent.layer.alert("网络超时", {icon: 5});
                }
            });
        }
        /*----------------------/方法调用时间统计--------------------*/

        /**
         * 渲染柱状图
         * @param   data    统计数据{xAxis:[], yAxis:[]}
         * @param   chartIndex  图表在数组中的索引
         * */
        function renderBarChart(data, chartIndex) {
            changeCharts[chartIndex].option.xAxis[0].data = data.xAxis;
            changeCharts[chartIndex].option.series[0].data = data.yAxis;
            changeCharts[chartIndex].chart.setOption(changeCharts[chartIndex].option);
            console.log(data);
            console.log(changeCharts[chartIndex].chart);
        }

    })
});