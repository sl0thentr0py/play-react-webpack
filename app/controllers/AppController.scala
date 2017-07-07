package controllers

import play.api.mvc.{Action, Controller}


object AppController extends Controller {

  def index(path: String) = Action {
    implicit request =>
      println("test")
      Ok(views.html.main())
  }

}
