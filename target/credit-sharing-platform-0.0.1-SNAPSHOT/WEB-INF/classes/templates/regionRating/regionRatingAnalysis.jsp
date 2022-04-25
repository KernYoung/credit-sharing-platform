<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>
    <style>
    .padding_5{
        padding: 5px;
    }

    .small_font{
        font-size: 12px!important ;
    }

    .font_blue{
        color: #007AFF;
    }


    </style>
<!-- 二、区域信用评价分析-->
    <div class="part">
        <h3 class="first_level_title">${model_number[model_index]}、区域信用评价分析</h3>
        <#if (data.baseInfo.rank) ??>
            <!-- 区域信用评价分析 表格-->
            <table  class="statement_list">
                <thead>
                    <tr>
                        <th class="small_font" style="width: 17%"></th>
                        <th class="small_font" style="width: 8%">最新指标值</th>
                        <th class="small_font" style="width: 5%">得分</th>
                        <th class="small_font" colspan="5" style="width: 70%">区间/得分</th>
                    </tr>
                </thead>
                <tbody>

                 <#if data.areaRatingAnalysis??>
                    <#list data.areaRatingAnalysis.area?sort_by("score")?reverse as item>
                        <tr>
                            <#if (item.ranges?size % 5 == 0) >
                                <#assign num = item.ranges?size / 5>
                            <#else>
                                <#assign num = (item.ranges?size - (item.ranges?size % 5)) / 5 + 1>
                            </#if>

                            <td class="small_font" rowspan="${num}">${item.name}</td>
                            <td class="small_font" rowspan="${num}">${item.formatValue}</td>
                            <td class="small_font" rowspan="${num}">${item.formatScore}</td>

                            <#list item.ranges as rangeItem>

                                <#if (rangeItem_index > 0) && (rangeItem_index % 5 == 0) ></tr><tr></#if>
                                <td class="small_font <#if item.formatScore == (rangeItem.value ? string["#,###,##0.00"]) >font_blue</#if>">
                                    ${rangeItem.range}<br />得分:${rangeItem.value ? string["#,###,##0.00"]}
                                </td>

                            </#list>

                            <#assign emptyNum = num * 5 - (item.ranges?size) >
                            <#if emptyNum gt 0 >
                                <#list 1..emptyNum as i>
                                    <td></td>
                                </#list>
                            </#if>
                        </tr>
                    </#list>
                 </#if>
                </tbody>
            </table>

            <div style="margin-top: 20px;">备注：</div>
            <div style="padding-left: 30px;margin-bottom: 10px;">
                <div>1.“是否区域内公开发债城投企业有息债务余额为0且一般公共预算收入小于100亿元”指标值若为“1.00”则代表“命中”；指标值若为“0.00”则代表“未命中”;</div>
                <div>2.“融资压力指标”的指标值即被评价区域的一般公共预算收入，指标落入区间及得分的判定以被评价区域的一般公共预算收入作为判定标准;</div>
            </div>

            <#if (data.blackListData) ??>
                <#assign blacklist = data.blackListData>
            </#if>

            <!-- 调整项 -->
            <#if (data.additional) ??>
                <div class="part_avoid">
                    <div style="margin-top: 20px;"></div>
                    <div class="padding_5" style="font-weight: bold">调整项</div>
                    <table class="statement_list">
                        <thead>
                        <tr>
                            <th >调整项</th>
                            <th >详情</th>
                            <th>是否忽略</th>
                            <th >影响</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list data.additional ? keys as key>
                            <#assign additionalResult = data.additional[key]>
                        <tr>
                            <td >${additionalResult.name}</td>
                           <#if (blacklist ?size > 0) >
                                    <td >区域本级的城投企业发生过非标风险事件</td>
                           <#else>
                                     <td >区域本级的城投企业未发生过非标风险事件</td>
                           </#if>
                            <td>${data.blackListScore}</td>
                            <td >${additionalResult.score}</td>
                        </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </#if>

            <#if (blacklist ?size > 0) >
            <div class="part_avoid">
                <div style="margin-top: 35px;"></div>
                <table class="statement_list">
                    <thead>
                    <tr>
                        <th style="width:10%">发布日期</th>
                        <th style="width:10%">公司名称</th>
                        <th style="width:10%">风险类型</th>
                        <th style="width:10%">行政级别</th>
                        <th style="width:10%">所属省份</th>
                        <th style="width:10%">所属地市</th>
                        <th style="width:10%">所属区县</th>
                        <th style="width:30%">详情 </th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list blacklist as item>
                    <tr>
                        <td>${(item.occurrenceTime1)!"-"}</td>
                        <td style="text-align:left;">${(item.enterpriseName)!"-"}</td>
                        <td>${(item.type1)!"-"}</td>
                        <td>${(item.level)!"-"}</td>
                        <td>${(item.province)!"-"}</td>
                        <td>${(item.city)!"-"}</td>
                        <td>${(item.county)!"-"}</td>
                        <td style="text-align:left;"><#if item.content1 ? length gt 80 >${item.content1?substring(0,80)}...<#else>${item.content1!}</#if></td>
                    </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
            </#if>

            <#if data.areaRatingAnalysis ??>
                <!-- 区域信用评价分析 指标释义-->
                <#list data.areaRatingAnalysis.indexAnalysis ? keys as key>
                 <div class="part_avoid">
                    <#assign t0 = data.baseInfo.year? number>
                    <#assign t1 = data.baseInfo.year? number -1>
                    <#assign dataItem = data.areaRatingAnalysis.indexAnalysis[key]>
                     <#if key="key">

                     </#if>
                    <div style="margin-top: 20px;"></div>
                    <div class="padding_5">${key_index + 1}.${key} </div>
                    <table class="statement_list">
                        <thead>
                            <tr>
                                <th>${key}</th>
                                <th>${t1}</th>
                                <th>${t0}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>指标值</td>
                                <td>${dataItem[t1?string].formatValue}</td>
                                <td>${dataItem[t0?string].formatValue}</td>

                            </tr>
                            <tr>
                                <td>得分</td>
                                <td>${dataItem[t1?string].score?string("0.00")}</td>
                                <td>${dataItem[t0?string].score?string("0.00")}</td>

                            </tr>
                        </tbody>
                    </table>
                    <div class="padding_5"><strong>指标释义：</strong>${dataItem.explain}</div>
                    <div class="padding_5"><strong>分析结论：</strong>${dataItem.content}</div>
                </div>

                </#list>
            </#if>
        <#else>
        　　<div class="p_style">关键数据不足，无法进行信用评价！</div>
        </#if>

    </div>
