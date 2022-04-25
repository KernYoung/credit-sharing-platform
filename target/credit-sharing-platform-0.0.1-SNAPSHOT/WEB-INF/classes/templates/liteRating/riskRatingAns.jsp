    <div id="page22" class="row col-sm-12 title_style_3" style="margin-top: 30px">
    二.信用评价分析
</div>

<div class="row col-sm-12" style="margin-top: 38px">
    <div class="col-sm-6">
        <div class="risk-rating-card risk-rating-card-left">
            <span style="width: 200px;display: inline-block">
                <div class="risk-rating-card-text">量化模型信用等级</div>
                <div class="risk-rating-card-value">${base.Rank}</div>
            </span>
            <span style="width: 200px;display: inline-block">
                <div class="risk-rating-card-text">量化模型信用分数</div>
                <div class="risk-rating-card-value" id="initialScore">  ${(base.score?number)?string('0.00')}</div>
            </span>
        </div>
    </div>
    <div class="col-sm-6">
        <div class="risk-rating-card risk-rating-card-right">
            <span style="width: 200px;display: inline-block">
                <div class="risk-rating-card-text">调整后信用等级</div>
                <div class="risk-rating-card-value">
                    <#if base.finalRank??>
                        ${base.finalRank}
                    </#if>
        </div>
        </span>
        <span style="width: 200px;display: inline-block">
                <div class="risk-rating-card-text">调整后信用分数</div>
                <div class="risk-rating-card-value" id="ad_score">
                    <#if base.finalScore??>
                        ${(base.finalScore?number)?string('0.00')}
                    </#if>
    </div>
    </span>
</div>

</div>
</div>
<div class="col-sm-12" style="margin-top: 20px">


</div>


<div class="row col-sm-12 " style="margin-top: 30px;">
    <h3>定性指标</h3>
</div>
<h3></h3>


<div class="row col-sm-12" style="margin-top: 20px;padding-left: 30px;padding-right: 30px;">
    <div class="dataTables_wrapper no-footer">
        <table id="rating_Table" class="table table-hover dataTable no-footer" role="grid"
               aria-describedby="rating_Table_info" style="width: 100%;">

            <thead>
            <tr role="row">
                <th>
                    定性指标名称
                </th>
                <th>
                    定性指标详情
                </th>
                <th>
                    指标得分
                </th>
                <th>
                    指标得分区间
                </th>
            </tr>
            </thead>
            <tbody>
            <#if modelAns.noFinResult?? && modelAns.noFinResult??>
            <#list modelAns.noFinResult as index>
            <tr>
                <td class="tb_td_style">
                    ${index.name!"-"}
                </td>
                <td class="tb_td_style">
                    ${index.value!"-"}
                </td>
                <td class="tb_td_style">
                    ${index.score?string('0.00')!"-"}
                </td>
                <td>
                    ${index.range!"-"}
                </td>
            </tr>
            </#list>
            <#else>
            <tr>
                <td class="center" colspan="4">无
                </td>
            </tr>
        </#if>
        </tbody>
        </table>

    </div>
</div>
<div class="row col-sm-12" style="padding-right: 30px;padding-right: 30px;margin-top:40px">
    <div><strong>具体分析</strong></div>
    <div>
        <div>
            1.公司所处一级行业为${base.行业名称}，根据智象风控对行业信用风险的统计分析，给予该行业${base.行业名称}${base.finalRank}分的信用得分，在智象风控所有18个一级行业中，得分由高到低排名第
            <#if riskAd.R2??>
            ${riskAd["R2"]}
            <#else>
            -
        </#if>，
    </div>
    <div>
        2.公司的企业性质为${base.nature}，根据智象风控对企业性质信用风险的统计分析，给予${base.nature}${riskAd["R6"]!"-"}的信用得分，在智象风控所有5种企业组织形式中，
        得分由高到低排名第
        <#if riskAd.R5??>
        ${riskAd["R5"]}
        <#else>
        -
    </#if>；
</div>

