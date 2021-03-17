package com.akiva.adam.finalproject.classes;

import com.akiva.adam.finalproject.interfaces.ISettings;

/**
 * A class used to set settings for the application
 * (There is only one setting however this is to allow
 * to add more easily)
 */
public class Settings implements ISettings {
    private Boolean notifications;  //default value is true

    /**
     * A constructor for Settings class
     *
     * @param notifications Boolean value indicating whether the user want
     *                      to see notifications
     */
    public Settings(Boolean notifications) {
        this.notifications = notifications;
    }


    /**
     * A default constructor needed for firebase functionality
     */
    public Settings() {
    }

    @Override
    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    @Override
    public Boolean getNotifications() {
        if (notifications != null) {
            return notifications;
        }
        return null;
    }
}
