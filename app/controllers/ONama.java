package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.o_nama;

public class ONama extends Controller {

    public static Result ONama() {
        return ok(o_nama.render("O Nama"));
      }
    
}
