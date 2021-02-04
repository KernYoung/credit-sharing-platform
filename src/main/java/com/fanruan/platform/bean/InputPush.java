package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "INPUT_PUSH")
public class InputPush {
        @Id
        @Column(name = "ID")
        private Integer id;

        /**
         * 审核人
         */
        @Column(name="USER_NAME")
        private String userName;

        /**
         *
         */
        @Column(name="PUSH_FLAG")
        private Long pushFlag;

        /**
         * 当前系统时间
         */
        @Column(name="PUSH_TIME")
        private String pushTime;

        /**
         *
         */
        @Column(name="RESULT_TYPE")
        private String resultType;

        /**
         *
         */
        @Column(name="RESULT")
        private String result;
        /**
         *
         */
        @Column(name="PROMPTINFO")
        private String  promptinfo;
        /**
         *
         */
        @Column(name="OA_FLAG")
        private String  oa;
        /**
         *
         */
        @Column(name="PUSH_TYPE")
        private String  pushType;
        @Column(name="TYC_UPDATETIME")
        private String  tycUpdatetime;
        /**
         *
         */
        @Column(name="URL")
        private String  url;
        /**
         *
         */
        @Column(name="PROMTINFO")
        private String  promtinfo;
    }
