package com.company;

import java.util.ArrayList;
import java.util.List;

public class DataList {
    private List<DataModel> dataModel;

    public DataList() {
        this.dataModel = new ArrayList<>();
    }

    public DataList(List<DataModel> dataModel) {
        this.dataModel = dataModel;
    }

    public List<DataModel> getDataModel() {
        return dataModel;
    }

    public void setDataModel(List<DataModel> dataModel) {
        this.dataModel = dataModel;
    }
}
