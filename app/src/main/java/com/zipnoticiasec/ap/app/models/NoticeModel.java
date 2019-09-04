package com.zipnoticiasec.ap.app.models;

import java.io.Serializable;

/**
 * Created by Andres on 15/6/2019.
 */

public class NoticeModel implements Serializable {

    public int id_noticia;
    public String fecha;
    public String titulo;
    public String intro;
    public String imagen;

    public int idcategoria;
    public String categoria;
    public String tipo;
    public String autor;
    public String ver_autor;
    public String contenido;
    public String fuente;
    public String logo_fuente;
    public String url_fuente;
    public int visitas;
    public String titulo_noticia;

    public NoticeModel() {
    }
}