<#if riskAd.isIndustryLeader??>
<#if riskAd.isIndustryLeader=="行业头部企业">
<div>
    3.企业是${base.行业名称}行业头部企业，在产业链上地位较高，议价能力较强，获得金融机构融资能力较强，应对信用风险的能力较强。
</div>
<#else>
<div>3.企业不是行业头部企业，应对信用风险的能力一般，调动内外部资源应对自身信用风险的能力一般。</div>
</#if>
</#if>



</div>
</div>
<!--分页截断-->
<div style="page-break-inside: avoid">
    <div class="row col-sm-12 " style="margin-top: 30px;">
        <h3>定量分析</h3>
    </div>

    <div class="row col-sm-12"style="margin-top: 20px;padding-left: 30px;padding-right: 30px;">


        <div class="dataTables_wrapper no-footer">
            <table class="table table-striped table-bordered table-hover dataTable no-footer">

                <thead>
                <tr role="row">
                    <th>定性指标名称
                    </th>
                    <th>最新指标值</th>
                    <th aria-label="状态">指标得分
                    </th>
                    <th>
                        区间一/得分
                    </th>
                    <th>
                        区间二/得分
                    </th>
                    <th>
                        区间三/得分
                    </th>
                    <th>
                        区间四/得分
                    </th>
                    <th>
                        区间五/得分
                    </th>
                </tr>
                </thead>
                <tbody>
                        <#if modelAns.finResult?? && modelAns.finResult??>
                        <#list modelAns.finResult as index>
                        <tr>
                            <td>
                                ${index.name!"-"}
                            </td>
                            <td>
                            <#if index.value?string=="MAX">
                                max
                                <#elseif index.value?string=="MIN">
                                min
                                <#else>
                                ${(index.value?number)?string("0.00%;;roundingMode=halfUp")}
                            </#if>

                            </td>
                            <td>
                                ${index.score?string('0.00')!"-"}
                            </td>

                            <#if index.range??>
                            <#list index.range as rangeItem>
                            <#if rangeItem.selected?? && rangeItem.selected=="selected">
                            <td style="color: #5AC8FA">
                                ${rangeItem.range?replace("max","&infin;")?replace("min","-&infin;")}
                                <br>
                                得分：${(rangeItem.score?number)?string("0.00;;roundingMode=halfUp")}
                            </td>
                            <#else>
                            <td>
                                ${rangeItem.range?replace("max","&infin;")?replace("min","-&infin;")}
                                <br>
                                得分：${(rangeItem.score?number)?string("0.00;;roundingMode=halfUp")}
                            </td>
                        </#if>

                        </#list>
                        <#if index.range?size lt 5>

                        <#assign numA=5-index.range?size />


                        <#list 1..numA as abc>
                        <td> - </td>
                        </#list>
                        </#if>

                        <#else>
                        <td>
                            -
                        </td>
                        </#if>
                        </tr>
                        </#list>
                        <#else>
                            <tr>
                                <td class="center" colspan="9">无
                                  </td>
                             </tr>
                         </#if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div style="margin-top: 20px">
    <!-- 历年指标数据   -->
    <#if indicatorsHistoryData??>
    <#list indicatorsHistoryData ?keys as name>
    <#assign item=indicatorsHistoryData[name]>
    <div class="row">
        （${name_index+1}）${name}
    </div>
    <div class="col-sm-12" style="margin-top: 10px">
        <table class="table table-striped table-bordered table-hover  dataTable no-footer">

            <thead>
            <tr>
                <th class="width-20">${name}</th>
                <#list indicatorsHistoryData[name] as index>
                <th style="width: auto" class="center">
                    ${index.year}
                </th>
            </#list>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>指标值</td>
                <#list indicatorsHistoryData[name] as index>
                <td style="width: auto" class="center">
                    <#if  index.value?string=="" ||index.value?string=="-"   >
                        -
                    <#elseif index.value?string=="MAX">
                        max
                    <#elseif index.value?string=="MIN">
                        min
                    <#else>
                        ${index.value?number?string("0.00%;;roundingMode=halfUp")}
                    </#if>
                </td>
            </#list>
            </tr>
            <tr>
                <td>得分</td>
                <#list indicatorsHistoryData[name] as index>
                <td style="width: auto" class="center">
                    <#if index.value?string=="" ||index.value?string=="-"   >

                    -
                    <#else>
                        ${index.score?string("0.00;;roundingMode=halfUp")}
                    </#if>

                </td>
            </#list>
            </tr>
            </tbody>

        </table>
    </div>


    <div class="col-sm-12 " style=" width: 100%;">
        <!--    折线图            -->
        <div id="lineEchart${name_index}" style="height: 300px;width: 600px;display: block;
                            margin-left: auto;
                            margin-right: auto;page-break-inside: avoid" data-value="<#assign text>
                        item
                     </#assign>
                    <#assign json=text?eval />
                        <#list json as index>
                         {'name':'${index.name}', 'score':'${index.score}','value':'

                          <#if index.value?string=="" ||index.value?string=="-"   >
                            -
                         <#elseif index.value?string=="MAX">
                            max
                        <#elseif index.value?string=="MIN">
                            min
                        <#else>
                            ${index.value?number?string("0.00##;;roundingMode=halfUp")}
                        </#if>


                    ','year':'${index.year}'} , </#list>">
</div>
<script>
    var id = "lineEchart${name_index}";
    var reg = /,$/gi;
    var list = $("#" + id).attr("data-value").replace(/\s+/g, "").replace(/'/g, '"');
    list = "[" + ((list.substring(list.length - 1) == ',') ? list.substring(0, list.length - 1) : list) + "]";

    drawChart(list);
    function drawChart(list) {
        // x轴定义
        list = JSON.parse(list);
        list.sort(function (a, b) {
            return a.year - b.year
        });
        var xAxisData = [];
        // 数据整理
        var seriesValue = [];
        var seriesScore = [];
        for (var i = 0; i < list.length; i++) {
            // x轴
            if (i == 0) {
                //echart两边空出一列
                xAxisData.push("");
                seriesValue.push("");
                seriesScore.push("")
            }
            xAxisData.push(list[i].year);
            // 数据项
            
            seriesValue.push(list[i].value);
            seriesScore.push(list[i].score);
            if (i == list.length - 1) {
                //echart两边空出一列
                xAxisData.push("");
                seriesValue.push("");
                seriesScore.push("")
            }
        }

        var option = {
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['指标值', '得分']
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: xAxisData
            },
            yAxis: [{
                type: "value"
            }, {
                type: "value",
                axisLabel: {
                    interval: 0
                },
                splitLine: {
                    show: false,

                }
            }]
            ,

            series: [
                {
                    yAxisIndex: 0,
                    name: '指标值',
                    type: 'line',
                    data: seriesValue
                },
                {
                    name: '得分',
                    yAxisIndex: 1,
                    type: 'line',
                    data: seriesScore
                }
            ]
        };
        var myChart = echarts.init(document.getElementById("lineEchart" + ${ name_index }));

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);

    }
