<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>
<style>
    .div_title{
        padding-bottom: 5px;
        float:left;
        width:220px;
        text-align:justify
    }

    .div_title span{width:100%;display: inline-block;}

    .div_content{
        padding-bottom: 5px;
        float:left;
        width:60%;
    }

</style>
<!-- 一、智评综述-->
<div style="height: 260px">
    <h2 style="margin-bottom: 30px;font-weight: bold;text-align: center">企业信用评价报告</h2>
    <br>
    <div class="title_style_3" style="display: inline-block">
        <div class="div_title">公司名称：<span></span></div><div class=" div_content ">${data.baseInfo.enterpriseName}</div>
        <div class="div_title">评价年度：<span></span></div><div class=" div_content ">${data.baseInfo.T0}</div>
        <div class="div_title">统一社会信用代码：<span></span></div><div class=" div_content ">${data.baseInfo.enterpriseCode}</div>
        <div class="div_title">报告时间：<span></span></div><div class=" div_content ">${data.baseInfo.time}</div>
        <div class="div_title">所属区域：<span></span></div><div class=" div_content ">${data.baseInfo.regionName}</div>
        <div class="div_title">报告编号：<span></span></div><div class=" div_content ">${data.baseInfo.number}</div>
    </div>
</div>
<div style="clear: both"></div>
<div class=" part ">
    <h3 class="title_style_3">${model_number[model_index]}、区域信用评价综述</h3>
    <#if (data.baseInfo.rank) ??>
    <table class="statement_list">
        <thead>
        <tr>
            <th  >L1</th>
            <th  >L2</th>
            <th  >L3</th>
            <th  >L4</th>
            <th  >L5</th>
            <th  >L6</th>
            <th  >L7</th>
            <th  >L8</th>
            <th  >L9</th>
            <th  >L10</th>
            <th  >L11</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td ><#if data.baseInfo.rank =='L1'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L2'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L3'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L4'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L5'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L6'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L7'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L8'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L9'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L10'><span>✔</span></#if></td>
            <td ><#if data.baseInfo.rank =='L11'><span>✔</span></#if></td>
        </tr>
        <tr>
            <td colspan="4">信用风险得分</td>
            <td colspan="11">${(data.baseInfo.score)}</td>
        </tr>
        </tbody>
    </table>

    <div style="margin-top: 30px;">
        <p style="text-indent:25px">${data.baseInfo.describe}</p>
    </div>
    <#if data.baseInfo.historyRating??>
    <#if data.baseInfo.historyRating.des ??>
    <p style="text-indent:25px">${data.baseInfo.historyRating.des? replace('\n', '<br/>')}</p>
</#if>
</#if>

<#else>
　　<div class="p_style">关键数据不足，无法进行信用评价！</div>
</#if>

</div>