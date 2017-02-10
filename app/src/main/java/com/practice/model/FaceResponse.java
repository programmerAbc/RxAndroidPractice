package com.practice.model;

/**
 * Created by gaofeng on 2017-02-10.
 */

public class FaceResponse {
    private String requestId;
    private String status;
    private Float confidence;
    private HistoricalSelfie historicalSelfie;
    private Selfie selfie;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }

    public HistoricalSelfie getHistoricalSelfie() {
        return historicalSelfie;
    }

    public void setHistoricalSelfie(HistoricalSelfie historicalSelfie) {
        this.historicalSelfie = historicalSelfie;
    }

    public Selfie getSelfie() {
        return selfie;
    }

    public void setSelfie(Selfie selfie) {
        this.selfie = selfie;
    }

    public static class HistoricalSelfie {

        private String imageId;

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

    }

    public static class Selfie {
        private String imageId;
        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }
    }
}