</script>
</div>
<!--   指标释义         -->


<div class="col-sm-12" style="margin-bottom: 20px">
    <strong>指标释义</strong>：${indicatorsHistoryAns["${name}"].Y3?string}<br>
    <strong>分析结论：</strong><br>
    ${indicatorsHistoryAns["${name}"].R1}年${name}指标值为
    <#if indicatorsHistoryAns["${name}"].R2?string=="">
       -
 
    <#elseif indicatorsHistoryAns["${name}"].R2?string=="MAX">
    max
    <#elseif indicatorsHistoryAns["${name}"].R2?string=="MIN">
    min
    <#else>
  
    ${((indicatorsHistoryAns["${name}"].R2?number))?string('0.00%')!"-"}
  
</#if>

，处于${indicatorsHistoryAns["${name}"].R3}风险区间，得分为
<#if indicatorsHistoryAns["${name}"].R4?string=="">
-

<#elseif indicatorsHistoryAns["${name}"].R4?string=="MAX">
max
<#elseif indicatorsHistoryAns["${name}"].R4?string=="MIN">
min
<#else>

${((indicatorsHistoryAns["${name}"].R4?number))?string('0.00')!"-"}

</#if>


分；${indicatorsHistoryAns["${name}"].R5}年该指标值为
<#if indicatorsHistoryAns["${name}"].R8?string=="">
-

