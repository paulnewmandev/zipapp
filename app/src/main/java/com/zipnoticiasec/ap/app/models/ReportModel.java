package com.zipnoticiasec.ap.app.models;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Andres on 16/6/2019.
 */

public class ReportModel implements Serializable {

    public int id_noticia;
    public String usuario;
    public String imagen_usuario;
    public String fecha;
    public String descripcion;
    public String imagen;
    public String thumb;
    public String ciudad;

    public File video_file; //atributo para guardar los videoa descargados

    public ReportModel() {
    }
}
