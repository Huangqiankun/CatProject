package com.hqk.catproject.bean;

import java.util.List;

@lombok.NoArgsConstructor
@lombok.Data
public class CatInfo {

    private List<ResultBean> result;
    private Long log_id;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class ResultBean {
        private String name;
        private String score;
    }
}
