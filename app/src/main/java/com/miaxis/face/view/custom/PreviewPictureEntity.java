package com.miaxis.face.view.custom;

public class PreviewPictureEntity {

    private   String path;
    private   String base64;

    public PreviewPictureEntity(String path, String base64) {
        this.path = path;
        this.base64 = base64;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    @Override
    public String toString() {
        return "PreviewPictureEntity{" +
                "path='" + path + '\'' +
                ", base64='" + base64 + '\'' +
                '}';
    }
}
