<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>

<style>
    .tb_td_title_style {
        max-width: 331px !important;
        width: 20%;
        background-color: white;
        text-align: center;
    }

    .tb_td_style_report {
        width: 238px;
        background-color: white;
        text-align: center;
    }
</style>

<div id="collapseSeven" class=" part ">
    <h3 class="title_style_3">${model_number[model_index]}、报表数据</h3>
    <div class="row">
        <div>
            <div style="margin-left: 20px"><h4>1.资产负债表</h4></div>
            <div class="row">
                <div class="col-sm-12">
                    <div style="text-align:right;margin-right: 30px"><span>单位：元</span></div>
                </div>
            </div>

                        <#assign arrSubjectTitle = ["科 目", "流动资产：","非流动资产：","流动负债：","非流动负债：","所有者权益(或股东权益)：",
                        "经营活动产生的现金流量：", "经营活动产生的现金流量" ,
                        "投资活动产生的现金流量：", "投资活动产生的现金流量",
                        "筹资活动产生的现金流量：", "筹资活动产生的现金流量"]>

                        <#assign invalidCol=["T0","T1","T2","T3","T4"]>

            <!--资产负债表 样式Start-->
            <div id="tab0" class="">
                <table class="statement_list" data-name="资产负债表"
                       id="tableReport0">
                    <tbody>
                                    <#list data.report[0].data as index>

                                    <tr>

                                                     <#assign subjectkey = index.km.val!''>
                                                <#if subjectkey?contains('科 目')>
                                                    <td class="tb_td_title_style"
                                                        style="text-indent: 20px;text-align: left">${index.km.val}</td>
                                                <#elseif arrSubjectTitle?seq_contains(subjectkey)>
                                                    <td class="tb_td_title_style"
                                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;">${index.km.val}</td>
                                                <#else>
                                                    <td class="tb_td_title_style"
                                                        style="text-indent: 30px;font-weight:300;text-align: left; font-size:13px;">${index.km.val}</td>
                                                </#if>

                                                                                <#if data.reportCount gte 5>
                                                                                    <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 30px;font-weight:bold;">${index.value6.val}</td>
                                                                                    <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                                                                    <#else>
                                                <td class="tb_td_style_report">
                                                    ${index.value6.val}
                                                </td>
                                                                                    </#if>
                                                                                </#if>


                                        <#if data.reportCount gte 4>
                                            <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 30px;font-weight:bold;">${index.value5.val}</td>
                                            <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                            <#else>
                                                <td class="tb_td_style_report">
                                                    ${index.value5.val}
                                                </td>
                                            </#if>
                                        </#if>

                                            <#if data.reportCount gte 3>
                                                <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 30px;font-weight:bold;">${index.value4.val}</td>
                                                <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                                <#else>
                                                <td class="tb_td_style_report">
                                                    ${index.value4.val}
                                                </td>
                                                </#if>
                                            </#if>

                                           <#if data.reportCount gte 2>
                                               <#if index_index == 0  ||  subjectkey?contains('科 目') >
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 30px;font-weight:bold;">${index.value3.val}</td>
                                               <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                               <#else>
                                                <td class="tb_td_style_report">
                                                    ${index.value3.val}
                                                </td>
                                               </#if>
                                           </#if>

                                         <#if data.reportCount gte 1>
                                             <#if index_index == 0 || subjectkey?contains('科 目')>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 30px;font-weight:bold;">${index.value2.val}</td>
                                             <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                <td class="tb_td_style_report"
                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                             <#else>
                                                <td class="tb_td_style_report">${index.value2.val}</td>
                                             </#if>
                                         </#if>

                                    </tr>
                                    </#list>
                    </tbody>
                </table>
            </div>
            <!--资产负债表 样式End-->
        </div>
        <!--分页结束-->

        <!--利润表 样式Start-->
        <div style="page-break-before: avoid">
            <div style="margin-left: 20px"><h4>2.利润表</h4></div>
            <div id="tab1">
                <table class="statement_list" data-name="利润表"
                       id="tableReport1">
                    <tbody>
                        <#list data.report[1].data as index>
                        <tr>
                                <#assign subjectkey = index.km.val!''>

                                    <#if subjectkey?contains('科 目')>
                                        <td class="tb_td_title_style"
                                            style="text-indent: 20px;text-align: left">${index.km.val}</td>
                                    <#elseif arrSubjectTitle?seq_contains(subjectkey)>
                                        <td class="tb_td_title_style"
                                            style="text-indent: 20px;text-align: left;background-color: #F2F7FF;">${index.km.val}</td>
                                    <#else>
                                        <td class="tb_td_title_style"
                                            style="text-indent: 30px;font-weight:300; font-size:13px;text-align: left">${index.km.val}</td>
                                    </#if>

                            <#if data.reportCount gte 5>
                                <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                        <td class="tb_td_style_report"
                                            style="text-indent: 30px;font-weight:bold;">${index.value6.val}</td>
                                <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                        <td class="tb_td_style_report"
                                            style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                <#else>
                                        <td class="tb_td_style_report">${index.value6.val}</td>
                                </#if>
                            </#if>

                             <#if data.reportCount gte 4>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 30px;font-weight:bold;">${index.value5.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                    <td class="tb_td_style_report">${index.value5.val}</td>
                                 </#if>
                             </#if>

                             <#if data.reportCount gte 3>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 30px;font-weight:bold;">${index.value4.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                    <td class="tb_td_style_report">${index.value4.val}</td>
                                 </#if>
                             </#if>


                             <#if data.reportCount gte 2>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 30px;font-weight:bold;">${index.value3.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                    <td class="tb_td_style_report">${index.value3.val}</td>
                                 </#if>
                             </#if>

                            <#if data.reportCount gte 1>
                                <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 30px;font-weight:bold;">${index.value2.val}</td>
                                <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                <#else>
                                    <td class="tb_td_style_report">${index.value2.val}</td>
                                </#if>
                            </#if>

                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
            <!--利润表 样式End-->
        </div>

        <div style="page-break-inside: avoid">
            <!--现金流量表 样式Start-->
            <div style="margin-left: 20px"><h4>3.现金流量表</h4></div>
            <div id="tab2" class="">
                <table class="statement_list" data-name="现金流量表"
                       id="tableReport2">
                    <tbody>
                        <#list data.report[2].data  as index>
                        <tr>
                                <#assign subjectkey = index.km.val!''>

                                <#if subjectkey?contains('科 目')>
                                    <td class="tb_td_title_style"
                                        style="text-indent: 20px;text-align: left">${index.km.val}</td>
                                <#elseif arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_title_style"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;">${index.km.val}</td>
                                <#else>
                                    <td class="tb_td_title_style"
                                        style="text-indent: 30px;text-align:left;font-weight:300; font-size:13px;">${index.km.val}</td>
                                </#if>

                            <#if data.reportCount gte 5>
                                <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 30px;font-weight:bold;">${index.value6.val}</td>
                                <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                    <td class="tb_td_style_report"
                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                <#else>
                                    <td class="tb_td_style_report">${index.value6.val}</td>
                                </#if>
                            </#if>

                             <#if data.reportCount gte 4>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                <td class="tb_td_style_report"
                                    style="text-indent: 30px;font-weight:bold;">${index.value5.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                <td class="tb_td_style_report"
                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                <td class="tb_td_style_report">${index.value5.val}</td>
                                 </#if>
                             </#if>


                            <#if data.reportCount gte 3>
                                <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                <td class="tb_td_style_report"
                                    style="text-indent: 30px;font-weight:bold;">${index.value4.val}</td>
                                <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                <td class="tb_td_style_report"
                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                <#else>
                                <td class="tb_td_style_report">${index.value4.val}</td>
                                </#if>
                            </#if>

                             <#if data.reportCount gte 2>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                <td class="tb_td_style_report"
                                    style="text-indent: 30px;font-weight:bold;">${index.value3.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                <td class="tb_td_style_report"
                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                <td class="tb_td_style_report">${index.value3.val}</td>
                                 </#if>
                             </#if>

                             <#if data.reportCount gte 1>
                                 <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                <td class="tb_td_style_report"
                                    style="text-indent: 30px;font-weight:bold;">${index.value2.val}</td>
                                 <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                <td class="tb_td_style_report"
                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                 <#else>
                                <td class="tb_td_style_report">${index.value2.val}</td>
                                 </#if>
                             </#if>

                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
            <!--现金流量表 样式End-->
        </div>


        <div style="page-break-inside: avoid">
            <div style="margin-left: 20px"><h4>4.补充资料</h4></div>
            <table class="statement_list" data-name="补充资料"
                   id="tableReport2">
                         <#list data.report[3].data as index>
                              <tr>
                                  <#assign subjectkey = index.km.val!''>

                                  <#if subjectkey?contains('科 目')>
                                                            <td class="tb_td_title_style"
                                                                style="text-indent: 20px;text-align: left">${index.km.val}</td>
                                  <#elseif arrSubjectTitle?seq_contains(subjectkey)>
                                                            <td class="tb_td_title_style"
                                                                style="text-indent: 20px;text-align: left;background-color: #F2F7FF;">${index.km.val}</td>
                                  <#else>
                                                            <td class="tb_td_title_style"
                                                                style="text-indent: 30px;text-align:left;font-weight:300; font-size:13px;">${index.km.val}</td>
                                  </#if>

                                   <#if data.reportCount gte 5>
                                       <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 30px;font-weight:bold;">${index.value6.val}</td>
                                       <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                       <#else>
                                                    <td class="tb_td_style_report" data-val="${index.value6.val}"
                                                        name="tdNum">
                                                        ${index.value6.val}
                                                    </td>
                                       </#if>
                                   </#if>

                                    <#if data.reportCount gte 4>
                                        <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 30px;font-weight:bold;">${index.value5.val}</td>
                                        <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                        <#else>
                                                    <td class="tb_td_style_report" data-val="${index.value5.val}"
                                                        name="tdNum">
                                                        ${index.value5.val}
                                                    </td>
                                        </#if>
                                    </#if>

                                  <#if data.reportCount gte 3>
                                      <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                                <td class="tb_td_style_report"
                                                                    style="text-indent: 30px;font-weight:bold;">
                                                                    ${index.value4.val!}
                                                                </td>
                                      <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                                <td class="tb_td_style_report"
                                                                    style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                      <#else>
                                                                <td class="tb_td_style_report"
                                                                    data-val="${index.value4.val}"
                                                                    name="tdNum">${index.value4.val!}</td>
                                      </#if>
                                  </#if>

                                  <#if data.reportCount gte 2>
                                      <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 30px;font-weight:bold;">${index.value3.val}</td>
                                      <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                    <td class="tb_td_style_report"
                                                        style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                      <#else>
                                                    <td class="tb_td_style_report" data-val="${index.value3.val}"
                                                        name="tdNum">${index.value3.val}</td>
                                      </#if>
                                  </#if>


                                  <#if data.reportCount gte 1>
                                      <#if index_index == 0  ||  subjectkey?contains('科 目')>
                                                        <td class="tb_td_style_report"
                                                            style="text-indent: 30px;font-weight:bold;">${index.value2.val}</td>
                                      <#elseif index_index != 0 && arrSubjectTitle?seq_contains(subjectkey)>
                                                        <td class="tb_td_style_report"
                                                            style="text-indent: 20px;text-align: left;background-color: #F2F7FF;"></td>
                                      <#else>
                                                        <td class="tb_td_style_report" data-val="${index.value2.val}"
                                                            name="tdNum">${index.value2.val}</td>
                                      </#if>
                                  </#if>

                              </tr>
                         </#list>
                </tbody>
            </table>
        </div>


    </div>
</div>
</div>