<#elseif indicatorsHistoryAns["${name}"].R8?string=="MAX">
max
<#elseif indicatorsHistoryAns["${name}"].R8?string=="MIN">
min
<#else>

${((indicatorsHistoryAns["${name}"].R8?number))?string('0.00%')!"-"}

</#if>

，
处于${indicatorsHistoryAns["${name}"].R6}风险区间，得分为
<#if indicatorsHistoryAns["${name}"].R7?string=="">
-

<#elseif indicatorsHistoryAns["${name}"].R7?string=="MAX">
max
<#elseif indicatorsHistoryAns["${name}"].R7?string=="MIN">
min
<#else>


${((indicatorsHistoryAns["${name}"].R7?number))?string('0.00')!"-"}

</#if>
分.





</div>
</#list>
<#else>
<div>无</div>
</#if>

</div>
</div>

<div class="row col-sm-12 " style="margin-top: 20px;">
    <h3>非量化风险项</h3>
</div>

<div class="row col-sm-12" style="margin-top: 20px;padding-left: 30px;padding-right: 30px;">
    <table class="table table-striped table-bordered  " role="grid"
           aria-describedby="rating_Table_info" style="width: 100%;">

        <thead>
        <tr role="row">
            <th aria-label="操作">非量化风险项
            </th>
            <th aria-label="主体名称">非量化风险项详情
            </th>
            <th>
                是否忽略
            </th>
            <th style="width: 10%" aria-label="状态">影响
            </th>
        </tr>
        </thead>
        <tbody id="creditInfo" >
        <#if modelAns.ad_data?? && modelAns.ad_data??>
        <#list modelAns.ad_data as index>
        <tr>
            <td class="tb_td_style name-ng">
                ${index.name}
            </td>

            <td class="tb_td_style">
                <!--                                    ${index.value}-->
                ${index.details}
            </td>
            <td>
                <#if index.state==0>
                <span>不忽略</span>
            </#if>
            <#if index.state==1>
            <span>忽略</span>
        </#if>
        </td>
        <td>
            <#if index.state==0>
            ${index.value}
            <#else>
            -
        </#if>

        </td>
        </tr>
        </#list>
        <#else>
        <tr>
            <td class="center" colspan="3">无
            </td>
        </tr>
    </#if>
    </tbody>
    </table>
</div>




<div class="row col-sm-12" style="padding-left: 30px;padding-right: 30px;"></div>
<div class="col-sm-11 body_size" >
    <div>具体分析：</div>
    <div>
        <#if modelAns.ad_data?? && modelAns.ad_data??>
        <#list modelAns.ad_data as index>
        <#if index.value!=0>
        <#if index.name=="股权冻结" && index.state==0>
        <div> ${index.details},据统计，在企业资本市场违约前夕，该事项出现几率较高，建议密切关注该事项进展。综合考虑，公司风险评价得分 -24</div>
    </#if>
    <#if index.name=="股权冻结" && index.state==1>
    <div> ${index.details},据统计，在企业资本市场违约前夕，该事项出现几率较高，建议密切关注该事项进展。综合考虑，公司风险评价得分不变</div>
</#if>

<#if index.name=="债券市场违约" && index.state==0>
<div>
    ${index.details},说明公司在违约前的经营或信用情况出现了严重问题，且该种情况将对其后续的经营、融资等产生非常不利的影响，建议密切关注该事项进展。综合考虑，公司风险评价得分-56
