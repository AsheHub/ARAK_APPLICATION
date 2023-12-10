package com.example.doctor_view;
public class Upload {
    private String filename;  // Change this field name to match the key in your database
    private String fileUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    public Upload() {
    }

    public Upload(String filename, String fileUrl) {
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    // Getters and setters
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
