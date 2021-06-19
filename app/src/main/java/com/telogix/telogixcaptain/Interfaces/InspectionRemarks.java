package com.telogix.telogixcaptain.Interfaces;

public interface InspectionRemarks  {
    void onYesPressed(int position, boolean isChecked, boolean ignored, boolean checkboxNo);
    void onNoPressed(int position, boolean isChecked, boolean ignored, boolean checkboxYes);
}