</div>
</#if>
<#if index.name=="债券市场违约" && index.state==1>
<div>
    ${index.details},说明公司在违约前的经营或信用情况出现了严重问题，且该种情况将对其后续的经营、融资等产生非常不利的影响，建议密切关注该事项进展。综合考虑，公司风险评价得分不变
</div>
</#if>

<#if index.name=="失信被执行人" && index.state==0>
<div>
    ${index.details},说明公司的经营或信用情况出现了较大问题，该种情况将对其后续的经营、融资等产生较大不利影响，建议密切关注该事项进展。综合考虑，公司风险评价得分-24
</div>
</#if>
<#if index.name=="失信被执行人" && index.state==1>
<div>
    ${index.details},说明公司的经营或信用情况出现了较大问题，该种情况将对其后续的经营、融资等产生较大不利影响，建议密切关注该事项进展。综合考虑，公司风险评价得分不变
</div>
</#if>

<#else>
<#if index.name=="股权冻结" && index.state==0>
<div>暂未发现公司在过去6个月内股权曾被冻结情形，公司风险评级得分维持不变</div>
</#if>
<#if index.name=="债券市场违约" && index.state==0>
<div>暂未发现公司存在债券市场违约情形,公司风险评价得分维持不变</div>
</#if>
<#if index.name=="失信被执行人" && index.state==0>
<div>暂未发现公司存在失信被执行人情形,公司风险评价得分维持不变</div>
</#if>
</#if>
</#list>
<#else>
<div></div>
</#if>

</div>

</div>
<!--具体风险点-->

<div class="row col-sm-12 " style="margin-top: 20px;">
    <h3>风险点详情</h3>
</div>
<div class="row" >
    <div class="col-sm-12" style="font-size: 16px;margin-bottom: 10px;margin-top: 10px">
        (1)股权冻结
    </div>
    <div class="col-sm-12 center" style="padding-left: 30px;padding-right: 30px;">
        <table id="equityDong" class="table table-striped table-bordered table-hover" style="page-break-inside: avoid">
            <thead>
            <tr>
                <th>冻结期限自</th>
                <th>冻结期限至</th>
                <th>股权持有方</th>
                <th>执行通知书文号</th>
                <th>股权冻结数额</th>
            </tr>
            </thead>
            <#if 股权冻结?size==0>
                <tr><td colspan="5" class="center">表中无数据</td></tr>
            <#else>
                <#list 股权冻结 as index>
                    <tr>
                        <td>${index.f1}</td>
                        <td>${index.f2}</td>
                        <td>${index.f3}</td>
                        <td>${index.f4}</td>
                        <td>${index.f5}</td>
                    </tr>
                </#list>
            </#if>

        </table>
    </div>

    <div class="col-sm-12" style="font-size: 16px;margin-bottom: 10px;margin-top: 10px">
        (2)债券市场违约
    </div>
    <div class="col-sm-12 center" style="padding-left: 30px;padding-right: 30px;">
        <table id="capitalMarketDefault" class="table table-striped table-bordered table-hover" style="page-break-inside: avoid">
            <thead>
            <tr>
                <th>违约债券</th>
                <th>起息日</th>
                <th>到期日</th>
                <th>违约日期</th>
                <th>违约日余额（亿元）</th>
                <th>当前余额（亿元）</th>
            </tr>
            </thead>
            <#if 资本市场违约?size==0>
                <tr><td colspan="6" class="center">表中无数据</td></tr>
            <#else>
            <#list 资本市场违约 as index>
                <tr>
                    <td>${index.f13}</td>
                    <td>${index.f14}</td>
                    <td>${index.f15}</td>
                    <td>${index.f16}</td>
                    <td>${index.f17}</td>
                    <td>${index.f18}</td>
                </tr>
            </#list>
            </#if>
        </table>
    </div>

    <div class="col-sm-12" style="font-size: 16px;margin-bottom: 10px;margin-top: 10px">
        (3)失信被执行人
    </div>
    <div class="col-sm-12 center" style="padding-left: 30px;padding-right: 30px;">
        <table id="dishonestExecutor" class="table table-striped table-bordered table-hover" style="page-break-inside: avoid">
            <thead>
            <tr>
                <th>发布时间</th>
                <th>立案时间</th>
                <th>案号</th>
                <th>执行依据文号</th>
                <th>行为具体情形</th>
            </tr>
            </thead>
            <#if 失信被执行人?size==0>
                <tr><td colspan="5" class="center">表中无数据</td></tr>
            <#else>
                <#list 失信被执行人 as index>
                    <tr>
                        <td>${index.f8?number?number_to_date}</td>
                        <td>${index.f9?number?number_to_date}</td>
                        <td>${index.f10}</td>
                        <td>${index.f11}</td>
                        <td>${index.f12}</td>
                    </tr>
                </#list>
            </#if>
        </table>
    </div>
