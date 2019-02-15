package com.atex.onecms.app.dam.integration.camel.component.redfact.jwt;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Token request for JWT handling.
 *
 * @author mnova
 */
public class TokenResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("user_nicename")
    private String userNiceName;

    @SerializedName("user_display_name")
    private String userDisplayName;

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(final String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(final String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("token", token)
                      .add("userEmail", userEmail)
                      .add("userNiceName", userNiceName)
                      .add("userDisplayName", userDisplayName)
                      .toString();
    }
}
