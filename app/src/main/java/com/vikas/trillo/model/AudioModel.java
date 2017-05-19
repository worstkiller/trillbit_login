package com.vikas.trillo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class AudioModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private Payload payload;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public class Payload {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user")
        @Expose
        private String user;
        @SerializedName("filename")
        @Expose
        private String filename;
        @SerializedName("created")
        @Expose
        private String created;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }
    }
}