</div>







<div   id="page33" class="row col-sm-12 title_style_3" style="margin-top: 10px;margin-left: 10px">
    三.财务分析
</div>
<div class="row" >


    <#if financeAnalysis?exists>
    <#list financeAnalysis?keys as key>
    <div class="col-sm-12">
        <div class="title_style_3" style="margin-bottom: 10px;margin-top: 10px">（${key_index+1}）${key}</div>
    </div>
    <#assign row=financeAnalysis[key]>


    <#if key=="基本财务指标">
    <div class="col-sm-12">
        <table class="table table-striped table-bordered  "  >
            <thead>
            <tr role="row">
                <th class="center" style="width: 12%;"  >财务指标
                </th>
                <#if financeAnalysisYears??>
                <#list financeAnalysisYears as index>
                <th class="center">
                    ${index}
                </th>
            </#list>
            </#if>
            <th class="center">
                行业25%分位值
            </th>
            <th class="center">
                行业50%分位值
            </th>
            <th class="center">
                行业75%分位值
            </th>
            </tr>
            </thead>
            <#if row??>
            <#list row as index>
            <#if key=="基本财务指标">
            <tr>
                <td>
                    ${index.name!"-"}
                </td>
                <#list financeAnalysisYears as indiYear>
                <td>

                    <#if  index["${indiYear}"]??==false ||  index["${indiYear}"]=="" ||index["${indiYear}"]=="-"     >
                    -
                    <#else>
                    ${(index["${indiYear}"]?number)?string(',###.00')!"-"}
                </#if>

                </td>
            </#list>
            <td>

                <#if index.tfPer??==false  ||index.tfPer=="" ||index.tfPer=="-"   >
                -
                <#else>
                ${ (index.tfPer?number)?string(',###.00')!"-"}
            </#if>

            </td>
            <td>

                <#if  index.fiPer??==false  || index.fiPer=="" ||index.fiPer=="-"    >
                -
                <#else>
                ${ (index.fiPer?number)?string(',###.00')!"-"}
            </#if>

            </td>
            <td>
                <#if  index.sfPer??==false  ||index.sfPer=="" ||index.sfPer=="-"    >
                -
                <#else>
                ${(index.sfPer?number)?string(',###.00')!"-"}
            </#if>


            </td>

            </tr>
            <#else>
            <tr>
                <td>
                    ${index.name!"-"}
                </td>
                <#list financeAnalysisYears as indiYear>
                <td>
                    <#if   index["${indiYear}"]??==false || index["${indiYear}"]=="" ||index["${indiYear}"]=="-"     >
                    -
                    <#else>
                    ${(index["${indiYear}"]?number)?string('0.00%')!"-"}
                </#if>

                </td>
            </#list>
            <td>
                <#if  index.tfPer?? ==false || index.tfPer=="" ||index.tfPer=="-"     >
                -
                <#else>
                ${ (index.tfPer?number)?string('0.00%')!"-"}
            </#if>
            </td>
            <td>

                <#if  index.fiPer?? ==false  || index.fiPer=="" ||index.fiPer=="-"      >
                -
                <#else>
                ${ (index.fiPer?number)?string('0.00%')!"-"}
            </#if>

            </td>
            <td>
                <#if  index.sfPer??==false ||  index.sfPer=="" ||index.sfPer=="-"    >
                -
                <#else>
                ${(index.sfPer?number)?string('0.00%')!"-"}
            </#if>


            </td>
            <td>

                <#if  index.avg??==false || index.avg=="" ||index.avg=="-"   >
                -
                <#else>
                ${(index.avg?number)?string('0.00%')!"-"}
            </#if>
            </td>




            </tr>
        </#if>
    </#list>
    <#else>
    <tr>
        <td class="center" colspan="9">无
        </td>
    </tr>
