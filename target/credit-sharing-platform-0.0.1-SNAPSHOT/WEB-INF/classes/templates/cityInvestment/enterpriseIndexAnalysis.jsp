<#assign model_index = model_index + 1>
<#setting datetime_format="yyyy-MM-dd HH:mm:ss"/>

    <style>

        .blue {
            color: #007AFF;
        }

        .flex {
            display: inline-block;
        }

        .flex.j-center {
            justify-content: center;
        }

        .flex.a-center {
            align-items: center;
        }

        .step_block_wrap {
            width: 100%;
            /*margin-top: 34px;*/
        }

        .step_block_item_flex {
            flex: 1
        }

        .step_block_item_flex h4 {
            font-size: 30px;
            text-align: center;
            font-weight: bolder;
        }

        .step_block_item_title {
            width: 100%;
            font-size: 20px;
            font-weight: bolder;
            text-align: center;
        }

        .step_block_item {
            margin: 20px 30px;
            position: relative;
            width: 400px;
            height: 95px;
            background: rgba(255, 255, 255, 1);
            box-shadow: 0px 0px 15px 1px rgba(80, 102, 125, 0.1);
            border-radius: 6px;
            padding: 30px 0px;
        }

        .des p {
            margin-block-start: 0;
            margin-block-end: 0;
            text-indent: 25px;
        }


    </style>

    <!-- 三、企业信用评价分析-->
    <div class="part">
        <h3 class="title_style_3">${model_number[model_index]}、企业信用评价分析</h3>
        <!-- 企业信用评价分析-->
        <div class="flex step_block_wrap j-center">
            <div class="step_block_item flex a-center">
                <div class="step_block_item_flex">
                    <div class="step_block_item_title">企业信用等级</div>
                    <h4 class="blue">${data.baseInfo.rank!"-"}</h4>
                </div>

            </div>
            <div class="step_block_item flex a-center">
                <div class="step_block_item_flex">
                    <div class="step_block_item_title">企业信用得分</div>
                    <h4 class="blue">${data.baseInfo.score!"-"}</h4>
                </div>
            </div>
        </div>

            <#assign  entRatingAnalysis= data.entRatingAnalysis />

        <div>
            <div class="title_style_3">信用评价指标分析</div>
            <table class="statement_list">
                <thead>
                <tr>
                    <th>最新指标值</th>
                    <th>指标值</th>
                    <th>影响</th>
                </tr>
                </thead>
                <tbody>

                <tr>
                    <td>基础信用得分</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["基础信用得分"].index!"-"}</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["基础信用得分"].affect!"-"}</td>
                </tr>
                <tr>
                    <td>基础信用等级</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["基础信用等级"].index!"-"}</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["基础信用等级"].affect!"-"}</td>
                </tr>
                <tr>
                    <td>企业区域地位</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["企业区域地位"].index!"-"}</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["企业区域地位"].affect!"-"}</td>
                </tr>

                        <#if entRatingAnalysis.entIndexAnalysis["营收规模"]??>
                        　　
                        <tr>
                            <td>营收规模</td>
                            <td>${entRatingAnalysis.entIndexAnalysis["营收规模"].index!"-"}</td>
                            <td>${entRatingAnalysis.entIndexAnalysis["营收规模"].affect!"-"}</td>
                        </tr>
                        </#if>

                <tr>
                    <td>应收类资产占比</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["应收类资产占比"].index!"-"}</td>
                    <td rowspan="2">${entRatingAnalysis.entIndexAnalysis["应收类资产占比"].affect!"-"}</td>
                </tr>
                <tr>
                    <td>重资产占比</td>
                    <td>${entRatingAnalysis.entIndexAnalysis["重资产占比"].index!"-"}</td>
                </tr>
                <tr>
                    <td>企业信用评价等级</td>
                    <td colspan="2">${entRatingAnalysis.entRank!"-"}</td>
                </tr>
                <tr>
                    <td>企业信用评价得分</td>
                    <td colspan="2">${entRatingAnalysis.entScore!"-"}</td>
                </tr>
                </tbody>
            </table>
            <div class="title_style_3">细分来看：</div>
            <#if data.entRatingAnalysis.analysisEnt ??>
                <div class="des">${data.entRatingAnalysis.analysisEnt? replace('\n', '<br/>')}</div>
            </#if>
        </div>


    </div>
