package com.edxdn.hmsoon.ui.data;

import java.util.ArrayList;

public class SectionDataModel {

    private String headerTitle;
    private ArrayList<MusicFragmentItemModel> allItemsInSection;

    public SectionDataModel() {
    }

    public SectionDataModel(String headerTitle, ArrayList<MusicFragmentItemModel> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<MusicFragmentItemModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<MusicFragmentItemModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

}