</#if>
</table>
</div>
<#else>
<div class="col-sm-12" style="width:100%;  ">
    <table class="table table-striped table-bordered  "    >
        <thead>
        <tr role="row">
            <th class="center" style="width: 12%;"  >财务指标
            </th>
            <#if financeAnalysisYears??>
            <#list financeAnalysisYears as index>
            <th class="center">
                ${index}
            </th>
        </#list>
        </#if>
        <th class="center">
            行业25%分位值
        </th>
        <th class="center">
            行业50%分位值
        </th>
        <th class="center">
            行业75%分位值
        </th>

        </tr>
        </thead>
        <#if row??>
        <#list row as index>
        <#if key=="基本财务指标">
        <tr>
            <td>
                ${index.name!"-"}
            </td>
            <#list financeAnalysisYears as indiYear>
            <td>
                <#if      index["${indiYear}"]??==false ||  index["${indiYear}"]=="" ||index["${indiYear}"]=="-"   >
                -
                <#else>
                ${(index["${indiYear}"]?number)?string(',##0.00')!"-"}
            </#if>

            </td>
        </#list>
        <td>
            <#if  index.tfPer??==false || index.tfPer=="" ||index.tfPer=="-"   >
            -
            <#else>
            ${ (index.tfPer?number)?string(',##0.00')!"-"}
        </#if>

        </td>
        <td>

            <#if  index.fiPer??==false || index.fiPer=="" ||index.fiPer=="-"   >
            -
            <#else>
            ${ (index.fiPer?number)?string(',##0.00')!"-"}
        </#if>

        </td>
        <td>
            <#if index.sfPer??==false || index.sfPer=="" ||index.sfPer=="-"   >
            -
            <#else>
            ${(index.sfPer?number)?string(',##0.00')!"-"}
        </#if>


        </td>

        </tr>
        <#else>
        <tr>
            <td>
                ${index.name!"-"}
            </td>
            <#list financeAnalysisYears as indiYear>
            <td>
                <#if  index["${indiYear}"]??==false ||   index["${indiYear}"]=="" ||index["${indiYear}"]=="-"   >
                -
                <#else>
                ${(index["${indiYear}"]?number)?string('0.00%')!"-"}
            </#if>

            </td>
        </#list>
        <td>
            <#if  index.tfPer??==false || index.tfPer=="" ||index.tfPer=="-"   >
            -
            <#else>
            ${ (index.tfPer?number)?string('0.00%')!"-"}
        </#if>

        </td>
        <td>

            <#if     index.fiPer??==false || index.fiPer=="" ||index.fiPer=="-"   >
            -
            <#else>
            ${ (index.fiPer?number)?string('0.00%')!"-"}
        </#if>

        </td>
        <td>
            <#if   index.sfPer??==false || index.sfPer=="" ||index.sfPer=="-"   >
            -
            <#else>
            ${(index.sfPer?number)?string('0.00%')!"-"}
        </#if>


        </td>

        </tr>
    </#if>
</#list>
<#else>
<tr>
    <td class="center" colspan="9">无
    </td>
</tr>
</#if>
</table>
</div>
</#if>

</#list>
<#else>
<div class="col-sm-12">
    <div style="font-size: 16px;margin-bottom: 10px;margin-top: 10px">无</div>
</div>
</#if>
</div>