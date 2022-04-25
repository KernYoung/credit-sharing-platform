<#assign model_index = model_index + 1 />
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>

<style>
    .report_info {
        font-weight: bold;
        /*line-height: 80;*/
        margin-bottom: 40px;
    }
</style>
<!-- 一、智评综述-->
    <div>
        <h2 style="margin-bottom: 30px;font-weight: bold;text-align: center">区域信用评价报告</h2>

        <h3 class="report_info">区域名称：${data.baseInfo.regionName!"-"}</h3>
        <h3 class="report_info">行政级别：${data.baseInfo.level!"-"}</h3>
        <h3 class="report_info">评价年度：${data.baseInfo.year!"-"}</h3>
        <h3 class="report_info">报告编号：${data.baseInfo.number!"-"}</h3>
        <h3 class="report_info">报告时间：${data.baseInfo.time!"-"}</h3>
    </div>

    <div class=" part ">
        <h3 class="first_level_title">${model_number[model_index]}、区域信用评价综述</h3>
        <#if (data.baseInfo.rank) ??>
            <table class="statement_list">
                <thead>
                <tr>
                    <th>L1</th>
                    <th>L2</th>
                    <th>L3</th>
                    <th>L4</th>
                    <th>L5</th>
                    <th>L6</th>
                    <th>L7</th>
                    <th>L8</th>
                    <th>L9</th>
                    <th>L10</th>
                    <th>L11</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td><#if data.baseInfo.rank =='L1'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L2'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L3'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L4'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L5'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L6'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L7'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L8'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L9'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L10'><span>✔</span></#if></td>
                    <td><#if data.baseInfo.rank =='L11'><span>✔</span></#if></td>
                </tr>
                <tr>
                    <td colspan="4">信用风险得分</td>
                    <td colspan="9 "> ${data.baseInfo.score}</td>
                </tr>
                </tbody>
            </table>

            <div style="margin-top: 30px;">
                <p class="p_style"> ${data.baseInfo.describe}</p>
            </div>
        <#else>
        　　
            <div class="p_style">关键数据不足，无法进行信用评价！</div>
        </#if>
    </div>