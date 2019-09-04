package com.zipnoticiasec.ap.app.models;

import java.io.Serializable;

/**
 * Created by Andres on 14/6/2019.
 */

public class UserModel implements Serializable{

    public static int SOCIAL_FACEBOOK = 1;
    public static int SOCIAL_GOOGLE = 2;

    public int id;
    public String nombres;
    public String email;
    public String imagen;
    public int social;
    public String telefono;

    public UserModel() {
    }

    public enum FIELDS {id, nombres, email, imagen, social, telefono}
}
