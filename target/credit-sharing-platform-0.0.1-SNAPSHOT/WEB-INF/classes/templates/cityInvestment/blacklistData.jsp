<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>

<!-- 四、疑似非标违约-->

    <div class="part">
        <h3 class="title_style_3">${model_number[model_index]}、区域疑似非标风险信息</h3>

        <#assign table = data.blackListData.table1>
        <!-- 本级-->
        <div class="part_avoid">

            <#if (table?size > 0)>
            <div>
            <!-- 表格 -->
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
                    <#list table as item>
                        <tr>
                            <td>${(item.occurrenceTime1)!""}</td>
                            <td style="text-align:left;">${(item.enterpriseName)!""}</td>
                            <td>${(item.type1)!""}</td>
                            <td>${(item.level)!""}</td>
                            <td>${(item.province)!""}</td>
                            <td>${(item.city)!""}</td>
                            <td>${(item.county)!""}</td>
                            <td style="text-align:left;">${item.content1!""}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>

            </div>
            <#else>
                <div class="p_style"><span>无</span></div>
            </#if>
        </div>        　

    </div>
