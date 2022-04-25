<div class="title_style_3" style="margin-top:20px">一、信用评价综述</div>
<div id="page1" style="margin-top: 30px">
    <div class="row col-sm-12" style="margin-top: 40px">
        <table class="table table-hover table-bordered">
            <thead>
                <tr >
                    <th  >AAA</th>
                    <th  >AA+</th>
                    <th >AA  </th>
                    <th  >AA-</th>
                    <th  >A+ </th>
                    <th  >A  </th>
                    <th  >A- </th>
                    <th  >BBB+</th>
                    <th  >BBB</th>
                    <th  >BBB- </th>
                    <th  >BB+ </th>
                    <th  >BB  </th>
                    <th  >BB- </th>
                    <th  >B+  </th>
                    <th  >B  </th>
                    <th  >B- </th>
                    <th  >CCC </th>
                    <th  >CC  </th>
                    <th  >C  </th>
                </tr>
            <tbody>
          
    
    
    
            <tr>
                <td >
                    <#if base.finalRank =='AAA'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='AA+'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='AA'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='AA-'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='A+'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='A'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='A-'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='BBB+'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='BBB'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='BBB-'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='BB+'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='BB'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='BB-'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='B+'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
                <td >
                    <#if base.finalRank =='B'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='B-'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
    
    
                <td >
                    <#if base.finalRank =='CCC'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='CC'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
                </td>
                <td >
                    <#if base.finalRank =='C'>
                    <span class="ace-icon fa fa-check"
                          style="color: black;font-size: 20px;vertical-align:middle;text-align: center;"></span></#if>
    
                </td>
            </tr>
    
            <tr class="add-border">
                <td colspan="9" class="center" style="color: black">
                    信用风险得分
                </td>
                <td colspan="10" class="center" style="color: black">
                    <#if base.finalScore??>
                        ${(base.finalScore?number)?string('0.00')}
                    </#if>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div class="row col-sm-12 p_style">
        <div style="color: #333333">
            ${base.名称!"-"}智象风控行业分类为${base.行业名称!"-"}-${base.industryName!"-"}。智象风控对违约企业进行大数据分析，
            通过全方位寻找违约预警因子，对企业进行信用风险评价，给予该主体的${base.ratingYear!"-"}年信用等级为${base.finalRank!"-"}，
            信用得分为${base.finalScore}，该信用等级未来一年的理论违约率为${modelAns.rank_default_rates}%。
        </div>

        <div style="margin-top: 10px;">
            <span style="text-indent: 2em">
               细分来看，智象风控评定${base.名称!"-"}${base.ratingYear!"-"}年量化模型信用等级为${base.Rank}，
                量化模型信用得分为${base.score}。
                <span style="text-indent: 2em">
                <#if modelAns.信用情况??>
                    在非量化风险项方面，本次信用风险评价因企业存在${modelAns.信用情况}，导致最终信用质量下降，建议密切关注该企业以上事项进展。
                </#if>
                </span>

            </span>
            <br>
        </div>
    </div>
    
</div>



