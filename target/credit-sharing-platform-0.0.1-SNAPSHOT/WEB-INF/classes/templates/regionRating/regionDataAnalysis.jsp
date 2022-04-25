<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>

<!-- 三、区域数据分析-->
    <div class="part">
        <h3 class="first_level_title">${model_number[model_index]}、区域数据分析</h3>
            <!-- 区域数据分析-->
            <div class="part_avoid">
                <#if data.areaDataAnalysis?? >
                     <#list data.areaDataAnalysis ? keys as key>
                    <!-- 标题 -->
                    <div class="padding_5">${key_index + 1}.${key}</div>
                    <#assign t0 = data.baseInfo.year?number>
                    <#assign t1 = data.baseInfo.year?number -1>
                    <#assign itemList = data.areaDataAnalysis[key]>

                    <!-- 表格 -->
                    <table class="statement_list">
                        <thead>
                            <tr>
                                <th >指标名称</th>
                                <th >${t1}</th>
                                <th >${t0}</th>

                                <#if data.baseInfo.level == "省级">

                                <th >省级区域25%分位值（${t0}年）</th>
                                <th >省级区域50%分位值（${t0}年）</th>
                                <th >省级区域75%分位值（${t0}年）</th>

                                <#elseif data.baseInfo.level == "地市级">

                                <th >地市级区域25%分位值（${t0}年）</th>
                                <th >地市级区域50%分位值（${t0}年）</th>
                                <th >地市级区域75%分位值（${t0}年）</th>

                                <#elseif data.baseInfo.level == "区县级">

                                <th >区县级区域25%分位值（${t0}年）</th>
                                <th >区县级区域50%分位值（${t0}年）</th>
                                <th >区县级区域75%分位值（${t0}年）</th>

                                </#if>
                            </tr>
                        </thead>
                        <tbody>
                            <#list itemList as item>
                                <tr>
                                    <td>${item.name}</td>
                                    <td>${item.t1}</td>
                                    <td>${item.t0}</td>
                                    <td>${item.formatPer25}</td>
                                    <td>${item.formatPer50}</td>
                                    <td>${item.formatPer75}</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>

                    <div style="height: 15px"></div>
                </#list>
                </#if>

            </div>
        <p style="margin-left: 10px;margin-top: 20px;    color: gray;">备注：以上三个表格的区域分位值均以公开发行过城投债区域的相关数据计算得来。</p>
    </div>
