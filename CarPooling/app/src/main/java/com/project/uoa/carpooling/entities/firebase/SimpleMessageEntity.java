package com.project.uoa.carpooling.entities.firebase;

/**
 * Entity used to represent a message for the messenger
 * feature of the application.
 *
 * Created by Chester Booker and Angel Castro on 13/06/2016.
 */
public class SimpleMessageEntity {

    private String text;
    private String name;

    public SimpleMessageEntity() {
    }

    public SimpleMessageEntity(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
